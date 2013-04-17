package com.greatsoft.transq.message.webservice;

import java.util.Calendar;
import java.util.UUID;
import javax.activation.DataHandler;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import com.greatsoft.transq.core.message.EnvelopeImp;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.HeipWSException;
import com.greatsoft.transq.utils.ConstantValue;

public class WSClient {
	private String address = "";
	private IWSCenterStub stub = null;

	public WSClient(String address) {
		this.address = address;
	}

	/**
	 * ���ӽ����ɹ�����true�����򷵻�false
	 * 
	 * @return
	 * @throws ConnectException
	 */
	public boolean connect() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IWSCenterStub.class);

		factory.setAddress(this.address);
		this.stub = (IWSCenterStub) factory.create();
		return true;
	}

	/**
	 * @param message
	 *            :Hiepϵͳ����Ϣ����,����Ϊnull
	 * @return �������ɹ�����true�����򷵻�false
	 * @throws WSException
	 *             ������Ϣ�������HIEPϵͳ�쳣
	 */
	public ResultImp put(WSMessage message) throws HeipWSException {
		if (message == null) {
			System.out.println("����������ϢΪnull");
			throw new HeipWSException();
		}
		ResultImp result = new ResultImp();
		result = stub.exchange(message);
		return result;
	}

	/**
	 * ����һ������ϵͳ����Ϣ��
	 * 
	 * @param dataType
	 *            ����Ϣ��ҵ����������
	 * @param priority
	 *            ����Ϣ���ȼ�
	 * @param dataCompressType
	 *            �������Ƿ�ѹ����0Ϊ��ѹ����1Ϊѹ��
	 * @param relativeExpiredTime
	 *            �����ʧЧʱ�䣬��λmin
	 * @param sourceAddress
	 *            �����ݷ��͵�Դ��ַ
	 * @param targetAddress
	 *            :���͵�Ŀ�ĵ�ַ
	 * @param sourceDataName
	 *            ��Ҫ���͵������ļ�������
	 * @param data
	 *            :Ҫ���͵������ļ�
	 */
	public WSMessage createMessage(String dataType, int priority,
			int dataCompressType, int relativeExpiredTime, String fromAddress,
			String targetAddress, String sourceDataName, DataHandler data) {

		EnvelopeImp envelope = new EnvelopeImp();
		envelope.setVersion(ConstantValue.MESSAGE_VERSION);
		envelope.setDataIdentify(UUID.randomUUID().toString());
		Calendar now = Calendar.getInstance();
		// String createTime = CalenderAndString.calendarToString(now);
		envelope.setCreateDateTime(now.toString());
		now.add(Calendar.MINUTE, relativeExpiredTime);
		envelope.setExpiredTime(now.toString());
		envelope.setDataType(dataType);
		envelope.setPriority(priority);
		envelope.setDataCompressType(dataCompressType);
		envelope.setSourceAddress(fromAddress);
		envelope.setFromAddress(fromAddress);
		envelope.setTargetAddress(targetAddress);
		envelope.setSourceDataName(sourceDataName);
		envelope.setOriginalFileLength(1000);
		envelope.setSendIdentify(ConstantValue.UUID_SEPERATER
				+ UUID.randomUUID().toString());

		WSMessage message = new WSMessage(data, envelope);

		return message;
	}

	/**
	 * �ر�����
	 */
	public void close() {
		this.stub = null;
	}
}
