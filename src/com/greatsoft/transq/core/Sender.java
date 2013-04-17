package com.greatsoft.transq.core;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.CommunicationException;

/**
 * ͨѶ������
 * 
 * @author RAY
 * 
 */
public interface Sender {
	/**
	 * ������Ϣ
	 * 
	 * @param message
	 *            ������Ϣ
	 * @return Զ�˷��ص���Ϣ�������첽������ʵ�֣��÷���ֵ��getMessage����Ϊ��
	 * @throws CommunicationException
	 *             ͨѶ��·��������ʱ���ء�
	 */
	ResultImp put(AbstractMessage message) throws CommunicationException;

	/**
	 * ������Ϣ
	 * 
	 * @param message
	 *            ������Ϣ
	 * @param filePath
	 *            ������Ϣ������ļ�����Ŀ¼
	 * @return Զ�˷��ص���Ϣ�������첽������ʵ�֣��÷���ֵ��getMessage����Ϊ��
	 * @throws CommunicationException
	 *             ͨѶ��·��������ʱ���ء�
	 */
	ResultImp put(AbstractMessage message, String filePath)
			throws CommunicationException;
}
