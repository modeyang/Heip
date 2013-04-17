package com.greatsoft.transq.core.task;

import com.greatsoft.transq.exception.TaskQueueException;
import com.greatsoft.transq.exception.TaskQueueNotExistException;

/**������й������*/
public interface TaskQueueManager {
	/**
	 * ��ȡָ�����е�TaskQueue����
	 * @param queueName �����������
	 * @return ������ж���ʵ��
	 * @throws TaskQueueNotExistException
	 */
	TaskQueue getTaskQueue(String dbName,String queueName) throws TaskQueueNotExistException;
	/** �����¶��У������������������
	 * @param queueName ��������
	 * @return ����������ж���
	 * 	@throws TaskQueueException ���в���ʧ�ܡ�
	 */
	TaskQueue createTaskQueue(String dbName,String queueName, TaskQueueProperties options) throws TaskQueueException;
	/**
	 * ɾ��ָ���������
	 * @param queueName ��ɾ�������������
	 * @throws TaskQueueNotExistException
	 */
	boolean DeleteTaskQueue(String queueName) throws TaskQueueNotExistException;
	/**
	 * ������������Ƿ����
	 * @param queueName �����������
	 * @return
	 */
	Boolean isExist(String queueName);
	/**
	 * �о������������
	 * @return
	 */
	String[] listQueue();
}

