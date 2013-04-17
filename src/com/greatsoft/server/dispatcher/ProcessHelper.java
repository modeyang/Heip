package com.greatsoft.server.dispatcher;

import java.net.ConnectException;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Connector;
import com.greatsoft.transq.core.ConnectorAdapterImp;
import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.Envelope;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.core.task.Task;
import com.greatsoft.transq.core.task.TaskQueue;
import com.greatsoft.transq.exception.CommunicationException;
import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.FileHelper;
import com.greatsoft.transq.utils.Log;

public class ProcessHelper {
	private static Logger log = Log.getLog(ProcessHelper.class);

	/**
	 * �����Ϣ����ʧ�ܵ����������޸������¼״̬Ϊ�������жϡ����������¼��Ӧ����Ϣ���������Ϣ����
	 * 
	 * @param taskQueue
	 * @param task
	 * @return null
	 */
	public static void failedProcess(TaskQueue taskQueue, Task task) {
		boolean infoFlag = false;
		boolean errorFlag = false;
		String sendIdentify = task.getSendIdentify();
		String logInfoString = "�����¼sendIdentify=" + sendIdentify + "��";
		String logErrorString = "�����¼sendIdentify=" + sendIdentify + "��";
		task.setStatus(ConstantValue.TASK_INTERRUPT);
		try {
			taskQueue.setTask(task);
			logInfoString += "�޸������¼״̬Ϊ�������жϡ��ɹ���\n";
			infoFlag = true;
		} catch (TaskQueueException e) {
			logErrorString += "�޸������¼״̬Ϊ�������жϡ�ʧ��," + e.getMessage() + "��\n";
			errorFlag = true;
		}
		if (FileHelper.moveFromNewToError(sendIdentify)) {
			logInfoString += "�������¼��Ӧ����Ϣ�ļ����������Ϣ���гɹ�";
			infoFlag = true;
		} else {
			logErrorString += "�������¼��Ӧ����Ϣ�ļ����������Ϣ����ʧ��";
			errorFlag = true;
		}
		if (infoFlag) {
			log.info(logInfoString);
		}
		if (errorFlag) {
			log.error(logErrorString);
		}
	}

	/**
	 * �����Ϣ��¼��ʱ�����������޸������¼״̬Ϊ������ʱ�����������¼��Ӧ����Ϣ���볬ʱ��Ϣ����
	 * 
	 * @param taskQueue
	 * @param task
	 * @return null
	 */
	public static void expiredTimeProcess(TaskQueue taskQueue, Task task) {
		boolean infoFlag = false;
		boolean errorFlag = false;
		String sendIdentify = task.getSendIdentify();
		String logInfoString = "�����¼sendIdentify=" + sendIdentify + "��";
		String logErrorString = "�����¼sendIdentify=" + sendIdentify + "��";
		task.setStatus(ConstantValue.TASK_EXPIRED_TIME);
		try {
			taskQueue.setTask(task);
			logInfoString += "�޸������¼״̬Ϊ������ʱ���ɹ���\n";
			infoFlag = true;
		} catch (TaskQueueException e) {
			logErrorString += "�޸������¼״̬Ϊ������ʱ��ʧ��," + e.getMessage() + "��\n";
			errorFlag = true;
		}
		if (FileHelper.moveFromNewToExpired(sendIdentify)) {
			logInfoString += "�������¼��Ӧ����Ϣ�ļ����볬ʱ��Ϣ���гɹ�";
			infoFlag = true;
		} else {
			logErrorString += "�������¼��Ӧ����Ϣ�ļ����볬ʱ��Ϣ����ʧ��";
			errorFlag = true;
		}
		if (infoFlag) {
			log.info(logInfoString);
		}
		if (errorFlag) {
			log.error(logErrorString);
		}
	}

