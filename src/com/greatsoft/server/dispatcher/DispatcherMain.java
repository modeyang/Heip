package com.greatsoft.server.dispatcher;

import org.apache.log4j.Logger;
import com.greatsoft.transq.core.CommandProcessor;
import com.greatsoft.transq.core.HiepController;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConfigHelper;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.FileHelper;
import com.greatsoft.transq.utils.Log;

public class DispatcherMain implements HiepController {
	private static Logger log = Log.getLog(DispatcherMain.class);
	private static Logger userLog = Logger
			.getLogger(ConstantValue.DISPATCHER_USER_LOG);

	private static Object mainThreadLock;

	private static DispatcherCommandProcessor commandProcessor;
	private static DispatcherServerProcess dispatcherServerProcess;
	private static DispatcherServerRoute dispatcherServerRoute;
	private static DeleteFileWorker deleteFileWorker;
	/** �߳��� */
	private static ThreadGroup threadGroup;
	/** dispatcherServerProcess�߳� */
	private static Thread dispatcherServerProcessThread;
	/** dispatcherServerRoute�߳� */
	private static Thread dispatcherServerRouteThread;
	/** commandProcessor�߳� */
	private static Thread commandProcessorThread;
	/** deleteFileWorker�߳� */
	private static Thread deleteFileWorkerThread;
	/** monitorSocket�˿� */
	private static int monitorSocketPort;

	private static boolean isStopMethodRun;

