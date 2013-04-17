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
	/** serverAddress��ȥ��Ϣ��Q�ĵ�ַ */
	private Address serverAddress;
	/** runningFlag����ϵͳ�˳���ʱ��ʹ�� */
	private Boolean runningFlag = true;
	/** ������չʹ�� */
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
		/** ��ʼ��serverAddress��Ӧ����Ϣ�м�������� */
		Connector connector = ConnectorAdapterImp.getInstance().getConnector(
				serverAddress, options);
		if (null == connector) {
			log.error("�������ַ��" + serverAddress + "��ƥ�����Ϣ�м������connectorʧ��");
			return;
		}
		/** ����Ϣ�м���������� */
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
		/** ������Ϣ���ն� */
		AbstractMessage message = null;
		Envelope envelope = null;
		Address[] targetAddressArray = null;
		String sendIdentify = null;
		String targetAddress = null;
		/** ѭ������Ϣ������ȡ����Ϣ,���д���. */
		while (runningFlag) {
			message = getOneMessage(receiver);
			envelope = message.getEnvelope();
			sendIdentify = envelope.getSendIdentify();
			targetAddress = envelope.getTargetAddress();
			userLog.info("·�ɴ���ģ����յ�һ����Ϣ��SendIdentify=" + sendIdentify+ " ��TargetAddress=" + targetAddress);
			if (isexpired(message)) {/** �����Ϣ�Ƿ�ʱ */
				continue;
			}
			targetAddressArray = QueueAddressParser.getInstance().parse(targetAddress);
			if (targetAddressArray == null) {/** ��Ϣ��Ŀ�ĵ�ַ�������� */
				log.error(ErrorCode.getErrorMessage(ErrorCode.PARSE_TARGRT_ADDRESS_ERROR));
				userLog.error("·�ɴ���ģ�鴦��һ�����յ�����Ϣʧ�ܣ�SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(sendIdentify);
				continue;
			}
			RouteGroup[] routerGroupArray = RouterImp.groupByNext(Config.LOCAL_ADDRESS, targetAddressArray);
			if (routerGroupArray == null) {/** ��Ϣ��Ŀ�ĵ�ַ·�ɽ���ʧ�� */
				log.error(ErrorCode.getErrorMessage(ErrorCode.ROUTER_GROUP_ERROR));
				userLog.error("·�ɴ���ģ�鴦��һ�����յ�����Ϣʧ�ܣ�SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(sendIdentify);
				continue;
			}
			int routeGroupArrayLength = routerGroupArray.length;
			for (int i = 0; i < routeGroupArrayLength; i++) {/** ÿ��RouteGroup����һ���µ�message����������һ���µ������¼ */
				newMessage(message, routerGroupArray[i],taskQueueLocal,taskQueueRemote);
			}
			ProcessHelper.successProcess(sendIdentify);
		}
		this.taskQueueLocal.closeTaskQueue();
		this.taskQueueRemote.closeTaskQueue();
	}
	/**
	 * ����receiver����Ϣ�м����ȡһ����Ϣ
	 * @param receiver
	 * @return
	 */
	private static AbstractMessage getOneMessage(Receiver receiver){
		AbstractMessage message=getMessage(receiver);
		while (message == null) {
			log.info("·�ɴ���ģ��û�н��յ���Ϣ");
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
	 * ����receiver��ȡһ��message�� �������message��ʽ�������ӡ��ʽ������Ϣ��������null
	 * �������receiver���Ӵ������ӡ���Ӵ�����Ϣ��������null ���û���쳣�򷵻�һ��message
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
				log.error("��Ϣ�г���δ֪��Ŀ�ĵ�ַ��SendIdentify=" + sendIdentify);
				userLog.error("·�ɴ���ģ�鴦��һ�����յ�����Ϣʧ�ܣ�SendIdentify=" + sendIdentify);
			}else if (nextHop.equals(Config.LOCAL_ADDRESS)) {/**��һ����ַ�Ǳ��ؽڵ㣬��Ҫ����Ϣ���͵��������ݴ�����*/
				String[] newTargetAddressArray = routerGroup.getGroupedTargetAddress();
				int newTargetAddressArrayCount = newTargetAddressArray.length;
				for (int index = 0; index < newTargetAddressArrayCount; index++) {
					putNewMessageAndNewTask(message,nextHop,newTargetAddressArray[index],ConstantValue.LOCAL,taskQueueLocal);
				}
			} else {/**��һ����ַ�������ڵ�*/
				putNewMessageAndNewTask(message,nextHop,routerGroup.getTargetAddress(),ConstantValue.REMOTE,taskQueueRemote);
			}
		}
		return;
	}
