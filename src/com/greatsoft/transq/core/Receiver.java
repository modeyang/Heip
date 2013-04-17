package com.greatsoft.transq.core;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.exception.MessageFormatException;
import com.greatsoft.transq.exception.ReceiveException;

/**
 * 消息接收器
 * @author RAY
 *
 */
public interface Receiver {
	/**
	 * 接收一个消息
	 * @return 接收到的消息， null，无消息
	 * @throws ReceiveException 当接收器实现无法接收到消息时抛出
	 * @throws MessageFormatException 当接收到消息解析异常时抛出
	 */
	AbstractMessage get()throws ReceiveException,MessageFormatException;
}
