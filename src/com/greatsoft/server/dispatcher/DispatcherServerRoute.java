package com.greatsoft.server.dispatcher;

import java.net.ConnectException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.Connector;
import com.greatsoft.transq.core.ConnectorAdapterImp;
import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.core.task.Task;
import com.greatsoft.transq.core.task.TaskQueue;
import com.greatsoft.transq.core.task.TaskQueueHelper;
import com.greatsoft.transq.core.task.TaskQueueManagerImp;
import com.greatsoft.transq.exception.CommunicationException;
import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.utils.CalenderAndString;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;

public class DispatcherServerRoute implements Runnable {
	private static Logger log = Log.getLog(DispatcherServerRoute.class);
	private static Logger userLog = Logger
			.getLogger(ConstantValue.DISPATCHER_USER_LOG);

	private String taskQueueName;
	public static TaskQueue taskQueue;

	private boolean runningFlag;

	public DispatcherServerRoute(String taskQueueName) {
		super();
		this.taskQueueName = taskQueueName;
		this.runningFlag = true;
	}

	public final boolean isRunning() {
		return runningFlag;
	}

	public final void setRunningFlag(boolean runningFlag) {
		this.runningFlag = runningFlag;
	}

	public TaskQueue getTaskQueue() {
		return taskQueue;
	}

