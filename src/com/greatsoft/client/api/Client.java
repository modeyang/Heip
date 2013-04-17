package com.greatsoft.client.api;

import java.io.File;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.AddressHelper;
import com.greatsoft.transq.core.Connector;
import com.greatsoft.transq.core.ConnectorAdapterImp;
import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.Envelope;
import com.greatsoft.transq.core.message.EnvelopeImp;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.message.MessageImp;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.CommunicationException;
import com.greatsoft.transq.exception.HiepMessageException;
import com.greatsoft.transq.message.queue.TLQAddress;
import com.greatsoft.transq.message.queue.TLQueue;
import com.greatsoft.transq.message.webservice.IWSCenterStub;
import com.greatsoft.transq.message.webservice.WSAddress;
import com.greatsoft.transq.message.webservice.WSConnector;
import com.greatsoft.transq.message.webservice.WSMessage;
import com.greatsoft.transq.utils.ByteBufferHelper;
import com.greatsoft.transq.utils.ConstantValue;

public class Client {
	private static final String messageDir = "./";
	private Connector connector;
	private Address address;
	private String addressString;

	public Client(String addressString) {
		this.addressString=addressString;
	}

	/**
	 * 连接建立成功返回true，否则返回false
	 * 
	 * @return
	 * @throws ConnectException
	 */
	public boolean connect() throws ConnectException {
		address = AddressHelper.parse(addressString);
		if(null==address){
			return false;
		}
		connector = ConnectorAdapterImp.getInstance().getConnector(address,
				null);
		if(null==connector){
			return false;
		}
		connector.connect();
		if (!connector.isConnected()) {
			return false;
		}
		return true;
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		connector.disConnect();
	}

	/**
	 * @param message
	 *            :Hiep系统的消息对象,不能为null
	 * @return 如果放入成功返回true，否则返回false
	 * @throws CommunicationException
	 *             ：将消息对象放入消息中间件异常。当放入消息到消息中间件失败时，抛出异常
	 * @throws HiepMessageException
	 *             :当传入的message为空，抛出异常，当传入的临时存放消息文件路径为null时，抛出异常
	 *             ，当序列化消息对象失败时抛出异常.
	 */
	public ResultImp put(AbstractMessage message)
			throws CommunicationException, HiepMessageException {
		if (message == null) {
			throw new HiepMessageException();
		}

		if (this.address instanceof WSAddress) {
			ResultImp result = new ResultImp();
			IWSCenterStub stub = WSConnector.getStub();
			result = stub.exchange((WSMessage) message);
			return result;
		} else if (this.address instanceof TLQAddress) {
			String sendIdentify = message.getEnvelope().getSendIdentify();
			if (sendIdentify == null
					|| sendIdentify.equals(ConstantValue.NULL_STRING)) {
				throw new HiepMessageException();
			}
			String messagePath = messageDir + sendIdentify;
			if (!MessageHelper.toFile(messagePath, message)) {
				throw new HiepMessageException();
			}
			Sender sender = connector.createSender();
			try {
				if (sender instanceof TLQueue) {
					return ((TLQueue) sender).put(message, messageDir);
				}

			} catch (CommunicationException e) {
				throw new CommunicationException();
			}

		}
		return null;

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
	 *            :要传送的数据文件,以字节流的形式
	 */
	public AbstractMessage createMessage(String dataType, int priority,
			int dataCompressType, int relativeExpiredTime,
			String sourceAddress, String fromAddress, String targetAddress,
			String sourceDataName, ByteBuffer data) {
		String dataIdentify = UUID.randomUUID().toString();
		Envelope envelope = new EnvelopeImp(dataIdentify, dataType, priority,
				dataCompressType, relativeExpiredTime, sourceAddress,
				fromAddress, targetAddress, sourceDataName);

		if (this.address instanceof TLQAddress) {
			AbstractMessage message = new MessageImp(envelope, data);
			return message;
		} else if (this.address instanceof WSAddress) {
			DataHandler datahandler = getDataHandler(data);
			AbstractMessage message = new WSMessage(datahandler,
					(EnvelopeImp) envelope);
			return message;
		}
		return null;
	}

	private DataHandler getDataHandler(ByteBuffer data) {
		File file = new File("temp");
		ByteBufferHelper.putByteBuffer(data, file.getPath());
		DataHandler dataHandler = new DataHandler(new FileDataSource(file));

		return dataHandler;
	}

	/**
	 * 将文件形式的数据和一些需要的要素，组装成一条消息对象
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
	 *            :要传送的数据，以文件形式
	 * @return 成功则返回一条message，否则获取信息文件的内容失败返回null
	 */
	public AbstractMessage createMessage(String dataType, int priority,
			int dataCompressType, int relativeExpiredTime,
			String sourceAddress, String fromAddress, String targetAddress,
			String sourceDataName, File data) {
		String dataIdentify = UUID.randomUUID().toString();
		Envelope envelope = new EnvelopeImp(dataIdentify, dataType, priority,
				dataCompressType, relativeExpiredTime, sourceAddress,
				fromAddress, targetAddress, sourceDataName);

		if (this.address instanceof TLQAddress) {
			ByteBuffer byteBuffer = ByteBufferHelper.getByteBuffer(data);
			if (null == byteBuffer) {
				return null;
			}
			return new MessageImp(envelope, byteBuffer);
		} else if (this.address instanceof WSAddress) {
			return new WSMessage(new DataHandler(new FileDataSource(data)),
					(EnvelopeImp) envelope);
		}

		return null;
	}

}
