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
	 * 连接建立成功返回true，否则返回false
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
	 *            :Hiep系统的消息对象,不能为null
	 * @return 如果放入成功返回true，否则返回false
	 * @throws WSException
	 *             ：将消息对象放入HIEP系统异常
	 */
	public ResultImp put(WSMessage message) throws HeipWSException {
		if (message == null) {
			System.out.println("传进来的消息为null");
			throw new HeipWSException();
		}
		ResultImp result = new ResultImp();
		result = stub.exchange(message);
		return result;
	}

	/**
	 * 创建一条交换系统的消息。
	 * 
	 * @param dataType
	 *            ：消息的业务数据类型
	 * @param priority
	 *            ：消息优先级
	 * @param dataCompressType
	 *            ：数据是否压缩，0为不压缩，1为压缩
	 * @param relativeExpiredTime
	 *            ：相对失效时间，单位min
	 * @param sourceAddress
	 *            ：数据发送的源地址
	 * @param targetAddress
	 *            :传送的目的地址
	 * @param sourceDataName
	 *            ：要传送的数据文件的名字
	 * @param data
	 *            :要传送的数据文件
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
	 * 关闭连接
	 */
	public void close() {
		this.stub = null;
	}
}
