package com.greatsoft.transq.processor.api;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.ProcessorException;

/**
 * 处理过程接口定义
 * 
 * @author RAY
 * 
 */
public interface Processor {
	/**
	 * 处理消息数据并返回结果消息
	 * 
	 * @param message
	 *            输入消息
	 * @param addtionParam
	 *            附加处理参数，可用于在主控程序中加入附加的处理要求
	 * @return 处理结果
	 * @throws ProcessorException
	 *             当处理实现不能确切的在返回消息中返回处理成功失败信息时，抛出该异常
	 */
	ResultImp process(AbstractMessage message, Object addtionParam)
			throws ProcessorException;

}