	/**
	 * ���������¼��״̬Ϊ�������С�������״̬�ɹ��򷵻�true�����������¼��Ӧ����Ϣ�ļ����������Ϣ����
	 * 
	 * @param taskQueue
	 * @param task
	 * @return true or false
	 */
	public static boolean updateTaskStatusToProcessing(TaskQueue taskQueue,
			Task task) {
		String sendIdentify = task.getSendIdentify();
		task.setStatus(ConstantValue.TASK_PROCESSING);
		try {
			taskQueue.setTask(task);
		} catch (TaskQueueException e) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.TASK_QUEUE_EXCEPTION_ERROR)
					+ e.getMessage());
			if (FileHelper.moveFromNewToError(task.getSendIdentify())) {
				log.error("�����¼״̬����Ϊ�������С�ʧ��,����Ӧ����Ϣ�ļ����������Ϣ���гɹ���SendIdentify="
						+ sendIdentify);
			} else {
				log.error("�����¼״̬����Ϊ�������С�ʧ��,����Ӧ����Ϣ�ļ����������Ϣ����ʧ�ܣ�SendIdentify="
						+ sendIdentify);
			}
			return false;
		}
		return true;
	}

	/**
	 * �����Ϣ����ɹ�����������ɾ�������¼���������¼��Ӧ����Ϣ�ļ�ɾ��
	 * 
	 * @param taskQueue
	 * @param task
	 * @return
	 */
	public static void successedProcess(TaskQueue taskQueue, Task task) {
		boolean infoFlag = false;
		boolean errorFlag = false;
		String sendIdentify = task.getSendIdentify();
		String logInfoString = "�����¼sendIdentify=" + sendIdentify + "��";
		String logErrorString = "�����¼sendIdentify=" + sendIdentify + "��";
		try {
			if (taskQueue.deleteTask(task)) {
				logInfoString += "�������¼������ɾ�������¼�ɹ���";
				infoFlag = true;
			} else {
				logErrorString += "�������¼������ɾ�������¼ʧ�ܡ�";
				errorFlag = true;
			}
		} catch (TaskQueueException e) {
			logErrorString += "�������¼������ɾ�������¼ʧ�ܣ�" + e.getMessage() + "��";
			errorFlag = true;
		}
		/*if (FileHelper.deleteFromNew(sendIdentify)) {
			logInfoString += "ɾ�������¼��Ӧ����Ϣ�ļ��ɹ�";
			infoFlag = true;
		} else {
			log.error("ɾ�������¼��Ӧ����Ϣ�ļ�ʧ�ܣ�����Ϣ�ļ��Ƶ��������");
			if (FileHelper.moveFromNewToDelete(sendIdentify)) {
				logInfoString += "����Ϣ�ļ��Ƶ�������гɹ�";
				infoFlag = true;
			} else {
				logErrorString += "����Ϣ�ļ��Ƶ��������ʧ��";
				errorFlag = true;
			}
		}*/
		if (infoFlag) {
			log.info(logInfoString);
		}
		if (errorFlag) {
			log.error(logErrorString);
		}
	}

	public static boolean putReturnMessage(AbstractMessage message) {
		Envelope envelope = message.getEnvelope();
		String sendIdentify = null;
		if (envelope != null) {
			sendIdentify = message.getEnvelope().getSendIdentify();
		} else {
			log.error("��Ϣ��ʽ�����ŷ�ͷΪ��");
			return false;
		}
		if (MessageHelper.putMessageToSend(message)) {
			log.error("����Ϣ�����л���������Ϣ������ʧ��SendIdentify=" + sendIdentify);
			return false;
		}
		Connector connector = ConnectorAdapterImp.getInstance().getConnector(
				Config.localServerAddress[0], null);
		try {
			connector.connect();
		} catch (ConnectException e) {
			log.error("connector�����쳣��ServerAddress="
					+ Config.localServerAddress + e.getMessage());
			return false;
		}
		Sender sender = connector.createSender();
		ResultImp putresult = null;
		try {
			putresult = sender.put(message, Config.HIEP_HOME_PATH
					+ Config.SEND_MESSAGE_FILE_DIRECTORY);
		} catch (CommunicationException e) {
			log.error("��Ϣ�����쳣:SendIdentify=" + sendIdentify + e.getMessage());
			return false;
		} finally {
			if (connector != null) {
				connector.disConnect();
				connector = null;
			}
		}
		if (putresult.getReturnCode() != ErrorCode.NO_ERROR) {
			return false;
		}
		log.info("��Ϣ���ͳɹ�:SendIdentify=" + sendIdentify);
		return true;
	}
}
