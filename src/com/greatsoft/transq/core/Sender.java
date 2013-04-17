package com.greatsoft.transq.core;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.CommunicationException;

/**
 * 通讯发送器
 * 
 * @author RAY
 * 
 */
public interface Sender {
	/**
	 * 发送消息
	 * 
	 * @param message
	 *            待发消息
	 * @return 远端返回的消息，对于异步发送器实现，该返回值的getMessage可能为空
	 * @throws CommunicationException
	 *             通讯链路出现问题时返回。
	 */
	ResultImp put(AbstractMessage message) throws CommunicationException;

	/**
	 * 发送消息
	 * 
	 * @param message
	 *            待发消息
	 * @param filePath
	 *            待发消息的落地文件所在目录
	 * @return 远端返回的消息，对于异步发送器实现，该返回值的getMessage可能为空
	 * @throws CommunicationException
	 *             通讯链路出现问题时返回。
	 */
	ResultImp put(AbstractMessage message, String filePath)
			throws CommunicationException;
}