/**
 * �����µ���Ϣ�Ͷ�Ӧ�������¼�������ɵ��µ���Ϣ��������Ϣ�����У���Ӧ�������¼�����Ӧ�������¼������
 * @param message ���յ�����Ϣ
 * @param nextHop ����Ϣ����һ����ַ
 * @param targetAddress ����Ϣ��Ŀ�ĵ�ַ
 * @param type ����Ϣ�����ͣ�������Ϣ����Զ����Ϣ
 * @param taskQueue �����¼��Ҫ��ŵ������¼����
 */
	private static void putNewMessageAndNewTask(AbstractMessage message,String nextHop,String targetAddress,String type,TaskQueue taskQueue) {
		AbstractMessage newMessage = MessageHelper.getNewMessage(message,targetAddress);
		Envelope newEnvelope =newMessage.getEnvelope();
		String newSendIdentify = newEnvelope.getSendIdentify();
		String sendIdentify=message.getEnvelope().getSendIdentify();
		userLog.info("·��ģ����յ�����Ϣ(SendIdentify="+sendIdentify+")����·�ɽ�������һ���µ�"+type+"��Ϣ:SendIdentify=" + newSendIdentify+", TargetAddress=" + targetAddress);
		/** �����ɵ�newMessage��أ���дѭ�����ԣ����ƴ����ļ������ͬ��*/
		if (!MessageHelper.putMessageToNew(newMessage)) {/**newMessage���ʧ�� */
			log.error(type+"��Ϣ�洢������Ϣ������ʧ��:SendIdentify="+ newSendIdentify);
			userLog.error("·��ģ�鴦��һ���µ�"+type+"��Ϣʧ��:SendIdentify=" + newSendIdentify);
			return;
		}
		log.info(""+type+"��Ϣ�洢������Ϣ�����гɹ�:SendIdentify=" + newSendIdentify);
		Task task = getNewTask(newEnvelope, nextHop,type);
		if (!putTask(task, taskQueue)) {
			log.error("�����¼����"+type+"�����¼����ʧ��:SendIdentify=��" + newSendIdentify);
			userLog.error("·��ģ�鴦��һ���µ�"+type+"��Ϣʧ��:SendIdentify=" + newSendIdentify);
			return;
		}
		userLog.info("·��ģ�鴦��һ���µ�"+type+"��Ϣ�ɹ�:SendIdentify=" + newSendIdentify);
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
			ProcessHelper.failedProcess(sendIdentify);
			return true;
		} else if (Calendar.getInstance().compareTo(expiredTime) > 0) {
			/** ��ʱ����Ӧ��message���볬ʱ��Ϣ���� */
			log.error("��Ϣ��ʱ��SendIdentify=" + sendIdentify);
			userLog.error("·�ɴ���ģ�鴦��һ�������¼ʧ�ܣ���Ϣ��ʱ��SendIdentify=" + sendIdentify);
			ProcessHelper.expiredProcess(sendIdentify);
			return true;
		}
		return false;
	}
	
	/**
	 * �������¼task���������¼����taskQueue�С�
	 * ����ɹ�������true��
	 * ���ʧ�ܣ�����Ӧ����Ϣ�ļ�������Ϣ�����ƶ�������Ϣ�����У�����false��
	 * @param task �����¼
	 * @param taskQueue �����¼����
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
 * ���������¼���е����ƺ������¼���е����ƣ����������¼����ʵ��
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
 * �����ŷ�ͷ��Ϣ����һ����ַ�����µ������¼
 * @param envelope �ŷ�ͷ��Ϣ
 * @param nextHop ��һ����ַ
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
