package com.greatsoft.server.router;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.CommandProcessor;
import com.greatsoft.transq.core.HiepController;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConfigHelper;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.FileHelper;
import com.greatsoft.transq.utils.Log;

public class RouterMain implements HiepController {

	private static Logger log = Log.getLog(RouterMain.class);

	private static Logger userLog = Logger.getLogger(ConstantValue.ROUTER_USER_LOG);

	private static Object mainThreadLock;

	private static Thread[] routerServerThreads = null;
	private static RouterServer[] routerServers = null;

	private static ThreadGroup threadGroup;

	private static Thread commandProcessorThread;

	private static int monitorSocketPort;

	private static boolean isStopMethodRun;

	public static void main(String[] args) {
		/** ����������Ϣ */
		if (!loadHiepConfig()) {
			System.exit(-1);
		}
		userLog.info("·�ɴ���ģ��������Ϣ���سɹ�");

		/** ��ʼ�����л��� */
		if (!initEnvironment()) {
			System.exit(-1);
		}
		userLog.info("·�ɴ���ģ�黷����ʼ���ɹ�");

		/** �����߳��� */
		if (!startHiep()) {
			System.exit(-1);
		}
		userLog.info("·�ɴ���ģ�������ɹ�");
		synchronized (mainThreadLock) {
			/** ����Ǳ�stop()������notify()���ѣ�������ѭ�� */
			isStopMethodRun = false;
			while (!isStopMethodRun) {
				try {
					mainThreadLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			deInit();
		}
		userLog.info("·�ɴ���ģ��رճɹ�");
		System.exit(0);
	}

	private static void deInit() {
		deletePidFile();
	}

	/** �������������ļ���������Ϣ����Ч�Խ��м�� */
	private static boolean loadHiepConfig() {
		/** ��ȡע�����HIEP_HOME_PATH��ֵ */
		if (!ConfigHelper.loadHiepHomePath()) {
			return false;
		}
		/** �����Ҫ�ıر��ļ�Ŀ¼�������ļ��Ƿ���� */
		if (!ConfigHelper.checkMainDirectory()) {
			return false;
		}
		/** �����Log����֮ǰ�ͳ�ʼ��ʧ�ܣ���ӡ��Ļ��д����ǰ���̵��ļ���Log��̬��ĳ�ʼ��,����׼����־���� */
		if (!ConfigHelper.createLog(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ROUTER_LOG4J_PROPERTIES_FILE_NAME)) {
			return false;
		}
		userLog.info("HIEP_HOME_PATH��ȡ�ɹ�����ҪĿ¼�������ļ����ڣ���־��ʼ���ɹ�");
		/** ����configĿ¼�µ�config.ini�ļ����ݺ�config_custom.ini�ļ����� */
		if (!ConfigHelper.loadConfigFile()) {
			return false;
		}
		userLog.info("config.ini�ļ���config_custom.ini�ļ����سɹ�");

		/** ����configĿ¼�µ�HIEP_routerTable.ini */
		if (!ConfigHelper.loadRouterTableConfig()) {
			return false;
		}
		userLog.info("HIEP_routerTable.ini�ļ����سɹ�");
		/** �������ݿ� */
		if (!ConfigHelper.loadDataBase()) {
			return false;
		}
		userLog.info("�����¼���й����������سɹ�");
		/** ���Ӳ�̿ռ��Ƿ��㹻 */
		if (!ConfigHelper.checkEnvironment()) {
			// return false;
		}
		return true;
	}

	/** ��ʼ��ϵͳ���л��� */
	private static boolean initEnvironment() {
		monitorSocketPort = Config.ROUTER_PORT;
		mainThreadLock = new Object();
		/** ��ֹ�������ظ��������ж�RouterPid.pid�ļ��Ƿ�������RouterPid.pid�ļ����ھͲ�����ϵͳ */
		if (isExistPidFile()) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.PID_FILE_EXIST_ERROR));
			return false;
		}
		/** ����PDI�ļ� */
		if (!createPidFile()) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.CREATE_PID_FILE_ERROR));
			return false;
		}
		return true;
	}

	/** �жϽ��̵�PID�ļ��Ƿ���� */
	private static boolean isExistPidFile() {
		return FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ROUTER_PID_FILE_NAME);
	}

	/** ����PDI�ļ� */
	private static boolean createPidFile() {
		return FileHelper.createFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ROUTER_PID_FILE_NAME);
	}

	/** ɾ��hiep.pid�ļ� */
	private static boolean deletePidFile() {
		return FileHelper.deleteOneFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ROUTER_PID_FILE_NAME);
	}

	/** ����routerServer�߳������������߳� */
	private static boolean startHiep() {
		threadGroup = new ThreadGroup(ConstantValue.THREAD_GROUP_NAME);

		routerServers = new RouterServer[Config.LOCAL_SERVER_ADDRESS_NUM];
		routerServerThreads = new Thread[Config.LOCAL_SERVER_ADDRESS_NUM];

		for (int i = 0; i < Config.LOCAL_SERVER_ADDRESS_NUM; i++) {
			routerServers[i] = new RouterServer(Config.localServerAddress[i],
					Config.TASK_QUEUE_LOCAL_NAME,
					Config.TASK_QUEUE_REMOTE_NAME,
					Config.TASK_QUEUE_CONTAINER_NAME);

			routerServerThreads[i] = new Thread(threadGroup, routerServers[i],
					Config.localServerAddress[i].toString()
							+ ConstantValue.THREAD_NAME);

			routerServerThreads[i].start();

		}

		commandProcessorThread = new Thread(
				threadGroup,
				new RouterCommandProcessor(monitorSocketPort, new RouterMain()),
				ConstantValue.ROUTER_COMMAND_PROCESS_NAME);
		commandProcessorThread.start();

		new Thread(new RouterServerFromSendFile(Config.SEND_MESSAGE_FILE_DIRECTORY,
				Config.TASK_QUEUE_LOCAL_NAME, Config.TASK_QUEUE_REMOTE_NAME,
				Config.TASK_QUEUE_CONTAINER_NAME)).start();

		return true;
	}

	@Override
	public int stop(int timeout) {
		log.info("��ʼ�ر�·�ɴ���ģ�飬��ȴ�");
		/** �ر�routerserver�߳��� **/
		for (int i = 0; i < Config.LOCAL_SERVER_ADDRESS_NUM; i++) {
			routerServers[i].setRunningFlag(false);
		}

		isStopMethodRun = true;
		/** �ر������ݿ������ */
		ConfigHelper.closeDataBase();
		/** ��ʱ�ж�,��ʱ���˲���ʲô�����ͣ����,������������ȡ� */
		if (timeout < 0) {
			log.info("���޳�ʱ�ر�·�ɴ���ģ�飬��ȴ�");
			sleep();
			synchronized (mainThreadLock) {
				mainThreadLock.notifyAll();
			}
			return CommandProcessor.NORMAL_QUIT;
		} else {
			/** �ڹ涨ʱ���ڹر�HIEPϵͳ */
			log.info("��" + (timeout / (1000)) + "���ڹر�·�ɴ���ģ��");
			sleep(timeout);
			log.info("�涨ʱ�䵽����ʼ�ر�·�ɴ���ģ��");
			if (threadGroup.activeCount() != 0) {
				/** ���߳�û��ͣ�� */
				log.info("�̼߳����л���" + threadGroup.activeCount() + "�߳���������");
				synchronized (mainThreadLock) {
					mainThreadLock.notifyAll();
				}
				/** ͨ������ֵ�����������Ľ�� */
				return CommandProcessor.FORCE_QUIT;
			} else {
				/** ���߳��Ѿ�ȫ�������� */
				log.info("�̼߳�����û���߳��������У���ʼ�ر�������");
				synchronized (mainThreadLock) {
					mainThreadLock.notifyAll();
				}
				return CommandProcessor.FORCE_QUIT;
			}
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL * 10);
		} catch (InterruptedException e) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
					+ e.getMessage());
		}
	}

	private static void sleep(int timeout) {
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
					+ e.getMessage());
		}
	}

	@Override
	public String viewThreads() {
		StringBuilder result = new StringBuilder(ConstantValue.NULL_STRING);
		Thread threads[] = new Thread[threadGroup.activeCount()];
		threadGroup.enumerate(threads);
		for (Thread t : threads)
			result.append("·�ɴ���ģ����ŵ��߳�:" + t.getName() + ConstantValue.ENTER);
		return result.toString();
	}

}
