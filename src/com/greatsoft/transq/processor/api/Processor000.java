package com.greatsoft.transq.processor.api;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.ProcessorException;

public class Processor000 implements Processor {

	@Override
	public ResultImp process(AbstractMessage message, Object addtionParam)
			throws ProcessorException {

		System.out.println("��Ϣ������ϣ���Ŀ�ĵ�ַ��"
				+ message.getEnvelope().getTargetAddress());

		return new ResultImp(0, "��Ϣ�������");
	}

}
