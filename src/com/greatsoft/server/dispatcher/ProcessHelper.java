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
	 * 完成消息处理失败的清理工作，修改任务记录状态为“处理中断”，将任务记录对应的信息放入错误信息队列
	 * 
	 * @param taskQueue
	 * @param task
	 * @return null
	 */
	public static void failedProcess(TaskQueue taskQueue, Task task) {
		boolean infoFlag = false;
		boolean errorFlag = false;
		String sendIdentify = task.getSendIdentify();
		String logInfoString = "任务记录sendIdentify=" + sendIdentify + "：";
		String logErrorString = "任务记录sendIdentify=" + sendIdentify + "：";
		task.setStatus(ConstantValue.TASK_INTERRUPT);
		try {
			taskQueue.setTask(task);
			logInfoString += "修改任务记录状态为“处理中断”成功。\n";
			infoFlag = true;
		} catch (TaskQueueException e) {
			logErrorString += "修改任务记录状态为“处理中断”失败," + e.getMessage() + "。\n";
			errorFlag = true;
		}
		if (FileHelper.moveFromNewToError(sendIdentify)) {
			logInfoString += "将任务记录对应的信息文件放入错误信息队列成功";
			infoFlag = true;
		} else {
			logErrorString += "将任务记录对应的信息文件放入错误信息队列失败";
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
	 * 完成消息记录超时的清理工作，修改任务记录状态为“处理超时”，将任务记录对应的信息放入超时信息队列
	 * 
	 * @param taskQueue
	 * @param task
	 * @return null
	 */
	public static void expiredTimeProcess(TaskQueue taskQueue, Task task) {
		boolean infoFlag = false;
		boolean errorFlag = false;
		String sendIdentify = task.getSendIdentify();
		String logInfoString = "任务记录sendIdentify=" + sendIdentify + "：";
		String logErrorString = "任务记录sendIdentify=" + sendIdentify + "：";
		task.setStatus(ConstantValue.TASK_EXPIRED_TIME);
		try {
			taskQueue.setTask(task);
			logInfoString += "修改任务记录状态为“处理超时”成功。\n";
			infoFlag = true;
		} catch (TaskQueueException e) {
			logErrorString += "修改任务记录状态为“处理超时”失败," + e.getMessage() + "。\n";
			errorFlag = true;
		}
		if (FileHelper.moveFromNewToExpired(sendIdentify)) {
			logInfoString += "将任务记录对应的信息文件放入超时信息队列成功";
			infoFlag = true;
		} else {
			logErrorString += "将任务记录对应的信息文件放入超时信息队列失败";
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
	 * 更新任务记录的状态为“处理中”，更新状态成功则返回true，否则将任务记录对应的信息文件放入错误信息队列
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
				log.error("任务记录状态更新为“处理中”失败,将对应的信息文件放入错误信息队列成功：SendIdentify="
						+ sendIdentify);
			} else {
				log.error("任务记录状态更新为“处理中”失败,将对应的信息文件放入错误信息队列失败：SendIdentify="
						+ sendIdentify);
			}
			return false;
		}
		return true;
	}

	/**
	 * 完成消息处理成功的清理工作，删除任务记录，将任务记录对应的信息文件删除
	 * 
	 * @param taskQueue
	 * @param task
	 * @return
	 */
	public static void successedProcess(TaskQueue taskQueue, Task task) {
		boolean infoFlag = false;
		boolean errorFlag = false;
		String sendIdentify = task.getSendIdentify();
		String logInfoString = "任务记录sendIdentify=" + sendIdentify + "：";
		String logErrorString = "任务记录sendIdentify=" + sendIdentify + "：";
		try {
			if (taskQueue.deleteTask(task)) {
				logInfoString += "从任务记录队列中删除任务记录成功。";
				infoFlag = true;
			} else {
				logErrorString += "从任务记录队列中删除任务记录失败。";
				errorFlag = true;
			}
		} catch (TaskQueueException e) {
			logErrorString += "从任务记录队列中删除任务记录失败，" + e.getMessage() + "。";
			errorFlag = true;
		}
		/*if (FileHelper.deleteFromNew(sendIdentify)) {
			logInfoString += "删除任务记录对应的信息文件成功";
			infoFlag = true;
		} else {
			log.error("删除任务记录对应的信息文件失败，将消息文件移到错误队列");
			if (FileHelper.moveFromNewToDelete(sendIdentify)) {
				logInfoString += "将消息文件移到错误队列成功";
				infoFlag = true;
			} else {
				logErrorString += "将消息文件移到错误队列失败";
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
			log.error("消息格式有误，信封头为空");
			return false;
		}
		if (MessageHelper.putMessageToSend(message)) {
			log.error("将消息反序列化到发送消息队列中失败SendIdentify=" + sendIdentify);
			return false;
		}
		Connector connector = ConnectorAdapterImp.getInstance().getConnector(
				Config.localServerAddress[0], null);
		try {
			connector.connect();
		} catch (ConnectException e) {
			log.error("connector连接异常，ServerAddress="
					+ Config.localServerAddress + e.getMessage());
			return false;
		}
		Sender sender = connector.createSender();
		ResultImp putresult = null;
		try {
			putresult = sender.put(message, Config.HIEP_HOME_PATH
					+ Config.SEND_MESSAGE_FILE_DIRECTORY);
		} catch (CommunicationException e) {
			log.error("消息发送异常:SendIdentify=" + sendIdentify + e.getMessage());
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
		log.info("消息发送成功:SendIdentify=" + sendIdentify);
		return true;
	}
}
