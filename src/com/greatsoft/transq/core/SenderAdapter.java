package com.greatsoft.transq.core;

/**
 * ���ݵ�ַ��ȡSenderʵ��
 * @author RAY
 *
 */
public interface SenderAdapter {

	/**
	 * ���ݵ�ַ��ȡSenderʵ��
	 * @param address �Է�ͨѶ��ַ
	 * @return ��ͨѶ��ַƥ��ķ�������null���������������ַƥ��ķ�������
	 */
	Sender getSender(Address address);
	
}