	public static void main(String[] args) {
		/** ����������Ϣ */
		if (!loadHiepConfig()) {
			System.exit(-1);
		}
		userLog.info("��Ϣת��ģ��������Ϣ���سɹ�");
		/** ��ʼ�����л��� */
		if (!initEnvironment()) {
			System.exit(-1);
		}
		userLog.info("��Ϣת��ģ�黷����ʼ���ɹ�");
		/** �����߳��� */
		if (!startHiep()) {
			System.exit(-1);
		}
		userLog.info("��Ϣת��ģ�������ɹ�");
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
			destory();
			userLog.info("��Ϣת��ģ��رճɹ�");
		}
		System.exit(0);
	}

	private static void destory() {
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
				+ ConstantValue.DISPARTER_LOG4J_PROPERTIES_FILE_NAME)) {
			return false;
		}
		userLog.info("HIEP_HOME_PATH��ȡ�ɹ�����ҪĿ¼�������ļ����ڣ���־��ʼ���ɹ�");
		/** ����configĿ¼�µ�config.ini�ļ����ݺ�config_custom.ini�ļ����� */
		if (!ConfigHelper.loadConfigFile()) {
			return false;
		}
		userLog.info("config.ini�ļ���config_custom.ini�ļ����سɹ�");

		/** ����configĿ¼�µ�HIEP_addressMap.ini */
		if (!ConfigHelper.loadAddressMapConfig()) {
			return false;
		}
		userLog.info("HIEP_addressMap.ini�ļ����سɹ�");

		/** ����configĿ¼�µ�HIEP_processorMap.ini */
		if (!ConfigHelper.loadProcessorMapConfig()) {
			return false;
		}
		userLog.info("HIEP_processorMap.ini�ļ����سɹ�");
		/** �������ݿ� */
		if (!ConfigHelper.loadDataBase()) {
			return false;
		}
		userLog.info("�����¼���й����������سɹ�");
		/** EXTĿ¼�µ�����JAR */
		if (!ConfigHelper.loadThirdJar()) {
			return false;
		}
		userLog.info("EXTĿ¼�µ�����JAR���سɹ�");
		/** ���Ӳ�̿ռ��Ƿ��㹻 */
		if (!ConfigHelper.checkEnvironment()) {
			return false;
		}
		return true;
	}

	private static boolean initEnvironment() {
		monitorSocketPort = Config.DISPATCHER_PORT;
		mainThreadLock = new Object();
		/** ��ֹ�������ظ��������ж�dispatcher.pid�ļ��Ƿ����,���dispatcher.pid�ļ����ھͲ�����ϵͳ */
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

	private static boolean startHiep() {
		/**
		 * ����������router,dispatcher��Ȼ��dispatcher���������߳�dispatcherServerProcess��
		 * dispatcherServerRoute
		 */
		threadGroup = new ThreadGroup(ConstantValue.threadGroupName);

		dispatcherServerProcess = new DispatcherServerProcess(
				Config.TASK_QUEUE_LOCAL_NAME);
		dispatcherServerRoute = new DispatcherServerRoute(
				Config.TASK_QUEUE_REMOTE_NAME);
		commandProcessor = new DispatcherCommandProcessor(monitorSocketPort,
				new DispatcherMain());
		deleteFileWorker = new DeleteFileWorker(true);

		dispatcherServerProcessThread = new Thread(threadGroup,
				dispatcherServerProcess,
				ConstantValue.DISPATCHER_SERVER_PROCESS_NAME);
		dispatcherServerRouteThread = new Thread(threadGroup,
				dispatcherServerRoute,
				ConstantValue.DISPATCHER_SERVER_ROUTE_NAME);
		commandProcessorThread = new Thread(threadGroup, commandProcessor,
				ConstantValue.DISPATCHER_COMMAND_PROCESSOR_NAME);
		deleteFileWorkerThread = new Thread(threadGroup, deleteFileWorker,
				ConstantValue.DELETE_FILE_WORKER_NAME);

		dispatcherServerProcessThread.start();
		dispatcherServerRouteThread.start();
		commandProcessorThread.start();
		//deleteFileWorkerThread.start();

		return true;
	}

	@Override
	public int stop(int timeout) {
		log.info("��ʼ�ر���Ϣת��ģ�飬��ȴ�");
		dispatcherServerProcess.setRunningFlag(false);
		dispatcherServerRoute.setRunningFlag(false);
		deleteFileWorker.setRunning(false);

		isStopMethodRun = true;
		dispatcherServerProcess.pool.shutdown();
		/** �ر������ݿ������ */
		ConfigHelper.closeDataBase();
		dispatcherServerProcess.getTaskQueue().closeTaskQueue();
		dispatcherServerRoute.getTaskQueue().closeTaskQueue();
		/** ��ʱ�ж�,��ʱ���˲���ʲô�����ͣ����,������������ȡ� */
		if (timeout < 0) {
			log.info("���޳�ʱ�ر���Ϣת��ģ�飬��ȴ�");
			/** ���޵ȴ� */
			sleep();
			while (!dispatcherServerProcess.pool.isTerminated()) {
				sleep();
			}
			if (threadGroup.activeCount() != 0) {
				/** ���߳�û��ͣ�� */
				log.info("�̼߳����л����߳�������");
				try {
					dispatcherServerProcessThread.join(timeout);
					dispatcherServerRouteThread.join(timeout);
					commandProcessorThread.join(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (mainThreadLock) {
					mainThreadLock.notifyAll();
				}
				/** ͨ������ֵ�����������Ľ�� */
				return CommandProcessor.FORCE_QUIT;
			} else {
				/** ���߳��Ѿ�ȫ�������� */
				log.info("���߳�ȫ�������ˣ��ر�������");

				synchronized (mainThreadLock) {
					mainThreadLock.notifyAll();
				}
				return CommandProcessor.NORMAL_QUIT;
			}

		} else {
			/** �ڹ涨ʱ���ڹر�HIEPϵͳ */
			log.info("��" + (timeout / (1000)) + "���ڹر�disPatcher����");
			sleep(timeout);
			log.info("�涨ʱ�䵽����ʼ�ر�HIEPϵͳ");
			killPoolThread();
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

	private static void killPoolThread() {
		if (!dispatcherServerProcess.pool.isTerminated()) {
			log.info("��ʱʱ�䵽���̳߳����ӽ��̻����߳�������,ǿ�ƹر��̳߳��������ӽ���");
			dispatcherServerProcess.pool.shutdownNow();
			sleep();
			if (!dispatcherServerProcess.pool.isTerminated()) {
				log.info("�̳߳����ӽ����Ѿ��ر�");
			} else {
				while (!dispatcherServerProcess.pool.isTerminated()) {
					sleep();
					dispatcherServerProcess.pool.shutdownNow();
					sleep();
				}
			}
		} else {
			log.info("��ʱʱ�䵽���̳߳��������ӽ����Ѿ��ر�");
		}
	}

	private static void sleep() {
		try {
			Thread.sleep(Config.DISPARTER_PROCESSING_INTERVAL * 10);
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

	/** �жϽ��̵�PID�ļ��Ƿ���� */
	private static boolean isExistPidFile() {
		return FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPATCHER_PID_FILE_NAME);
	}

	/** ����PDI�ļ� */
	private static boolean createPidFile() {
		return FileHelper.createFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPATCHER_PID_FILE_NAME);
	}

	/** ɾ��hiep.pid�ļ� */
	private static boolean deletePidFile() {
		return FileHelper.deleteOneFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPATCHER_PID_FILE_NAME);
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
