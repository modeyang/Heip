package com.greatsoft.server.router;

import java.io.File;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.QueueAddressParser;
import com.greatsoft.transq.core.RouteGroup;
import com.greatsoft.transq.core.RouterImp;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.Envelope;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.task.Task;
import com.greatsoft.transq.core.task.TaskHelper;
import com.greatsoft.transq.core.task.TaskQueue;
import com.greatsoft.transq.core.task.TaskQueueManagerImp;
import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.utils.CalenderAndString;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.FileHelper;

public class RouterServerFromSendFile implements Runnable {
	/** ϵͳ��־ */
	private static Logger log = Logger.getLogger(RouterServer.class);
	/** �û���־ */
	private static Logger userLog = Logger.getLogger(ConstantValue.ROUTER_USER_LOG);
	/** �����¼�������������� */
	private String taskQueueContainerName;
	/** ���������¼���е����� */
	private String taskQueueLocalName;
	/** Զ�������¼���е����� */
	private String taskQueueRemoteName;
	/** ���������¼���� */
	private TaskQueue taskQueueLocal = null;
	/** Զ�������¼���� */
	private TaskQueue taskQueueRemote = null;
	/** ������Ϣ���е�Ŀ¼ */
	private String sendFile;
	/** runningFlag����ϵͳ�˳���ʱ��ʹ�� */
	private Boolean runningFlag = true;
	/** ������չʹ�� */
	private Object options;
	
	private static String fileName="";

	public RouterServerFromSendFile(String sendFile, String taskQueueLocalName,
			String taskQueueRemoteName, String taskQueueContainerName) {
		super();
		this.sendFile = sendFile;
		this.taskQueueLocalName = taskQueueLocalName;
		this.taskQueueRemoteName = taskQueueRemoteName;
		this.taskQueueContainerName = taskQueueContainerName;
	}

	public final Boolean isRunningFlag() {
		return runningFlag;
	}

	public final void setRunningFlag(Boolean runningFlag) {
		this.runningFlag = runningFlag;
	}

