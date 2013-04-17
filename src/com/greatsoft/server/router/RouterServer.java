package com.greatsoft.server.router;

import java.net.ConnectException;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.Connector;
import com.greatsoft.transq.core.ConnectorAdapterImp;
import com.greatsoft.transq.core.QueueAddressParser;
import com.greatsoft.transq.core.Receiver;
import com.greatsoft.transq.core.RouteGroup;
import com.greatsoft.transq.core.RouterImp;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.Envelope;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.task.Task;
import com.greatsoft.transq.core.task.TaskHelper;
import com.greatsoft.transq.core.task.TaskQueue;
import com.greatsoft.transq.core.task.TaskQueueManagerImp;
import com.greatsoft.transq.exception.MessageFormatException;
import com.greatsoft.transq.exception.ReceiveException;
import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.utils.CalenderAndString;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.FileHelper;

public class RouterServer implements Runnable {
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
	/** serverAddress，去消息的Q的地址 */
	private Address serverAddress;
	/** runningFlag变量系统退出的时候使用 */
	private Boolean runningFlag = true;
	/** 留作扩展使用 */
	private Object options;

	public RouterServer(Address serverAddress, String taskQueueLocalName,
			String taskQueueRemoteName, String taskQueueContainerName) {
		super();
		this.serverAddress = serverAddress;
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
		taskQueueLocal = getTaskQueue(taskQueueLocalName,taskQueueContainerName);
		taskQueueRemote = getTaskQueue(taskQueueRemoteName,taskQueueContainerName);
		options = loadConnectionOptions();
		/** 初始化serverAddress对应的消息中间件连接器 */
		Connector connector = ConnectorAdapterImp.getInstance().getConnector(
				serverAddress, options);
		if (null == connector) {
			log.error("创建与地址“" + serverAddress + "”匹配的消息中间件连接connector失败");
			return;
		}
		/** 与消息中间件建立连接 */
		boolean flag = false;
		while (!flag) {
			try {
				connector.connect();
			} catch (ConnectException e1) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.CONNECTION_ERROR)
						+ e1.getMessage());
				try {
					Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL*5);
				} catch (InterruptedException e) {
					log.error(ErrorCode
							.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
							+ e.getMessage());
				}
				continue;
			}
			flag = connector.isConnected();
		}
		Receiver receiver = connector.createReceiver();
		/** 创建消息接收端 */
		AbstractMessage message = null;
		Envelope envelope = null;
		Address[] targetAddressArray = null;
		String sendIdentify = null;
		String targetAddress = null;
		/** 循环从消息队列中取得消息,进行处理. */
		while (runningFlag) {
			message = getOneMessage(receiver);
			envelope = message.getEnvelope();
			sendIdentify = envelope.getSendIdentify();
			targetAddress = envelope.getTargetAddress();
			userLog.info("路由处理模块接收到一条信息，SendIdentify=" + sendIdentify+ " ，TargetAddress=" + targetAddress);
			if (isexpired(message)) {/** 检测消息是否超时 */
				continue;
			}
			targetAddressArray = QueueAddressParser.getInstance().parse(targetAddress);
			if (targetAddressArray == null) {/** 信息的目的地址解析错误 */
				log.error(ErrorCode.getErrorMessage(ErrorCode.PARSE_TARGRT_ADDRESS_ERROR));
				userLog.error("路由处理模块处理一条接收到的消息失败，SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(sendIdentify);
				continue;
			}
			RouteGroup[] routerGroupArray = RouterImp.groupByNext(Config.LOCAL_ADDRESS, targetAddressArray);
			if (routerGroupArray == null) {/** 信息的目的地址路由解析失败 */
				log.error(ErrorCode.getErrorMessage(ErrorCode.ROUTER_GROUP_ERROR));
				userLog.error("路由处理模块处理一条接收到的消息失败，SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(sendIdentify);
				continue;
			}
			int routeGroupArrayLength = routerGroupArray.length;
			for (int i = 0; i < routeGroupArrayLength; i++) {/** 每个RouteGroup生成一个新的message，进而生成一个新的任务记录 */
				newMessage(message, routerGroupArray[i],taskQueueLocal,taskQueueRemote);
			}
			ProcessHelper.successProcess(sendIdentify);
		}
		this.taskQueueLocal.closeTaskQueue();
		this.taskQueueRemote.closeTaskQueue();
	}
	/**
	 * 根据receiver从消息中间件获取一条信息
	 * @param receiver
	 * @return
	 */
	private static AbstractMessage getOneMessage(Receiver receiver){
		AbstractMessage message=getMessage(receiver);
		while (message == null) {
			log.info("路由处理模块没有接收到消息");
			try {
				Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL*100);
			} catch (InterruptedException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)+ e.getMessage());
			}
			message=getMessage(receiver);
		}
		MessageHelper.putMessageToReceived(message);
		return message;
	}
	
	/**
	 * 根据receiver获取一条message。 如果出现message格式错误则打印格式错误信息，并返回null
	 * 如果出现receiver连接错误则打印连接错误信息，并返回null 如果没有异常则返回一条message
	 * @param receiver
	 * @return
	 */
	private static AbstractMessage getMessage(Receiver receiver) {
		AbstractMessage message = null;
		try {
			message = receiver.get();
		} catch (MessageFormatException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.MESSAGE_FORMAT_ERROR) + e.getMessage());
			return null;
		} catch (ReceiveException e1) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.GET_DATA_ERROR)
					+ e1.getMessage());
			return null;
		}
		return message;
	}
	
	private static void newMessage(AbstractMessage message, RouteGroup routerGroup,TaskQueue taskQueueLocal,TaskQueue taskQueueRemote) {
		if (routerGroup != null) {
			String nextHop = routerGroup.getNextHop();
			String sendIdentify=message.getEnvelope().getSendIdentify();
			if (nextHop == null || nextHop.equals(ConstantValue.NULL_STRING)){
				log.error("消息中出现未知的目的地址，SendIdentify=" + sendIdentify);
				userLog.error("路由处理模块处理一条接收到的消息失败，SendIdentify=" + sendIdentify);
			}else if (nextHop.equals(Config.LOCAL_ADDRESS)) {/**下一跳地址是本地节点，即要将信息发送到本地数据处理部门*/
				String[] newTargetAddressArray = routerGroup.getGroupedTargetAddress();
				int newTargetAddressArrayCount = newTargetAddressArray.length;
				for (int index = 0; index < newTargetAddressArrayCount; index++) {
					putNewMessageAndNewTask(message,nextHop,newTargetAddressArray[index],ConstantValue.LOCAL,taskQueueLocal);
				}
			} else {/**下一跳地址是其他节点*/
				putNewMessageAndNewTask(message,nextHop,routerGroup.getTargetAddress(),ConstantValue.REMOTE,taskQueueRemote);
			}
		}
		return;
	}
