package com.greatsoft.transq.message.webservice;

import java.io.File;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

import com.greatsoft.transq.core.message.EnvelopeImp;
import com.greatsoft.transq.core.message.ResultImp;

public class TestMainDemo {

	private static int dataCompressType;
	private static String dataType = "";
	private static String fromAddress;
	private static int priority;
	private static String sourceAddress;
	private static String sourceDataName;
	private static String targetAddress;
	private static String filePath;

	public static void main(String[] args) {

		if (args.length != 8) {
			System.out.println("�����������");
		} else {
			dataCompressType = Integer.parseInt(args[0]);
			dataType = args[1];
			fromAddress = args[2];
			priority = Integer.parseInt(args[3]);
			sourceAddress = args[4];
			sourceDataName = args[5];

			targetAddress = args[6];
			filePath = args[7];
		}

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		/** ���÷���ӿ� */
		factory.setServiceClass(IWSCenterStub.class);
		/** ����HIEP���ݽ���ƽ̨Web������ĵ�ַ */
		factory.setAddress("http://localhost:9000/transq/services/centerService");

		IWSCenterStub stub = (IWSCenterStub) factory.create();
		/** ������Ӧ���� */
		ResultImp result = new ResultImp();

		/** ��ȡһ���������Ϣ */
		WSMessage wsmessage = getWSMessage();
		/** ���÷���ӿ� */
		result = stub.exchange(wsmessage);

		if (result == null) {
			System.out.println("����ʧ��");
		} else if (result.getReturnMessage() == null) {
			System.out.println("������ϢΪnull");
		} else {
			System.out.println("���óɹ�" + result.getReturnCode() + "\t"
					+ result.getReturnInfo());
		}
	}

	public static WSMessage getWSMessage() {
		EnvelopeImp envelope = new EnvelopeImp();
		envelope.setCreateDateTime(new java.util.Date().toString());
		envelope.setDataCompressType(dataCompressType);
		envelope.setDataIdentify(UUID.randomUUID().toString());
		envelope.setDataType(dataType);
		envelope.setExpiredTime("30");
		envelope.setFromAddress(fromAddress);
		envelope.setOriginalFileLength(new File(filePath).length());
		envelope.setPriority(priority);
		envelope.setSendIdentify(UUID.randomUUID().toString());
		envelope.setSourceAddress(sourceAddress);
		envelope.setSourceDataName(sourceDataName);
		envelope.setTargetAddress(targetAddress);
		envelope.setVersion("1.00");

		return new WSMessage(new DataHandler(new FileDataSource(new File(
				filePath))), envelope);
	}

}