	@Override
	public void run() {
		taskQueue = TaskQueueManagerImp.getInstance(
				Config.TASK_QUEUE_CONTAINER_NAME).getTaskQueue(
				Config.TASK_QUEUE_CONTAINER_NAME, this.taskQueueName);
		while (runningFlag && taskQueue == null) {
			log.warn(ErrorCode.getErrorMessage(ErrorCode.TASK_QUEUE_GET_ERROR));
			try {
				Thread.sleep(Config.DISPARTER_PROCESSING_INTERVAL * 10);
			} catch (InterruptedException e) {
				log.error(ErrorCode
						.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
						+ e.getMessage());
			}
			taskQueue = TaskQueueManagerImp.getInstance(
					Config.TASK_QUEUE_CONTAINER_NAME).getTaskQueue(
					Config.TASK_QUEUE_CONTAINER_NAME, this.taskQueueName);
		}
		Task task = null;
		String sendIdentify = null;
		Calendar expiredTime = null;
		while (runningFlag) {
			/** ��Զ����������л�ȥ�����¼ */
			task = getTask(taskQueue);
			while (task == null) {
				log.info("Զ��������ģ��û�д�Զ�����������ȡ�������¼");
				try {
					Thread.sleep(Config.DISPARTER_PROCESSING_INTERVAL * 100);
				} catch (InterruptedException e) {
					log.error(ErrorCode
							.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
							+ e.getMessage());
				}
				// continue;
				task = getTask(taskQueue);
			}
			sendIdentify = task.getSendIdentify();
			log.info("Զ��������ģ���Զ�����������ȡ��һ�������¼��SendIdentify=" + sendIdentify);
			userLog.info("Զ��������ģ���Զ�����������ȡ��һ�������¼��SendIdentify="
					+ sendIdentify);
			expiredTime = CalenderAndString.stringToCalendar(task
					.getExpiredTime());
			if (null == expiredTime) {
				log.error("�����¼�ĳ�ʱʱ���ַ�����" + "expiredTime"
						+ "��ת��ΪCalenderʵ��ʧ��:SendIdentify=" + sendIdentify);
				userLog.error("Զ��������ģ�鴦��һ�������¼ʧ�ܣ�SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
				continue;
			}
			if (Calendar.getInstance().compareTo(expiredTime) > 0) {
				/** ��ʱ,ɾ�������¼����Ӧ��message�ļ����볬ʱ�ļ����� */
				log.error("�����¼��ʱ��SendIdentify=" + sendIdentify);
				userLog.error("Զ��������ģ�鴦��һ�������¼ʧ�ܣ������¼��ʱ��SendIdentify="
						+ sendIdentify);
				ProcessHelper.expiredTimeProcess(taskQueue, task);
				continue;
			} else {
				/** û�г�ʱ�����������¼״̬ */
				if (ProcessHelper.updateTaskStatusToProcessing(taskQueue, task)) {
					/** ���������¼��״̬Ϊ�������С� */
					if (putMessage(task)) {
						/** �����¼״̬���³ɹ���������Ϣ */
						userLog.info("Զ����Ϣ����ģ�鷢��һ����Ϣ�ɹ�: sendIdentify="
								+ sendIdentify);
						ProcessHelper.successedProcess(taskQueue, task);
						continue;
					} else {
						userLog.error("Զ����Ϣ����ģ�鷢��һ����Ϣʧ��: sendIdentify="
								+ sendIdentify);
						ProcessHelper.failedProcess(taskQueue, task);
						continue;
					}
				}
				userLog.error("Զ��������ģ�鴦��һ�������¼ʧ�ܣ�SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
			}
		}
	}

	/** ��Ϣת�� */
	private static boolean putMessage(Task task) {
		String addressString = task.getToAddress();
		String sendIdentify = task.getSendIdentify();
		if (addressString != null) {
			/** �ҵ���һ����ַ��Ӧ��serverAddress��׼����Ϣת�� */
			Address address = Config.addressMap.get(addressString);
			if (null == address) {
				log.error("û���ҵ���Ϣ����һ����ַ��" + addressString
						+ "����Ӧ��serverAddress��SendIdentify=" + sendIdentify);
				return false;
			}
			Connector connector = ConnectorAdapterImp.getInstance()
					.getConnector(address, null);
			if (null == connector) {
				log.error("�������ַ��" + addressString
						+ "��ƥ�����Ϣ�м������connectorʧ�ܣ�SendIdentify=" + sendIdentify);
				return false;
			}
			try {
				connector.connect();
			} catch (ConnectException e1) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.CONNECTION_ERROR)
						+ e1.getMessage());
				log.error("�������ַ��" + addressString
						+ "��ƥ�����Ϣ�м������ʧ�ܣ�SendIdentify=" + sendIdentify);
				return false;
			}
			if (!connector.isConnected()) {
				log.error("����Ϣ�м����������ʧ�ܣ�SendIdentify=" + sendIdentify);
				return false;
			}
			Sender sender = connector.createSender();
			if (null == sender) {
				log.error("��Ϣ�м������senderʧ�ܣ�SendIdentify=" + sendIdentify);
				return false;
			}
			AbstractMessage message = MessageHelper.loadMessage(task);
			/** ��ȡ�����¼��Ӧ����Ϣ */
			if (message == null) {
				log.error("Զ����Ϣ����ģ�飬��ȡ�����¼��Ӧ����Ϣʧ�ܣ�SendIdentify=" + sendIdentify);
				return false;
			}
			ResultImp result = null;
			try {
				result = sender.put(message);
				/** ������Ϣ */
			} catch (CommunicationException e) {
				log.error(ErrorCode
						.getErrorMessage(ErrorCode.PUT_MESSAGE_COMMUNICATION_ERROR));
				connector.disConnect();
				return false;
			}
			if (null == result) {
				log.error(ErrorCode
						.getErrorMessage(ErrorCode.PUT_MESSAGE_COMMUNICATION_ERROR));
				connector.disConnect();
				return false;
			}
			connector.disConnect();
			if (ErrorCode.NO_ERROR == result.getReturnCode()) {
				log.info("Զ����Ϣ����ģ�鷢��һ����Ϣ�ɹ�: sendIdentify=" + sendIdentify);
				return true;
			} else {
				log.error("Զ����Ϣ����ģ�鷢��һ����Ϣʧ��: sendIdentify=" + sendIdentify
						+ "��returnInfo=" + result.getReturnInfo());
				return false;
			}
		}
		return false;
	}

	private static Task getTask(TaskQueue taskQueue) {
		Task task = null;
		try {
			task = TaskQueueHelper.getFirstOne(taskQueue,
					Config.REMOTE_MESSAGE_PROCESS_PRIORITY_MODE);
		} catch (TaskQueueException e1) {
			log.error(ErrorCode.TASK_QUEUE_EXCEPTION_ERROR_INFO
					+ e1.getMessage());
		}
		return task;
	}
}