/**
 * 生成新的消息和对应的任务记录，将生成的新的消息放入新信息队列中，对应的任务记录放入对应的任务记录队列中
 * @param message 接收到的消息
 * @param nextHop 新消息的下一跳地址
 * @param targetAddress 新消息的目的地址
 * @param type 新消息的类型：本地消息或者远程消息
 * @param taskQueue 任务记录需要存放的任务记录队列
 */
	private static void putNewMessageAndNewTask(AbstractMessage message,String nextHop,String targetAddress,String type,TaskQueue taskQueue) {
		AbstractMessage newMessage = MessageHelper.getNewMessage(message,targetAddress);
		Envelope newEnvelope =newMessage.getEnvelope();
		String newSendIdentify = newEnvelope.getSendIdentify();
		String sendIdentify=message.getEnvelope().getSendIdentify();
		userLog.info("路由模块接收到的消息(SendIdentify="+sendIdentify+")经过路由解析生成一条新的"+type+"消息:SendIdentify=" + newSendIdentify+", TargetAddress=" + targetAddress);
		/** 将生成的newMessage落地，落写循环策略，控制磁盘文件和落地同步*/
		if (!MessageHelper.putMessageToNew(newMessage)) {/**newMessage落地失败 */
			log.error(type+"消息存储到新消息队列中失败:SendIdentify="+ newSendIdentify);
			userLog.error("路由模块处理一条新的"+type+"消息失败:SendIdentify=" + newSendIdentify);
			return;
		}
		log.info(""+type+"消息存储到新消息队列中成功:SendIdentify=" + newSendIdentify);
		Task task = getNewTask(newEnvelope, nextHop,type);
		if (!putTask(task, taskQueue)) {
			log.error("任务记录放入"+type+"任务记录队列失败:SendIdentify=：" + newSendIdentify);
			userLog.error("路由模块处理一条新的"+type+"消息失败:SendIdentify=" + newSendIdentify);
			return;
		}
		userLog.info("路由模块处理一条新的"+type+"消息成功:SendIdentify=" + newSendIdentify);
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
			ProcessHelper.failedProcess(sendIdentify);
			return true;
		} else if (Calendar.getInstance().compareTo(expiredTime) > 0) {
			/** 超时，对应的message放入超时消息队列 */
			log.error("消息超时：SendIdentify=" + sendIdentify);
			userLog.error("路由处理模块处理一条任务记录失败，消息超时，SendIdentify=" + sendIdentify);
			ProcessHelper.expiredProcess(sendIdentify);
			return true;
		}
		return false;
	}
	
	/**
	 * 将任务记录task放入任务记录队列taskQueue中。
	 * 如果成功，返回true。
	 * 如果失败，将对应的信息文件从新消息队列移动错误信息队列中，返回false。
	 * @param task 任务记录
	 * @param taskQueue 任务记录队列
	 * @param newSendIdentify
	 * @return true | false
	 */
	public static boolean putTask(Task task, TaskQueue taskQueue) {
		String sendIdentify=task.getSendIdentify();
		int putOneTaskResult = ErrorCode.NO_ERROR;
		try {
			putOneTaskResult = taskQueue.putOneTask(task);
		} catch (TaskQueueException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TASK_QUEUE_EXCEPTION_ERROR)+ e.getMessage());
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
 * @param taskQueueName
 * @param taskQueueContainerName
 * @return TaskQueue
 */
	public static TaskQueue getTaskQueue(String taskQueueName,String taskQueueContainerName) {
		TaskQueue taskQueue = null;
		while (taskQueue == null) {
			try {
				Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL);
			} catch (InterruptedException e) {
				log.error(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR+ e.getMessage());
			}
			taskQueue = TaskQueueManagerImp.getInstance(taskQueueContainerName).getTaskQueue(taskQueueContainerName, taskQueueName);
		}
		return taskQueue;
	}
	
/**
 * 根据信封头信息和下一跳地址生成新的任务记录
 * @param envelope 信封头信息
 * @param nextHop 下一跳地址
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
