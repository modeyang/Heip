package com.greatsoft.server.dispatcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.core.task.Task;
import com.greatsoft.transq.core.task.TaskQueue;
import com.greatsoft.transq.exception.ProcessorException;
import com.greatsoft.transq.processor.api.Processor;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;

public class TaskProcessStub implements Runnable {
	private static Logger log = Log.getLog(TaskProcessStub.class);
	private static Logger userLog = Logger
			.getLogger(ConstantValue.DISPATCHER_USER_LOG);

	private Task task;
	private TaskQueue taskQueue;

	public TaskProcessStub(Task task, TaskQueue taskQueue) {
		this.task = task;
		this.taskQueue = taskQueue;
	}

	@Override
	public void run() {
		String sendIdentify = task.getSendIdentify();
		userLog.info("������Ϣ����ģ�鿪ʼ����һ�������¼: sendIdentify=" + sendIdentify);
		AbstractMessage message = MessageHelper.loadMessage(task);
		if (message != null) {
			String toAddress = task.getToAddress();
			String dataType = task.getDataType();
			Processor processor = null;
			String processorNameValue = null;
			if (!toAddress.equals(Config.LOCAL_ADDRESS)) {
				processorNameValue = Config.processorNameMap.get(toAddress
						+ dataType);

			} else {
				/** ��һ����ַΪ*�������ݴ�����ȱʡ */
				processorNameValue = Config.processorNameMap.get(dataType);
			}
			try {
				/** ���÷���,���������õ���ʵ�� */
				processor = (Processor) newInstance(processorNameValue, null);
			} catch (Exception e) {
				/** �Ҷ�Ӧ��Processor�������쳣 */
				log.error("�������¼��Ӧ��Processor�������쳣��sendIdentify=" + sendIdentify
						+ "; ���ݴ������� = " + toAddress + "; dataType = "
						+ dataType + e.getMessage());
				userLog.error("������Ϣ����ģ�鴦����Ϣʧ��: sendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
				userLog.info("������Ϣ����ģ�鴦��һ�������¼��ϣ�SendIdentify=" + sendIdentify);
				return;
			}
			if (processor != null) {
				/** �ɹ��ҵ���Ӧ��processor */
				log.info("������Ϣ����ģ��õ�һ���������������������¼: sendIdentify="
						+ sendIdentify);
				ResultImp result = null;
				try {
					result = processor.process(message, null);
				} catch (ProcessorException e) {
					log.error(e.getMessage());
				}
				if (null != result
						&& result.getReturnCode() == ErrorCode.NO_ERROR) {
					userLog.info("������Ϣ����ģ�鴦����Ϣ�ɹ�: sendIdentify=" + sendIdentify
							+ " ;returnInfo=" + result.getReturnInfo());
					ProcessHelper.successedProcess(taskQueue, task);
				} else {
					userLog.error("������Ϣ����ģ�鴦����Ϣʧ��: sendIdentify="
							+ sendIdentify);
					String logString = "������Ϣ����ģ�鴦����Ϣʧ��: sendIdentify=��"
							+ sendIdentify + "; ���ݴ������� = " + toAddress
							+ "; dataType = " + dataType;
					if (null != result)
						logString += " ;returnInfo" + result.getReturnInfo();
					log.error(logString);
					ProcessHelper.failedProcess(taskQueue, task);
				}
				
				AbstractMessage returnMessage = result.getReturnMessage();
				if (returnMessage != null) {
					userLog.error("������Ϣ����ģ�鴦����Ϣ(sendIdentify=" + sendIdentify
							+ ")��ȡ��һ��������Ϣ��" + returnMessage);
					if (ProcessHelper.putReturnMessage(returnMessage)) {
						userLog.info("������Ϣ������������Ϣ(sendIdentify=" + sendIdentify
								+ ")���ص���Ϣ���ͳɹ�");
					} else {
						userLog.info("������Ϣ������������Ϣ(sendIdentify=" + sendIdentify
								+ ")���ص���Ϣ����ʧ��");
					}
				}
			} else {
				/** �Ҳ�����Ӧ��Processor������ */
				log.error("�Ҳ��������¼��Ӧ��Processor��������sendIdentify=" + sendIdentify
						+ "; ���ݴ������� = " + toAddress + "; dataType = "
						+ dataType);
				userLog.error("������Ϣ����ģ�鴦����Ϣʧ��: sendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
			}
		} else {
			/** ��ȡ�����¼��Ӧ����Ϣʧ�� */
			log.error("������Ϣ����ģ�飬��ȡ�����¼��Ӧ����Ϣʧ�ܣ�SendIdentify=" + sendIdentify);
			userLog.error("������Ϣ����ģ�鴦����Ϣʧ��: sendIdentify=" + sendIdentify);
			ProcessHelper.failedProcess(taskQueue, task);
		}

		task = null;
		userLog.info("������Ϣ����ģ�鴦��һ�������¼��ϣ�SendIdentify=" + sendIdentify);
	}

	/**
	 * ʹ�÷�����ƣ�����һ��ָ�������ʵ������
	 * 
	 * @param className
	 *            ָ����������֣�����+������
	 * @param args
	 *            ��ʼ��ʵ������Ĳ���
	 * @return ָ�������ʵ���������null
	 */
	@SuppressWarnings("rawtypes")
	private static Object newInstance(String className, Object[] args) {
		Class newoneClass = null;
		try {
			newoneClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			log.error(className + e.getMessage());
			return null;
		}
		if (args != null) {
			Class[] argsClass = new Class[args.length];
			for (int i = 0, j = args.length; i < j; i++) {
				argsClass[i] = args[i].getClass();
			}
			return newConstructor(className, args, newoneClass, argsClass);
		} else {
			return newConstructor(className, newoneClass);
		}
	}

	/**
	 * ����һ��ָ�������ʵ������,���캯���޲���
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object newConstructor(String className, Class newoneClass) {
		Constructor cons = null;
		try {
			cons = newoneClass.getConstructor();
		} catch (SecurityException e) {
			log.error("���䰲ȫ�쳣��Processor����������=" + className + e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("�����쳣��Processor����������=" + className + e.getMessage());
		}
		if (null != cons) {
			try {
				return cons.newInstance();
			} catch (IllegalArgumentException e) {
				log.error("���䲻�Ϸ��Ĳ�����Processor����������=" + className
						+ e.getMessage());
			} catch (InstantiationException e) {
				log.error("����ʵ����ʧ�ܣ�Processor����������=" + className
						+ e.getMessage());
			} catch (IllegalAccessException e) {
				log.error("����Ƿ����ʣ�Processor����������=" + className + e.getMessage());
			} catch (InvocationTargetException e) {
				log.error("����Ƿ����ã�Processor����������=" + className + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * ����һ��ָ�������ʵ������,���캯���в���
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object newConstructor(String className, Object[] args,
			Class newoneClass, Class[] argsClass) {
		Constructor cons = null;
		try {
			cons = newoneClass.getConstructor(argsClass);
		} catch (SecurityException e) {
			log.error("���䰲ȫ�쳣��Processor����������=" + className + e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("�����쳣��Processor����������=" + className + e.getMessage());
		}
		if (cons != null) {
			try {
				return cons.newInstance(args);
			} catch (IllegalArgumentException e) {
				log.error("���䲻�Ϸ��Ĳ�����Processor����������=" + className + "������="
						+ args + e.getMessage());
			} catch (InstantiationException e) {
				log.error("����ʵ����ʧ�ܣ�Processor����������=" + className
						+ e.getMessage());
			} catch (IllegalAccessException e) {
				log.error("����Ƿ����ʣ�Processor����������=" + className + e.getMessage());
			} catch (InvocationTargetException e) {
				log.error("����Ƿ����ã�Processor����������=" + className + e.getMessage());
			}
		}
		return null;
	}

}
