package com.greatsoft.transq.core;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.exception.MessageFormatException;
import com.greatsoft.transq.exception.ReceiveException;

/**
 * ��Ϣ������
 * @author RAY
 *
 */
public interface Receiver {
	/**
	 * ����һ����Ϣ
	 * @return ���յ�����Ϣ�� null������Ϣ
	 * @throws ReceiveException ��������ʵ���޷����յ���Ϣʱ�׳�
	 * @throws MessageFormatException �����յ���Ϣ�����쳣ʱ�׳�
	 */
	AbstractMessage get()throws ReceiveException,MessageFormatException;
}
