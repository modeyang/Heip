package com.greatsoft.transq.core;

import java.net.ConnectException;

/**
 * ͨѶ����
 * 
 * @author RAY
 * 
 */
public interface Connector {
	/**
	 * ������ָ����ַ������.
	 */
	void connect() throws ConnectException;

	/**
	 * ���������Ƿ���.
	 * 
	 * @return ���������ӷ���ture��δ���ӷ���false
	 */
	Boolean isConnected();

	/**
	 * �Ͽ�����
	 */
	void disConnect();

	/**
	 * �������Ͷ�
	 * 
	 * @return �ɹ��������Ͷ˷���Sender�����򷵻�false
	 */
	Sender createSender();

	/**
	 * �������ն�
	 * 
	 * @return �ɹ��������ն˷���Sender�����򷵻�false
	 */
	Receiver createReceiver();
}
