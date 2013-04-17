package com.greatsoft.transq.message.webservice;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.greatsoft.transq.core.message.ResultImp;
import com.tongtech.org.apache.log4j.Logger;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public class WSCenterStub implements IWSCenterStub {
	private static Logger log = Logger.getLogger(WSCenterStub.class);

	@WebMethod
	@Override
	public ResultImp exchange(WSMessage message) {
		log.info("����exchange������ʼ...");
		log.info("�յ�һ���ţ�");
		print(message);

		/** �����ź�ҵ����������ƴ�ӳ�key�� */
		String key = message.getEnvelope().getTargetAddress().split("@")[0]
				+ message.getEnvelope().getDataType();
		log.info("key:" + key);

		/** ����key�ҵ���Ӧ��������processor��ַ **/
		String address = WSServerMain.addressmap.get(key);
		log.info("��ַ��" + address);

		FutureTask<ResultImp> future = new FutureTask<ResultImp>(
				new ProcessorWorker(address, message));
		new Thread(future).start();

		ResultImp result = null;

		try {
			result = future.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		System.gc();
		return result;
	}

	private void print(WSMessage message) {
		log.info(message.getEnvelope().toString());
	}

}