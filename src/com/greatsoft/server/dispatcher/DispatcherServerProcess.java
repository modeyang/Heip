package com.greatsoft.server.dispatcher;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.task.Task;
import com.greatsoft.transq.core.task.TaskQueue;
import com.greatsoft.transq.core.task.TaskQueueHelper;
import com.greatsoft.transq.core.task.TaskQueueManagerImp;
import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.utils.CalenderAndString;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;

public class DispatcherServerProcess implements Runnable {
	private static Logger log = Log.getLog(DispatcherServerProcess.class);
	private static Logger userLog = Logger
			.getLogger(ConstantValue.DISPATCHER_USER_LOG);

	private String taskQueueName;
	public static TaskQueue taskQueue;

	private boolean runningFlag = true;
	private TaskProcessStub taskProcessStub = null;
	/** �̳߳� */
	public ExecutorService pool = null;
	/** �̳߳����̵߳ĸ��� */
	private int childThreadCount = 0;

	public DispatcherServerProcess(String taskQueueName) {
		super();
		this.taskQueueName = taskQueueName;
		pool = Executors.newFixedThreadPool(Config.THREAD_NUMBER);
	}

	public boolean isRunningFlag() {
		return runningFlag;
	}

	public void setRunningFlag(boolean quit) {
		this.runningFlag = quit;
	}

	public TaskQueue getTaskQueue() {
		return taskQueue;
	}

	@Override
	public void run() {
		Task task = null;
		taskQueue = TaskQueueManagerImp.getInstance(
				Config.TASK_QUEUE_CONTAINER_NAME).getTaskQueue(
				Config.TASK_QUEUE_CONTAINER_NAME, taskQueueName);
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
					Config.TASK_QUEUE_CONTAINER_NAME, taskQueueName);
		}
		Calendar expiredTime = null;
		String sendIdentify = null;
		while (runningFlag) {
			task = getTask(taskQueue);
			if (null == task) {
				log.info("����������ģ��û�дӱ������������ȡ�������¼");
				try {
					Thread.sleep(Config.ROUTER_PROCESSING_INTERVAL * 100);
				} catch (InterruptedException e) {
					log.error(ErrorCode
							.getErrorMessage(ErrorCode.THREAD_SLEEP_INTERRUPT_ERROR)
							+ e.getMessage());
				}
				continue;
			}
			sendIdentify = task.getSendIdentify();
			userLog.info("����������ģ��ӱ������������ȡ��һ�������¼��SendIdentify="
					+ sendIdentify);
			expiredTime = CalenderAndString.stringToCalendar(task
					.getExpiredTime());
			if (null == expiredTime) {
				log.error("�����¼�ĳ�ʱʱ���ַ���ת��ΪCalenderʵ��ʧ��:SendIdentify="
						+ sendIdentify);
				userLog.error("Զ��������ģ�鴦��һ�������¼ʧ�ܣ�SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
				continue;
			}
			if (Calendar.getInstance().compareTo(expiredTime) > 0) {
				/** ��ʱ,ɾ�������¼����Ӧ��message�ļ����볬ʱ�ļ����� */
				log.error("�����¼��ʱ��SendIdentify=" + sendIdentify);
				userLog.error("����������ģ�鴦��һ�������¼ʧ�ܣ������¼��ʱ��SendIdentify="
						+ sendIdentify);
				ProcessHelper.expiredTimeProcess(taskQueue, task);
				continue;
			}
			if (!ProcessHelper.updateTaskStatusToProcessing(taskQueue, task)) {
				/** ���������¼��״̬Ϊ�������С� */
				userLog.error("����������ģ�鴦��һ�������¼ʧ�ܣ�SendIdentify=" + sendIdentify);
				continue;
			}
			userLog.info("����һ��������Ϣ�������񡣴�����Ϣ��SendIdentify=" + sendIdentify);
			taskProcessStub = new TaskProcessStub(task, taskQueue);
			pool.execute(taskProcessStub);
			childThreadCount++;
		}
	}

	private static Task getTask(TaskQueue taskQueue) {
		Task task = null;
		try {
			task = TaskQueueHelper.getFirstOne(taskQueue,
					Config.LOCAL_MESSAGE_PROCESS_PRIORITY_MODE);
		} catch (TaskQueueException e) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.TASK_QUEUE_EXCEPTION_ERROR)
					+ e.getMessage());
		}
		return task;
	}

}
