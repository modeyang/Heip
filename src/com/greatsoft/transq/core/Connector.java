package com.greatsoft.transq.core;

import java.net.ConnectException;

/**
 * 通讯连接
 * 
 * @author RAY
 * 
 */
public interface Connector {
	/**
	 * 建立与指定地址的连接.
	 */
	void connect() throws ConnectException;

	/**
	 * 测试连接是否建立.
	 * 
	 * @return 已连接连接返回ture，未连接返回false
	 */
	Boolean isConnected();

	/**
	 * 断开连接
	 */
	void disConnect();

	/**
	 * 创建发送端
	 * 
	 * @return 成功创建发送端返回Sender，否则返回false
	 */
	Sender createSender();

	/**
	 * 创建接收端
	 * 
	 * @return 成功创建接收端返回Sender，否则返回false
	 */
	Receiver createReceiver();
}
