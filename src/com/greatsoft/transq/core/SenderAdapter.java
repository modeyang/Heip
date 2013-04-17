package com.greatsoft.transq.core;

/**
 * 根据地址获取Sender实例
 * @author RAY
 *
 */
public interface SenderAdapter {

	/**
	 * 根据地址获取Sender实例
	 * @param address 对方通讯地址
	 * @return 与通讯地址匹配的发送器。null，不存在与输入地址匹配的发送器。
	 */
	Sender getSender(Address address);
	
}
