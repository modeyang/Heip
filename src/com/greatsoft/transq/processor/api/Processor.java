package com.greatsoft.transq.processor.api;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.ProcessorException;

/**
 * ������̽ӿڶ���
 * 
 * @author RAY
 * 
 */
public interface Processor {
	/**
	 * ������Ϣ���ݲ����ؽ����Ϣ
	 * 
	 * @param message
	 *            ������Ϣ
	 * @param addtionParam
	 *            ���Ӵ�������������������س����м��븽�ӵĴ���Ҫ��
	 * @return ������
	 * @throws ProcessorException
	 *             ������ʵ�ֲ���ȷ�е��ڷ�����Ϣ�з��ش���ɹ�ʧ����Ϣʱ���׳����쳣
	 */
	ResultImp process(AbstractMessage message, Object addtionParam)
			throws ProcessorException;

}
