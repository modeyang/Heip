package com.greatsoft.transq.message.webservice;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.greatsoft.transq.core.message.EnvelopeImp;
import com.greatsoft.transq.core.message.ResultImp;

public class TestMainDemo2 {

	public static void main(String[] args) {
		while (true) {
			/** ģ�����û���������˷���webservice���� */
			new Thread(new Worker()).start();
			new Thread(new Worker()).start();
			new Thread(new Worker()).start();
			new Thread(new Worker()).start();
			new Thread(new Worker()).start();
			new Thread(new Worker()).start();

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/** ����һ��HIEPϵͳ����Ϣ */
	protected static WSMessage getWSMessage() {
		EnvelopeImp envelope = new EnvelopeImp();
		envelope.setCreateDateTime("asdf");
		envelope.setDataCompressType(0);
		envelope.setDataIdentify("000");
		envelope.setDataType("000");
		envelope.setExpiredTime("798");
		envelope.setFromAddress("����");
		envelope.setOriginalFileLength(798);
		envelope.setPriority(5);
		envelope.setSendIdentify("1234");
		envelope.setSourceAddress("����");
		envelope.setSourceDataName("asdf");
		envelope.setTargetAddress("����@����");
		envelope.setVersion("1.00");

		/** ����һ��Ҫ������ļ� */
		File temp = null;
		try {
			temp = getFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new WSMessage(new DataHandler(new FileDataSource(temp)),
				envelope);
	}

	/** ����һ��Ҫ������ļ� */
	private static File getFile() throws IOException {
		File temp = new File("temp");
		if (!temp.exists()) {
			temp.createNewFile();
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(temp));
		writer.write("Hello World!!");
		writer.close();
		return temp;
	}
}

class Worker implements Runnable {

	@Override
	public void run() {
		/** ����MTOM�������ļ� */
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("mtom-enabled", Boolean.TRUE);

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IWSCenterStub.class);
		factory.setAddress("http://localhost:9000/transq/services/centerService");
		factory.setProperties(props);

		IWSCenterStub stub = (IWSCenterStub) factory.create();

		/** �������ӳ�ʱ����Ӧ��ʱ */
		Client client = ClientProxy.getClient(stub);
		HTTPConduit conduit = (HTTPConduit) client.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setConnectionTimeout(120000);
		policy.setReceiveTimeout(120000);
		conduit.setClient(policy);

		ResultImp result = new ResultImp();
		WSMessage wsmessage = TestMainDemo2.getWSMessage();
		/** ����webservice */
		result = stub.exchange(wsmessage);

		if (result == null) {
			System.out.println("����ʧ��");
		} else {
			System.out.println("���óɹ�");
		}

	}
}
