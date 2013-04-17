package com.greatsoft.transq.processor.api;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.ProcessorException;

public class Processor000 implements Processor {

	@Override
	public ResultImp process(AbstractMessage message, Object addtionParam)
			throws ProcessorException {

		System.out.println("消息处理完毕，其目的地址："
				+ message.getEnvelope().getTargetAddress());

		return new ResultImp(0, "消息处理完毕");
	}

}
