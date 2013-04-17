package com.greatsoft.transq.core.task;

import java.util.List;

import com.greatsoft.transq.exception.TaskQueueException;

/**
 * 任务队列操作接口，该接口实例应由TaskQueueManager创建，而不应直接创建
 * 
 * @author RAY
 */
public interface TaskQueue {
	/**
	 * 获得任务队列名称
	 * 
	 * @uml.property name="name"
	 */
	String getName();

	/**
	 * 任务队列的属性
	 * 
	 * @uml.property name="properties"
	 * @uml.associationEnd
	 */
	TaskQueueProperties getProperties();

	/**
	 * 取得第一条符合条件的任务记录
	 * 
	 * @param nextID
	 *            下一跳地址。搜索条件。为NULL或""时，该条件忽略。
	 * @param status
	 *            任务状态。搜索条件，各任务状态可以用'|'操作符组合表示只要其中一种状态符合即满足需要
	 * @return 返回任务记录, 无符合条件的记录，返回null
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	Task getFirstOne(String nextID, int status) throws TaskQueueException;

	/**
	 * 取得第一条符合条件的任务记录
	 * 
	 * @param status
	 *            任务状态。搜索条件，各任务状态可以用'|'操作符组合表示只要其中一种状态符合即满足需要
	 * @return 返回任务记录, 无符合条件的记录，返回null
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	Task getFirstOne(int status) throws TaskQueueException;

	/**
	 * 取得第一条符合条件的优先级最高的任务记录，优先级范围1-9。9是最高的优先级
	 * 
	 * @param status
	 *            任务状态。搜索条件，各任务状态可以用'|'操作符组合表示只要其中一种状态符合即满足需要
	 * @return 返回任务记录, 无符合条件的记录，返回null
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	Task getFirstOneByPriority(int status) throws TaskQueueException;

	/**
	 * 取得第一条符合条件的失效时间最短的任务记录，优先级范围1-9。9是最高的优先级
	 * 
	 * @param status
	 *            任务状态。搜索条件，各任务状态可以用'|'操作符组合表示只要其中一种状态符合即满足需要
	 * @return 返回任务记录, 无符合条件的记录，返回null
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	Task getFirstOneByDeadTime(int status) throws TaskQueueException;

	/**
	 * 取得符合条件的所有任务记录
	 * 
	 * @param nextID
	 *            下一跳地址。搜索条件。为NULL或""时，该条件忽略。
	 * @param status
	 *            任务状态。搜索条件，各任务状态可以用'|'操作符组合表示只要其中一种状态符合即满足需要
	 * @return 返回任务记录, 无符合条件的记录，返回null
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	List<Task> getTask(String nextID, int status) throws TaskQueueException;

	/**
	 * 插入任务记录
	 * 
	 * @param tasks
	 *            待插入记录集合
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	boolean putTask(List<Task> tasks) throws TaskQueueException;

	/**
	 * 插入任务记录
	 * 
	 * @param task
	 *            待插入记录
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	public int putOneTask(Task task) throws TaskQueueException;

	/**
	 * 修改任务记录，其中task参数是先前用getTask获取的 根据task的ID来修改
	 * 
	 * @param ptask
	 *            先前GetTask调用获得的任务记录
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	boolean setTask(Task task) throws TaskQueueException;

	/**
	 * 删除任务记录
	 * 
	 * @param task
	 *            待删除任务记录，必须为先前通过GetTask调用获得的任务记录
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	boolean deleteTask(Task task) throws TaskQueueException;

	/**
	 * 列出指定条件的任务记录，该函数用于列举所有任务记录。
	 * 
	 * @param NextAddr
	 *            搜索条件，为NULL或""时， 取所有地址的记录
	 * @param tasks
	 *            返回查询结果序列
	 * @param count
	 *            指定需要列举的记录数；返回时为结果记录序列中记录个数
	 * @throws TaskQueueException
	 *             队列操作失败。
	 */
	void list(String NextAddr, int count, List<Task> tasks)
			throws TaskQueueException;

	/**
	 * 重置所有任务记录状态为未处理
	 * 
	 * @throws TaskQueueException
	 */
	boolean resetAllTask() throws TaskQueueException;

	/**
	 * 打开指定的任务记录队列
	 * 
	 * @param taskQueueName
	 *            任务记录队列名字
	 * @return
	 * @throws TaskQueueException
	 */
	boolean openTaskQueue(String taskQueueName) throws TaskQueueException;

	/**
	 * 关闭任务记录队列
	 */
	void closeTaskQueue();

}