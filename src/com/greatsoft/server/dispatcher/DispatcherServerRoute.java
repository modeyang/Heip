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
			/** 从远程任务队列中回去任务记录 */
			task = getTask(taskQueue);
			while (task == null) {
				log.info("远程任务处理模块没有从远程任务队列中取到任务记录");
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
			log.info("远程任务处理模块从远程任务队列中取到一条任务记录，SendIdentify=" + sendIdentify);
			userLog.info("远程任务处理模块从远程任务队列中取到一条任务记录，SendIdentify="
					+ sendIdentify);
			expiredTime = CalenderAndString.stringToCalendar(task
					.getExpiredTime());
			if (null == expiredTime) {
				log.error("任务记录的超时时间字符串“" + "expiredTime"
						+ "”转换为Calender实例失败:SendIdentify=" + sendIdentify);
				userLog.error("远程任务处理模块处理一条任务记录失败，SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
				continue;
			}
			if (Calendar.getInstance().compareTo(expiredTime) > 0) {
				/** 超时,删除任务记录，对应的message文件放入超时文件队列 */
				log.error("任务记录超时：SendIdentify=" + sendIdentify);
				userLog.error("远程任务处理模块处理一条任务记录失败，任务记录超时，SendIdentify="
						+ sendIdentify);
				ProcessHelper.expiredTimeProcess(taskQueue, task);
				continue;
			} else {
				/** 没有超时，更新任务记录状态 */
				if (ProcessHelper.updateTaskStatusToProcessing(taskQueue, task)) {
					/** 更新任务记录的状态为“处理中” */
					if (putMessage(task)) {
						/** 任务记录状态更新成功，发送信息 */
						userLog.info("远程消息处理模块发送一条信息成功: sendIdentify="
								+ sendIdentify);
						ProcessHelper.successedProcess(taskQueue, task);
						continue;
					} else {
						userLog.error("远程消息处理模块发送一条信息失败: sendIdentify="
								+ sendIdentify);
						ProcessHelper.failedProcess(taskQueue, task);
						continue;
					}
				}
				userLog.error("远程任务处理模块处理一条任务记录失败：SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
			}
		}
	}

	/** 消息转发 */
	private static boolean putMessage(Task task) {
		String addressString = task.getToAddress();
		String sendIdentify = task.getSendIdentify();
		if (addressString != null) {
			/** 找到下一跳地址对应的serverAddress，准备信息转发 */
			Address address = Config.addressMap.get(addressString);
			if (null == address) {
				log.error("没有找到信息的下一跳地址“" + addressString
						+ "”对应的serverAddress：SendIdentify=" + sendIdentify);
				return false;
			}
			Connector connector = ConnectorAdapterImp.getInstance()
					.getConnector(address, null);
			if (null == connector) {
				log.error("创建与地址“" + addressString
						+ "”匹配的消息中间件连接connector失败：SendIdentify=" + sendIdentify);
				return false;
			}
			try {
				connector.connect();
			} catch (ConnectException e1) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.CONNECTION_ERROR)
						+ e1.getMessage());
				log.error("创建与地址“" + addressString
						+ "”匹配的消息中间件连接失败：SendIdentify=" + sendIdentify);
				return false;
			}
			if (!connector.isConnected()) {
				log.error("与消息中间件建立连接失败：SendIdentify=" + sendIdentify);
				return false;
			}
			Sender sender = connector.createSender();
			if (null == sender) {
				log.error("消息中间件创建sender失败：SendIdentify=" + sendIdentify);
				return false;
			}
			AbstractMessage message = MessageHelper.loadMessage(task);
			/** 获取任务记录对应的信息 */
			if (message == null) {
				log.error("远程消息处理模块，获取任务记录对应的信息失败：SendIdentify=" + sendIdentify);
				return false;
			}
			ResultImp result = null;
			try {
				result = sender.put(message);
				/** 发送消息 */
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
				log.info("远程消息处理模块发送一条信息成功: sendIdentify=" + sendIdentify);
				return true;
			} else {
				log.error("远程消息处理模块发送一条信息失败: sendIdentify=" + sendIdentify
						+ "，returnInfo=" + result.getReturnInfo());
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
