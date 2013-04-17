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
	 * ���ӽ����ɹ�����true�����򷵻�false
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
	 * �ر�����
	 */
	public void close() {
		connector.disConnect();
	}

	/**
	 * @param message
	 *            :Hiepϵͳ����Ϣ����,����Ϊnull
	 * @return �������ɹ�����true�����򷵻�false
	 * @throws CommunicationException
	 *             ������Ϣ���������Ϣ�м���쳣����������Ϣ����Ϣ�м��ʧ��ʱ���׳��쳣
	 * @throws HiepMessageException
	 *             :�������messageΪ�գ��׳��쳣�����������ʱ�����Ϣ�ļ�·��Ϊnullʱ���׳��쳣
	 *             �������л���Ϣ����ʧ��ʱ�׳��쳣.
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
	 *            :Ҫ���͵������ļ�,���ֽ�������ʽ
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
	 * ���ļ���ʽ�����ݺ�һЩ��Ҫ��Ҫ�أ���װ��һ����Ϣ����
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
	 *            :Ҫ���͵����ݣ����ļ���ʽ
	 * @return �ɹ��򷵻�һ��message�������ȡ��Ϣ�ļ�������ʧ�ܷ���null
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