	@Override
	public void run() {
		taskQueueLocal = getTaskQueue(taskQueueLocalName,
				taskQueueContainerName);
		taskQueueRemote = getTaskQueue(taskQueueRemoteName,
				taskQueueContainerName);
		options = loadConnectionOptions();
		AbstractMessage message = null;
		Envelope envelope = null;
		Address[] targetAddressArray = null;
		String sendIdentify = null;
		String targetAddress = null;
		/** ѭ������Ϣ������ȡ����Ϣ,���д���. */
		while (runningFlag) {
			message = getOneMessage(sendFile);
			envelope = message.getEnvelope();
			sendIdentify = envelope.getSendIdentify();
			targetAddress = envelope.getTargetAddress();
			userLog.info("·�ɴ���ģ����յ�һ����Ϣ��SendIdentify=" + sendIdentify+ " ��TargetAddress=" + targetAddress);
			if (isexpired(message)) {
				/** �����Ϣ�Ƿ�ʱ */
				if(!FileHelper.moveToDelete(fileName)){
					log.error("���Ͷ����е���Ϣ�ļ�ɾ��ʧ��"+fileName);
				}
				log.error("���Ͷ����е���Ϣ�ļ�ɾ���ɹ�"+fileName);
				continue;
			}
			targetAddressArray = QueueAddressParser.getInstance().parse(targetAddress);
			if (targetAddressArray == null) {
				/** ��Ϣ��Ŀ�ĵ�ַ�������� */
				log.error(ErrorCode.getErrorMessage(ErrorCode.PARSE_TARGRT_ADDRESS_ERROR));
				userLog.error("·�ɴ���ģ�鴦��һ�����յ�����Ϣʧ�ܣ�SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(sendIdentify);
				message=null;
				if(!FileHelper.moveToDelete(fileName)){
					log.error("���Ͷ����е���Ϣ�ļ�ɾ��ʧ��"+fileName);
				}
				log.error("���Ͷ����е���Ϣ�ļ�ɾ���ɹ�"+fileName);
				continue;
			}
			RouteGroup[] routerGroupArray = RouterImp.groupByNext(Config.LOCAL_ADDRESS, targetAddressArray);
			if (routerGroupArray == null) {
				/** ��Ϣ��Ŀ�ĵ�ַ·�ɽ���ʧ�� */
				log.error(ErrorCode.getErrorMessage(ErrorCode.ROUTER_GROUP_ERROR));
				message=null;
				if(!FileHelper.moveToDelete(fileName)){
					log.error("���Ͷ����е���Ϣ�ļ�ɾ��ʧ��"+fileName);
				}
				log.error("���Ͷ����е���Ϣ�ļ�ɾ���ɹ�"+fileName);
				ProcessHelper.failedProcess(sendIdentify);
				userLog.error("·�ɴ���ģ�鴦��һ�����յ�����Ϣʧ�ܣ�SendIdentify=" + sendIdentify);
				continue;
			}
			int routeGroupArrayLength = routerGroupArray.length;
			for (int i = 0; i < routeGroupArrayLength; i++) {
				/** ÿ��RouteGroup����һ���µ�message����������һ���µ������¼ */
				newMessage(message, routerGroupArray[i], taskQueueLocal,taskQueueRemote);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)+ e.getMessage());
			}
			message=null;
			if(!FileHelper.moveToDelete(fileName)){
				log.error("���Ͷ����е���Ϣ�ļ�ɾ��ʧ��"+fileName);
			}
			log.error("���Ͷ����е���Ϣ�ļ�ɾ���ɹ�"+fileName);
			ProcessHelper.successProcess(sendIdentify);
		}
		this.taskQueueLocal.closeTaskQueue();
		this.taskQueueRemote.closeTaskQueue();

	}

	/**
	 * ����receiver����Ϣ�м����ȡһ����Ϣ
	 * 
	 * @param sendFile
	 * @return
	 */
	private static AbstractMessage getOneMessage(String sendFile) {
		File sendPath = new File(sendFile);
		AbstractMessage message = null;
		while (message == null) {
			File[] files = sendPath.listFiles();
			if(files==null || files.length==0){
				try {
					Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL*100);
				} catch (InterruptedException e) {
					log.error(ErrorCode
							.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
							+ e.getMessage());
				}
				continue;
			}
			for (int i = 0; i < files.length; i++) {
			String	fileName1 = files[i].getAbsolutePath();
				log.info("�ӷ��Ͷ��л�ȡ��һ����Ϣ���ļ���Ϊ��" + fileName1);
				message = MessageHelper.getMessage(fileName1);
				fileName=fileName1;
				if ( null!=message){
					MessageHelper.putMessageToReceived(message);
					return message;
				} else {
					if(!FileHelper.deleteOneFile(fileName1)){
						if(!FileHelper.moveToDelete(fileName1)){
							log.error("���Ͷ����е���Ϣ�ļ�ɾ��ʧ��"+fileName);
						}
						log.error("���Ͷ����е���Ϣ�ļ�ɾ���ɹ�"+fileName);
					}
					try {
						Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL*100);
					} catch (InterruptedException e) {
						log.error(ErrorCode
								.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)+ e.getMessage());
					}
				}
			}
			try {
				Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL * 100);
			} catch (InterruptedException e) {
				log.error(ErrorCode
						.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
						+ e.getMessage());
			}
		}
		return null;
	}

	private static void newMessage(AbstractMessage message, RouteGroup routerGroup,
			TaskQueue taskQueueLocal, TaskQueue taskQueueRemote) {
		if (routerGroup != null) {
			String nextHop = routerGroup.getNextHop();
			String sendIdentify = message.getEnvelope().getSendIdentify();
			if (nextHop == null || nextHop.equals(ConstantValue.NULL_STRING)) {
				log.error("��Ϣ�г���δ֪��Ŀ�ĵ�ַ��SendIdentify=" + sendIdentify);
				userLog.error("·�ɴ���ģ�鴦��һ�����յ�����Ϣʧ�ܣ�SendIdentify=" + sendIdentify);
			} else if (nextHop.equals(Config.LOCAL_ADDRESS)) {
				/** ��һ����ַ�Ǳ��ؽڵ㣬��Ҫ����Ϣ���͵��������ݴ����� */
				String[] newTargetAddressArray = routerGroup
						.getGroupedTargetAddress();
				int newTargetAddressArrayCount = newTargetAddressArray.length;
				for (int index = 0; index < newTargetAddressArrayCount; index++) {
					putNewMessageAndNewTask(message, nextHop,
							newTargetAddressArray[index], ConstantValue.LOCAL,
							taskQueueLocal);
				}
			} else {
				/** ��һ����ַ�������ڵ� */
				putNewMessageAndNewTask(message, nextHop,
						routerGroup.getTargetAddress(), ConstantValue.REMOTE,
						taskQueueRemote);
			}
		}
		return;
	}

	/**
	 * �����µ���Ϣ�Ͷ�Ӧ�������¼�������ɵ��µ���Ϣ��������Ϣ�����У���Ӧ�������¼�����Ӧ�������¼������
	 * 
	 * @param message
	 *            ���յ�����Ϣ
	 * @param nextHop
	 *            ����Ϣ����һ����ַ
	 * @param targetAddress
	 *            ����Ϣ��Ŀ�ĵ�ַ
	 * @param type
	 *            ����Ϣ�����ͣ�������Ϣ����Զ����Ϣ
	 * @param taskQueue
	 *            �����¼��Ҫ��ŵ������¼����
	 */
	private static void putNewMessageAndNewTask(AbstractMessage message,
			String nextHop, String targetAddress, String type,
			TaskQueue taskQueue) {
		AbstractMessage newMessage = MessageHelper
				.getNewMessage(message, targetAddress);
		Envelope newEnvelope = newMessage.getEnvelope();
		String newSendIdentify = newEnvelope.getSendIdentify();
		String sendIdentify = message.getEnvelope().getSendIdentify();
		userLog.info("·��ģ����յ�����Ϣ(SendIdentify=" + sendIdentify
				+ ")����·�ɽ�������һ���µ�" + type + "��Ϣ:SendIdentify=" + newSendIdentify
				+ ", TargetAddress=" + targetAddress);
		/** �����ɵ�newMessage��أ���дѭ�����ԣ����ƴ����ļ������ͬ�� */
		if (!MessageHelper.putMessageToNew(newMessage)) {
			/** newMessage���ʧ�� */
			log.error(type + "��Ϣ�洢������Ϣ������ʧ��:SendIdentify=" + newSendIdentify);
			userLog.error("·��ģ�鴦��һ���µ�" + type + "��Ϣʧ��:SendIdentify="
					+ newSendIdentify);
			return;
		}
		log.info("" + type + "��Ϣ�洢������Ϣ�����гɹ�:SendIdentify=" + newSendIdentify);
		Task task = getNewTask(newEnvelope, nextHop,type);
		if (!putTask(task, taskQueue)) {
			log.error("�����¼����" + type + "�����¼����ʧ��:SendIdentify=��"
					+ newSendIdentify);
			userLog.error("·��ģ�鴦��һ���µ�" + type + "��Ϣʧ��:SendIdentify="
					+ newSendIdentify);
			return;
		}
		userLog.error("·��ģ�鴦��һ���µ�" + type + "��Ϣ�ɹ�:SendIdentify="
				+ newSendIdentify);
		return;
	}

	/**
	 * �����Ϣ�Ƿ�ʱ,��ʱ����true�����򷵻�false
	 * 
	 * @param message
	 * @return true | false
	 */
	private static boolean isexpired(AbstractMessage message) {
		Envelope envelope = message.getEnvelope();
		String sendIdentify = envelope.getSendIdentify();
		Calendar expiredTime = CalenderAndString.stringToCalendar(envelope
				.getExpiredTime());
		if (null == expiredTime) {
			/** ��Ϣ�ĳ�ʱʱ���ֶδ��󣬶�Ӧ��message���������Ϣ���� */
			log.error("��Ϣ�ĳ�ʱʱ���ַ�����" + "expiredTime"
					+ "��ת��ΪCalenderʵ��ʧ��:SendIdentify=" + sendIdentify);
			userLog.error("·�ɴ���ģ�鴦��һ�������¼ʧ�ܣ�SendIdentify=" + sendIdentify);
			message=null;
			ProcessHelper.failedProcess(sendIdentify);
			return true;
		} else if (Calendar.getInstance().compareTo(expiredTime) > 0) {
			/** ��ʱ����Ӧ��message���볬ʱ��Ϣ���� */
			log.error("��Ϣ��ʱ��SendIdentify=" + sendIdentify);
			userLog.error("·�ɴ���ģ�鴦��һ�������¼ʧ�ܣ���Ϣ��ʱ��SendIdentify=" + sendIdentify);
			message=null;
			ProcessHelper.expiredProcess(sendIdentify);
			return true;
		}
		return false;
	}

	/**
	 * �������¼task���������¼����taskQueue�С� ����ɹ�������true��
	 * ���ʧ�ܣ�����Ӧ����Ϣ�ļ�������Ϣ�����ƶ�������Ϣ�����У�����false��
	 * 
	 * @param task
	 *            �����¼
	 * @param taskQueue
	 *            �����¼����
	 * @param newSendIdentify
	 * @return true | false
	 */
	public static boolean putTask(Task task, TaskQueue taskQueue) {
		String sendIdentify = task.getSendIdentify();
		int putOneTaskResult = ErrorCode.NO_ERROR;
		try {
			putOneTaskResult = taskQueue.putOneTask(task);
		} catch (TaskQueueException e) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.TASK_QUEUE_EXCEPTION_ERROR)
					+ e.getMessage());
			FileHelper.moveFromNewToError(sendIdentify);
			return false;
		}
		if (putOneTaskResult != ErrorCode.NO_ERROR) {
			log.error(ErrorCode.getErrorMessage(putOneTaskResult));
			FileHelper.moveFromNewToError(sendIdentify);
			return false;
		}
		return true;
	}

	/**
	 * ���������¼���е����ƺ������¼���е����ƣ����������¼����ʵ��
	 * 
	 * @param taskQueueName
	 * @param taskQueueContainerName
	 * @return TaskQueue
	 */
	public static TaskQueue getTaskQueue(String taskQueueName,
			String taskQueueContainerName) {
		TaskQueue taskQueue = null;
		while (taskQueue == null) {
			try {
				Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL);
			} catch (InterruptedException e) {
				log.error(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR
						+ e.getMessage());
			}
			taskQueue = TaskQueueManagerImp.getInstance(taskQueueContainerName)
					.getTaskQueue(taskQueueContainerName, taskQueueName);
		}
		return taskQueue;
	}

	/**
	 * �����ŷ�ͷ��Ϣ����һ����ַ�����µ������¼
	 * 
	 * @param envelope
	 *            �ŷ�ͷ��Ϣ
	 * @param nextHop
	 *            ��һ����ַ
	 * @param type 
	 * @return task �����¼
	 */
	public static Task getNewTask(Envelope envelope, String nextHop, String type) {
		if(type==ConstantValue.LOCAL){
			nextHop = envelope.getTargetAddress().split(
					ConstantValue.PARSE_SINGLE_ADDRESS_STRING)[0];
			if (nextHop.equals(ConstantValue.ALL_DEPARTMENT_STRING)) {
				nextHop = Config.LOCAL_ADDRESS;
			}
		}
		return TaskHelper.getNewTak(envelope, nextHop);
	}

	private Object loadConnectionOptions() {
		return options;
	}

}
