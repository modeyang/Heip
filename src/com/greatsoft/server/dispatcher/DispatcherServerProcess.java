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
	/** 线程池 */
	public ExecutorService pool = null;
	/** 线程池中线程的个数 */
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
				log.info("本地任务处理模块没有从本地任务队列中取到任务记录");
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
			userLog.info("本地任务处理模块从本地任务队列中取到一条任务记录，SendIdentify="
					+ sendIdentify);
			expiredTime = CalenderAndString.stringToCalendar(task
					.getExpiredTime());
			if (null == expiredTime) {
				log.error("任务记录的超时时间字符串转换为Calender实例失败:SendIdentify="
						+ sendIdentify);
				userLog.error("远程任务处理模块处理一条任务记录失败，SendIdentify=" + sendIdentify);
				ProcessHelper.failedProcess(taskQueue, task);
				continue;
			}
			if (Calendar.getInstance().compareTo(expiredTime) > 0) {
				/** 超时,删除任务记录，对应的message文件放入超时文件队列 */
				log.error("任务记录超时：SendIdentify=" + sendIdentify);
				userLog.error("本地任务处理模块处理一条任务记录失败，任务记录超时，SendIdentify="
						+ sendIdentify);
				ProcessHelper.expiredTimeProcess(taskQueue, task);
				continue;
			}
			if (!ProcessHelper.updateTaskStatusToProcessing(taskQueue, task)) {
				/** 更新任务记录的状态为“处理中” */
				userLog.error("本地任务处理模块处理一条任务记录失败，SendIdentify=" + sendIdentify);
				continue;
			}
			userLog.info("启动一个本地消息处理任务。处理消息：SendIdentify=" + sendIdentify);
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
