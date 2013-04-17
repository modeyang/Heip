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
	/** 系统日志 */
	private static Logger log = Logger.getLogger(RouterServer.class);
	/** 用户日志 */
	private static Logger userLog = Logger.getLogger(ConstantValue.ROUTER_USER_LOG);
	/** 任务记录队列容器的名字 */
	private String taskQueueContainerName;
	/** 本地任务记录队列的名字 */
	private String taskQueueLocalName;
	/** 远程任务记录队列的名字 */
	private String taskQueueRemoteName;
	/** 本地任务记录队列 */
	private TaskQueue taskQueueLocal = null;
	/** 远程任务记录队列 */
	private TaskQueue taskQueueRemote = null;
	/** 发送消息队列的目录 */
	private String sendFile;
	/** runningFlag变量系统退出的时候使用 */
	private Boolean runningFlag = true;
	/** 留作扩展使用 */
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
		/** 循环从消息队列中取得消息,进行处理. */
		while (runningFlag) {
			message = getOneMessage(sendFile);
			envelope = message.getEnvelope();
			sendIdentify = envelope.getSendIdentify();
			targetAddress = envelope.getTargetAddress();
			userLog.info("路由处理模块接收到一条信息，SendIdentify=" + sendIdentify+ " ，TargetAddress=" + targetAddress);
			if (isexpired(message)) {
				/** 检测消息是否超时 */
				if(!FileHelper.moveToDelete(fileName)){
					log.error("发送队列中的消息文件删除失败"+fileName);
				}
				log.error("发送队列中的消息文件删除成功"+fileName);
				continue;
			}
			targetAddressArray = QueueAddressParser.getInstance().parse(targetAddress);
			if (targetAddressArray == null) {
				/** 信息的目的地址解析错误 */
				log.error(ErrorCode.getErrorMessage(ErrorCode.PARSE_TARGRT_ADDRESS_ERROR));
				userLog.error("路由处理模块处理一条接收到的消息失败，SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(sendIdentify);
				message=null;
				if(!FileHelper.moveToDelete(fileName)){
					log.error("发送队列中的消息文件删除失败"+fileName);
				}
				log.error("发送队列中的消息文件删除成功"+fileName);
				continue;
			}
			RouteGroup[] routerGroupArray = RouterImp.groupByNext(Config.LOCAL_ADDRESS, targetAddressArray);
			if (routerGroupArray == null) {
				/** 信息的目的地址路由解析失败 */
				log.error(ErrorCode.getErrorMessage(ErrorCode.ROUTER_GROUP_ERROR));
				message=null;
				if(!FileHelper.moveToDelete(fileName)){
					log.error("发送队列中的消息文件删除失败"+fileName);
				}
				log.error("发送队列中的消息文件删除成功"+fileName);
				ProcessHelper.failedProcess(sendIdentify);
				userLog.error("路由处理模块处理一条接收到的消息失败，SendIdentify=" + sendIdentify);
				continue;
			}
			int routeGroupArrayLength = routerGroupArray.length;
			for (int i = 0; i < routeGroupArrayLength; i++) {
				/** 每个RouteGroup生成一个新的message，进而生成一个新的任务记录 */
				newMessage(message, routerGroupArray[i], taskQueueLocal,taskQueueRemote);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)+ e.getMessage());
			}
			message=null;
			if(!FileHelper.moveToDelete(fileName)){
				log.error("发送队列中的消息文件删除失败"+fileName);
			}
			log.error("发送队列中的消息文件删除成功"+fileName);
			ProcessHelper.successProcess(sendIdentify);
		}
		this.taskQueueLocal.closeTaskQueue();
		this.taskQueueRemote.closeTaskQueue();

	}

	/**
	 * 根据receiver从消息中间件获取一条信息
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
				log.info("从发送队列获取到一条消息的文件名为：" + fileName1);
				message = MessageHelper.getMessage(fileName1);
				fileName=fileName1;
				if ( null!=message){
					MessageHelper.putMessageToReceived(message);
					return message;
				} else {
					if(!FileHelper.deleteOneFile(fileName1)){
						if(!FileHelper.moveToDelete(fileName1)){
							log.error("发送队列中的消息文件删除失败"+fileName);
						}
						log.error("发送队列中的消息文件删除成功"+fileName);
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
				log.error("消息中出现未知的目的地址，SendIdentify=" + sendIdentify);
				userLog.error("路由处理模块处理一条接收到的消息失败，SendIdentify=" + sendIdentify);
			} else if (nextHop.equals(Config.LOCAL_ADDRESS)) {
				/** 下一跳地址是本地节点，即要将信息发送到本地数据处理部门 */
				String[] newTargetAddressArray = routerGroup
						.getGroupedTargetAddress();
				int newTargetAddressArrayCount = newTargetAddressArray.length;
				for (int index = 0; index < newTargetAddressArrayCount; index++) {
					putNewMessageAndNewTask(message, nextHop,
							newTargetAddressArray[index], ConstantValue.LOCAL,
							taskQueueLocal);
				}
			} else {
				/** 下一跳地址是其他节点 */
				putNewMessageAndNewTask(message, nextHop,
						routerGroup.getTargetAddress(), ConstantValue.REMOTE,
						taskQueueRemote);
			}
		}
		return;
	}

	/**
	 * 生成新的消息和对应的任务记录，将生成的新的消息放入新信息队列中，对应的任务记录放入对应的任务记录队列中
	 * 
	 * @param message
	 *            接收到的消息
	 * @param nextHop
	 *            新消息的下一跳地址
	 * @param targetAddress
	 *            新消息的目的地址
	 * @param type
	 *            新消息的类型：本地消息或者远程消息
	 * @param taskQueue
	 *            任务记录需要存放的任务记录队列
	 */
	private static void putNewMessageAndNewTask(AbstractMessage message,
			String nextHop, String targetAddress, String type,
			TaskQueue taskQueue) {
		AbstractMessage newMessage = MessageHelper
				.getNewMessage(message, targetAddress);
		Envelope newEnvelope = newMessage.getEnvelope();
		String newSendIdentify = newEnvelope.getSendIdentify();
		String sendIdentify = message.getEnvelope().getSendIdentify();
		userLog.info("路由模块接收到的消息(SendIdentify=" + sendIdentify
				+ ")经过路由解析生成一条新的" + type + "消息:SendIdentify=" + newSendIdentify
				+ ", TargetAddress=" + targetAddress);
		/** 将生成的newMessage落地，落写循环策略，控制磁盘文件和落地同步 */
		if (!MessageHelper.putMessageToNew(newMessage)) {
			/** newMessage落地失败 */
			log.error(type + "消息存储到新消息队列中失败:SendIdentify=" + newSendIdentify);
			userLog.error("路由模块处理一条新的" + type + "消息失败:SendIdentify="
					+ newSendIdentify);
			return;
		}
		log.info("" + type + "消息存储到新消息队列中成功:SendIdentify=" + newSendIdentify);
		Task task = getNewTask(newEnvelope, nextHop,type);
		if (!putTask(task, taskQueue)) {
			log.error("任务记录放入" + type + "任务记录队列失败:SendIdentify=："
					+ newSendIdentify);
			userLog.error("路由模块处理一条新的" + type + "消息失败:SendIdentify="
					+ newSendIdentify);
			return;
		}
		userLog.error("路由模块处理一条新的" + type + "消息成功:SendIdentify="
				+ newSendIdentify);
		return;
	}

	/**
	 * 检测消息是否超时,超时返回true，否则返回false
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
			/** 信息的超时时间字段错误，对应的message放入错误消息队列 */
			log.error("信息的超时时间字符串“" + "expiredTime"
					+ "”转换为Calender实例失败:SendIdentify=" + sendIdentify);
			userLog.error("路由处理模块处理一条任务记录失败，SendIdentify=" + sendIdentify);
			message=null;
			ProcessHelper.failedProcess(sendIdentify);
			return true;
		} else if (Calendar.getInstance().compareTo(expiredTime) > 0) {
			/** 超时，对应的message放入超时消息队列 */
			log.error("消息超时：SendIdentify=" + sendIdentify);
			userLog.error("路由处理模块处理一条任务记录失败，消息超时，SendIdentify=" + sendIdentify);
			message=null;
			ProcessHelper.expiredProcess(sendIdentify);
			return true;
		}
		return false;
	}

	/**
	 * 将任务记录task放入任务记录队列taskQueue中。 如果成功，返回true。
	 * 如果失败，将对应的信息文件从新消息队列移动错误信息队列中，返回false。
	 * 
	 * @param task
	 *            任务记录
	 * @param taskQueue
	 *            任务记录队列
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
	 * 根据任务记录队列的名称和任务记录队列的名称，返回任务记录队列实例
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
	 * 根据信封头信息和下一跳地址生成新的任务记录
	 * 
	 * @param envelope
	 *            信封头信息
	 * @param nextHop
	 *            下一跳地址
	 * @param type 
	 * @return task 任务记录
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
