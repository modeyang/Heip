package com.greatsoft.transq.core.task;

import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.exception.TaskQueueNotExistException;

/**任务队列管理服务*/
public interface TaskQueueManager {
	/**
	 * 获取指定队列的TaskQueue对象
	 * @param queueName 任务队列名称
	 * @return 任务队列对象实例
	 * @throws TaskQueueNotExistException
	 */
	TaskQueue getTaskQueue(String dbName,String queueName) throws TaskQueueNotExistException;
	/** 创建新队列，并返回任务队列引用
	 * @param queueName 队列名称
	 * @return 返回任务队列对象
	 * 	@throws TaskQueueException 队列操作失败。
	 */
	TaskQueue createTaskQueue(String dbName,String queueName, TaskQueueProperties options) throws TaskQueueException;
	/**
	 * 删除指定任务队列
	 * @param queueName 待删除任务队列名称
	 * @throws TaskQueueNotExistException
	 */
	boolean DeleteTaskQueue(String queueName) throws TaskQueueNotExistException;
	/**
	 * 测试任务队列是否存在
	 * @param queueName 任务队列名称
	 * @return
	 */
	Boolean isExist(String queueName);
	/**
	 * 列举所有任务队列
	 * @return
	 */
	String[] listQueue();
}

