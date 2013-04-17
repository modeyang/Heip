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
		userLog.info("本地消息处理模块开始处理一个任务记录: sendIdentify=" + sendIdentify);
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
				/** 下一跳地址为*，即数据处理部门缺省 */
				processorNameValue = Config.processorNameMap.get(dataType);
			}
			try {
				/** 利用反射,根据类名得到其实例 */
				processor = (Processor) newInstance(processorNameValue, null);
			} catch (Exception e) {
				/** 找对应的Processor处理器异常 */
				log.error("找任务记录对应的Processor处理器异常：sendIdentify=" + sendIdentify
						+ "; 数据处理部门是 = " + toAddress + "; dataType = "
						+ dataType + e.getMessage());
				userLog.error("本地消息处理模块处理消息失败: sendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
				userLog.info("本地信息处理模块处理一条任务记录完毕：SendIdentify=" + sendIdentify);
				return;
			}
			if (processor != null) {
				/** 成功找到对应的processor */
				log.info("本地消息处理模块得到一个任务处理器来处理任务记录: sendIdentify="
						+ sendIdentify);
				ResultImp result = null;
				try {
					result = processor.process(message, null);
				} catch (ProcessorException e) {
					log.error(e.getMessage());
				}
				if (null != result
						&& result.getReturnCode() == ErrorCode.NO_ERROR) {
					userLog.info("本地消息处理模块处理消息成功: sendIdentify=" + sendIdentify
							+ " ;returnInfo=" + result.getReturnInfo());
					ProcessHelper.successedProcess(taskQueue, task);
				} else {
					userLog.error("本地消息处理模块处理消息失败: sendIdentify="
							+ sendIdentify);
					String logString = "本地消息处理模块处理消息失败: sendIdentify=："
							+ sendIdentify + "; 数据处理部门是 = " + toAddress
							+ "; dataType = " + dataType;
					if (null != result)
						logString += " ;returnInfo" + result.getReturnInfo();
					log.error(logString);
					ProcessHelper.failedProcess(taskQueue, task);
				}
				
				AbstractMessage returnMessage = result.getReturnMessage();
				if (returnMessage != null) {
					userLog.error("本地消息处理模块处理消息(sendIdentify=" + sendIdentify
							+ ")获取到一条返回信息：" + returnMessage);
					if (ProcessHelper.putReturnMessage(returnMessage)) {
						userLog.info("本地消息处理器处理消息(sendIdentify=" + sendIdentify
								+ ")返回的消息发送成功");
					} else {
						userLog.info("本地消息处理器处理消息(sendIdentify=" + sendIdentify
								+ ")返回的消息发送失败");
					}
				}
			} else {
				/** 找不到对应的Processor处理器 */
				log.error("找不到任务记录对应的Processor处理器：sendIdentify=" + sendIdentify
						+ "; 数据处理部门是 = " + toAddress + "; dataType = "
						+ dataType);
				userLog.error("本地消息处理模块处理消息失败: sendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
			}
		} else {
			/** 获取任务记录对应的信息失败 */
			log.error("本地消息处理模块，获取任务记录对应的信息失败：SendIdentify=" + sendIdentify);
			userLog.error("本地消息处理模块处理消息失败: sendIdentify=" + sendIdentify);
			ProcessHelper.failedProcess(taskQueue, task);
		}

		task = null;
		userLog.info("本地信息处理模块处理一条任务记录完毕：SendIdentify=" + sendIdentify);
	}

	/**
	 * 使用反射机制，返回一个指定的类的实例对象
	 * 
	 * @param className
	 *            指定的类的名字（包名+类名）
	 * @param args
	 *            初始化实例对象的参数
	 * @return 指定的类的实例对象或者null
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
	 * 返回一个指定的类的实例对象,构造函数无参数
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object newConstructor(String className, Class newoneClass) {
		Constructor cons = null;
		try {
			cons = newoneClass.getConstructor();
		} catch (SecurityException e) {
			log.error("反射安全异常，Processor处理器类名=" + className + e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("反射异常，Processor处理器类名=" + className + e.getMessage());
		}
		if (null != cons) {
			try {
				return cons.newInstance();
			} catch (IllegalArgumentException e) {
				log.error("反射不合法的参数，Processor处理器类名=" + className
						+ e.getMessage());
			} catch (InstantiationException e) {
				log.error("反射实例化失败，Processor处理器类名=" + className
						+ e.getMessage());
			} catch (IllegalAccessException e) {
				log.error("反射非法访问，Processor处理器类名=" + className + e.getMessage());
			} catch (InvocationTargetException e) {
				log.error("反射非法调用，Processor处理器类名=" + className + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * 返回一个指定的类的实例对象,构造函数有参数
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Object newConstructor(String className, Object[] args,
			Class newoneClass, Class[] argsClass) {
		Constructor cons = null;
		try {
			cons = newoneClass.getConstructor(argsClass);
		} catch (SecurityException e) {
			log.error("反射安全异常，Processor处理器类名=" + className + e.getMessage());
		} catch (NoSuchMethodException e) {
			log.error("反射异常，Processor处理器类名=" + className + e.getMessage());
		}
		if (cons != null) {
			try {
				return cons.newInstance(args);
			} catch (IllegalArgumentException e) {
				log.error("反射不合法的参数，Processor处理器类名=" + className + "，参数="
						+ args + e.getMessage());
			} catch (InstantiationException e) {
				log.error("反射实例化失败，Processor处理器类名=" + className
						+ e.getMessage());
			} catch (IllegalAccessException e) {
				log.error("反射非法访问，Processor处理器类名=" + className + e.getMessage());
			} catch (InvocationTargetException e) {
				log.error("反射非法调用，Processor处理器类名=" + className + e.getMessage());
			}
		}
		return null;
	}

}
