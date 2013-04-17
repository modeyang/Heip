package com.greatsoft.transq.core.task;

import java.util.List;

import com.greatsoft.transq.exception.TaskQueueException;

/**
 * ������в����ӿڣ��ýӿ�ʵ��Ӧ��TaskQueueManager����������Ӧֱ�Ӵ���
 * 
 * @author RAY
 */
public interface TaskQueue {
	/**
	 * ��������������
	 * 
	 * @uml.property name="name"
	 */
	String getName();

	/**
	 * ������е�����
	 * 
	 * @uml.property name="properties"
	 * @uml.associationEnd
	 */
	TaskQueueProperties getProperties();

	/**
	 * ȡ�õ�һ�����������������¼
	 * 
	 * @param nextID
	 *            ��һ����ַ������������ΪNULL��""ʱ�����������ԡ�
	 * @param status
	 *            ����״̬������������������״̬������'|'��������ϱ�ʾֻҪ����һ��״̬���ϼ�������Ҫ
	 * @return ���������¼, �޷��������ļ�¼������null
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	Task getFirstOne(String nextID, int status) throws TaskQueueException;

	/**
	 * ȡ�õ�һ�����������������¼
	 * 
	 * @param status
	 *            ����״̬������������������״̬������'|'��������ϱ�ʾֻҪ����һ��״̬���ϼ�������Ҫ
	 * @return ���������¼, �޷��������ļ�¼������null
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	Task getFirstOne(int status) throws TaskQueueException;

	/**
	 * ȡ�õ�һ���������������ȼ���ߵ������¼�����ȼ���Χ1-9��9����ߵ����ȼ�
	 * 
	 * @param status
	 *            ����״̬������������������״̬������'|'��������ϱ�ʾֻҪ����һ��״̬���ϼ�������Ҫ
	 * @return ���������¼, �޷��������ļ�¼������null
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	Task getFirstOneByPriority(int status) throws TaskQueueException;

	/**
	 * ȡ�õ�һ������������ʧЧʱ����̵������¼�����ȼ���Χ1-9��9����ߵ����ȼ�
	 * 
	 * @param status
	 *            ����״̬������������������״̬������'|'��������ϱ�ʾֻҪ����һ��״̬���ϼ�������Ҫ
	 * @return ���������¼, �޷��������ļ�¼������null
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	Task getFirstOneByDeadTime(int status) throws TaskQueueException;

	/**
	 * ȡ�÷������������������¼
	 * 
	 * @param nextID
	 *            ��һ����ַ������������ΪNULL��""ʱ�����������ԡ�
	 * @param status
	 *            ����״̬������������������״̬������'|'��������ϱ�ʾֻҪ����һ��״̬���ϼ�������Ҫ
	 * @return ���������¼, �޷��������ļ�¼������null
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	List<Task> getTask(String nextID, int status) throws TaskQueueException;

	/**
	 * ���������¼
	 * 
	 * @param tasks
	 *            �������¼����
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	boolean putTask(List<Task> tasks) throws TaskQueueException;

	/**
	 * ���������¼
	 * 
	 * @param task
	 *            �������¼
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	public int putOneTask(Task task) throws TaskQueueException;

	/**
	 * �޸������¼������task��������ǰ��getTask��ȡ�� ����task��ID���޸�
	 * 
	 * @param ptask
	 *            ��ǰGetTask���û�õ������¼
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	boolean setTask(Task task) throws TaskQueueException;

	/**
	 * ɾ�������¼
	 * 
	 * @param task
	 *            ��ɾ�������¼������Ϊ��ǰͨ��GetTask���û�õ������¼
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	boolean deleteTask(Task task) throws TaskQueueException;

	/**
	 * �г�ָ�������������¼���ú��������о����������¼��
	 * 
	 * @param NextAddr
	 *            ����������ΪNULL��""ʱ�� ȡ���е�ַ�ļ�¼
	 * @param tasks
	 *            ���ز�ѯ�������
	 * @param count
	 *            ָ����Ҫ�оٵļ�¼��������ʱΪ�����¼�����м�¼����
	 * @throws TaskQueueException
	 *             ���в���ʧ�ܡ�
	 */
	void list(String NextAddr, int count, List<Task> tasks)
			throws TaskQueueException;

	/**
	 * �������������¼״̬Ϊδ����
	 * 
	 * @throws TaskQueueException
	 */
	boolean resetAllTask() throws TaskQueueException;

	/**
	 * ��ָ���������¼����
	 * 
	 * @param taskQueueName
	 *            �����¼��������
	 * @return
	 * @throws TaskQueueException
	 */
	boolean openTaskQueue(String taskQueueName) throws TaskQueueException;

	/**
	 * �ر������¼����
	 */
	void closeTaskQueue();

}