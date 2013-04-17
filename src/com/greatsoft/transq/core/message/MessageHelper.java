package com.greatsoft.transq.core.message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.message.Envelope;
import com.greatsoft.transq.core.message.EnvelopeImp;
import com.greatsoft.transq.core.message.MessageImp;
import com.greatsoft.transq.core.task.Task;
import com.greatsoft.transq.utils.ByteBufferHelper;
import com.greatsoft.transq.utils.CalenderAndString;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;

public class MessageHelper {
	private static Logger log = Log.getLog(MessageHelper.class);
	/**
	 * ����task��SendIdentify�ҵ�ԭmessage���л����ļ��������ļ������л�Ϊһ��message����
	 * */
	public static AbstractMessage loadMessage(Task task) {
		String filePath = Config.NEW_MESSAGE_FILE_DIRECTORY+ task.getSendIdentify();
		return getMessage(filePath);
	}
	
	/**
	 * �þ͵���Ϣ���µ�Ŀ�ĵ�ַ�����µ���Ϣ
	 * @param message
	 * @param targetAddress
	 * @return
	 */
	public static AbstractMessage getNewMessage(AbstractMessage message, String targetAddress) {
		Envelope newEnvelope = new EnvelopeImp(message.getEnvelope(),targetAddress);
		return new MessageImp(newEnvelope,message.getData());
	}
	/**
	 * ��һ��message�������л�����ļ��������л�Ϊһ��message����
	 * 
	 * @param ��Ҫ�����л����ļ�
	 * @return message����
	 */
	public static AbstractMessage getMessage(String filePath) {
		ByteBuffer byteBuffer = ByteBufferHelper.getByteBuffer(filePath);
		if (byteBuffer != null) {
			AbstractMessage message = getMessage(byteBuffer);
			if (message != null) {
				log.info("���ļ������л�Ϊ��Ϣ�ɹ���filePath="+filePath+"\n"+message);
				return message;
			}
		}
		log.error("���ļ������л�Ϊ��Ϣʧ�ܣ�filePath="+filePath);
		return null;
	}
	

	/**
	 * ��byteBuffer��ת��Ϊһ��Message���ɹ��򷵻�һ��Message��ʧ���򷵻�null
	 * 
	 * @param byteBuffer
	 * @return Message or null
	 */
	public static AbstractMessage getMessage(ByteBuffer byteBuffer) {
		Envelope envelope;
		envelope = getEnvelope(byteBuffer);
		if (envelope != null) {
			ByteBuffer byteBufferData = byteBuffer.slice();
			if (byteBufferData != null) {
				return new MessageImp(envelope, byteBufferData);
			}
			log.error("������Ϣ�е�����ʧ��");
			return null;
		}
		log.error("������Ϣ�е��ŷ�ͷ����ʧ��");
		return null;
	}

	/**
	 * ��һ��message���л����ɵ�ByteBuffer�����ŷ�ͷ���ֽ����������ɹ��򷵻�Envelope�����򷵻�null
	 * 
	 * @param byteBuffer
	 * @return Envelope or null
	 * @throws UnsupportedEncodingException
	 */
	public static Envelope getEnvelope(ByteBuffer byteBuffer) {
		byteBuffer.order(ConstantValue.HIEP_MESSAGE_BYTEORDER);
		String hiepID = getStringFromByteBuffer(byteBuffer);
		if ((hiepID == null) || hiepID.equals(ConstantValue.NULL_STRING)) {
			log.error("������Ϣ�ŷ�ͷ��hiepIDʧ��");
			return null;
		}
		if (!hiepID.equals(ConstantValue.HIEP_MESSAGE_ID_STRING)) {
			log.error("��Ϣ���Ǳ�׼��HIEP����ƽ̨��Ϣ��hiepID="+hiepID);
			return null;
		}
		String hiepMessageEncoding = getStringFromByteBuffer(byteBuffer);
		if (!hiepMessageEncoding.equals(ConstantValue.HIEP_MESSAGE_ENCODING)) {
			log.error("��Ϣ�ı��뷽ʽ����hiepMessageEncoding="+hiepMessageEncoding);
			return null;
		}
		int messageDataByteOffset = byteBuffer.getInt();
		if (messageDataByteOffset <= 0) {
			log.error("��Ϣ������ƫ��������������messageDataByteOffset="+messageDataByteOffset);
			return null;
		}
		int priority = byteBuffer.getInt();
		if (priority < 0 || priority > 9) {
			log.error("��Ϣ�����ȼ�����priority="+priority);
			return null;
		}
		int dataCompressType = byteBuffer.getInt();
		if (dataCompressType < 0 || dataCompressType > 1) {
			log.error("��Ϣ������ѹ�����ʹ���dataCompressType="+dataCompressType);
			return null;
		}
		long originalFileLength = byteBuffer.getLong();
		if (originalFileLength <= 0) {
			log.error("��Ϣ��ԭʼ���ݳ��ȷ���������originalFileLength="+originalFileLength);
			return null;
		}
		String version = getStringFromByteBuffer(byteBuffer);
		if (version == null || version.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ�İ汾���ͽ���ʧ��");
			return null;
		}
		String dataIdentify = getStringFromByteBuffer(byteBuffer);
		if (dataIdentify == null || dataIdentify.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ�����ݱ�־����ʧ��");
			return null;
		}
		String sendIdentify = getStringFromByteBuffer(byteBuffer);
		if (sendIdentify == null || sendIdentify.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ�ķ��ͱ�־����ʧ��");
			return null;
		}
		String dataType = getStringFromByteBuffer(byteBuffer);
		if (dataType == null || dataType.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ���������ͽ���ʧ��");
			return null;
		}
		String createDateTime = getStringFromByteBuffer(byteBuffer);
		if (createDateTime == null || createDateTime.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ�Ĳ���ʱ�����ʧ��");
			return null;
		}
		String expiredTime = getStringFromByteBuffer(byteBuffer);
		if (expiredTime == null || expiredTime.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ��ʧЧʱ�����ʧ��");
			return null;
		}
		String sourceAddress = getStringFromByteBuffer(byteBuffer);
		if (sourceAddress == null || sourceAddress.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ��Դ��ַ����ʧ��");
			return null;
		}
		String fromAddress = getStringFromByteBuffer(byteBuffer);
		if (fromAddress == null || fromAddress.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ����һ�ν�����ַ����ʧ��");
			return null;
		}
		String targetAddress = getStringFromByteBuffer(byteBuffer);
		if (targetAddress == null || targetAddress.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ��Ŀ�ĵ�ַ����ʧ��");
			return null;
		}
		String sourceDataName = getStringFromByteBuffer(byteBuffer);
		if (sourceDataName == null || sourceDataName.equals(ConstantValue.NULL_STRING)) {
			log.error("��Ϣ�������ļ����ƽ���ʧ��");
			return null;
		}
		return new EnvelopeImp(version, dataIdentify, sendIdentify,
				dataType, priority, dataCompressType, createDateTime,
				expiredTime, sourceAddress, fromAddress, targetAddress,
				sourceDataName, originalFileLength);
	}

	/**
	 * ����Ϣ���л���������Ϣ�����С�
	 * 
	 * @param newMessage
	 * @return
	 */
	public static boolean putMessageToReceived(AbstractMessage message) {
		String filePath=Config.RECEIVED_MESSAGE_FILE_DIRECTORY+message.getEnvelope().getSendIdentify();
		return toFile(filePath,message);
	}
	
	/**
	 * ����Ϣ���л�������Ϣ�����С�
	 * 
	 * @param newMessage
	 * @return
	 */
	public static boolean putMessageToNew(AbstractMessage message) {
		String filePath=Config.NEW_MESSAGE_FILE_DIRECTORY+message.getEnvelope().getSendIdentify();
		return toFile(filePath,message);
	}
	
	/**
	 * ����Ϣ���л���������Ϣ�����С�
	 * @param message
	 * @return
	 */
	public static boolean putMessageToSend(AbstractMessage message) {
		String filePath=Config.SEND_MESSAGE_FILE_DIRECTORY+message.getEnvelope().getSendIdentify();
		return toFile(filePath,message);
	}
	/**
	 * ���л�һ��message��ָ�����ļ�
	 * 
	 * @param filePath
	 *            ָ���ļ�·��
	 * @param message
	 *            ��Ҫ���л���message����
	 * @return
	 * @throws IOException
	 */
	public static boolean toFile(String filePath, AbstractMessage message) {
		Envelope envelope=message.getEnvelope();
		String sendIdentify=envelope.getSendIdentify();
		ByteBuffer byteBuffer=putEnvelope(envelope);
		if (byteBuffer == null) {
			log.error("���л���Ϣ�ŷ�ͷ����ʧ��:SendIdentify="+sendIdentify);
			return false;
		}
		byteBuffer.flip();
		ByteBuffer dataBuffer=message.getData();
		
		/*if(message.getDataFileName()==ConstantValue.NULL_STRING ||message.getDataFileName() ==null){
			dataBuffer=message.getData();
		}else{
			dataBuffer=ByteBufferHelper.getByteBuffer(message.getDataFileName());
		}*/
		if (dataBuffer == null) {
			log.error("���л���Ϣ���ݲ���ʧ��:SendIdentify="+sendIdentify);
			return false;
		}
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(filePath, "rw");
		} catch (FileNotFoundException e) {
			log.error("û���ҵ�ָ�����ļ���filePath=" + filePath+e.getMessage());
			return false;
		}
		FileChannel fileChannel = file.getChannel();
		FileLock fileLock = null;
		try {
			fileLock = fileChannel.tryLock();
		} catch (IOException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)+e.getMessage());
			try {
				file.close();
				file = null;
			} catch (IOException e1) {
				log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e1.getMessage());
			}
			return false;
		}
		try {
			fileLock.release();
		} catch (IOException e) {
			log.error(ErrorCode.FILE_IO_ERROR_INFO+e.getMessage());	
			try {
				fileChannel.close();
				fileChannel = null;
				file.close();
				file = null;
			} catch (IOException e1) {
				log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e1.getMessage());
			}
			return false;
		}
		dataBuffer.position(0);
		try {
			if (fileChannel.write(byteBuffer) == 0 || fileChannel.write(dataBuffer) == 0) {
				log.error("д�ļ�ʧ�ܣ�filePath="+filePath);
				try {
					fileChannel.close();
					fileChannel = null;
					file.close();
					file = null;
				} catch (IOException e) {
					log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e.getMessage());
				}
				return false;
			}
		} catch (IOException e) {
			log.error("д�ļ�ʧ�ܣ�filePath="+filePath+ e.getMessage());
			return false;
		}finally{
			try {
				if(fileChannel!=null){
					fileChannel.close();
					fileChannel = null;
				}
				if(file!=null){
					file.close();
					file = null;
				}
			} catch (IOException e) {
				log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e.getMessage());
			}
		}
		return true;
	}

	/**
	 * ���л�һ��message��ָ�����ļ�
	 * 
	 * @param filePath
	 *            ָ���ļ�·��
	 * @param message
	 *            ��Ҫ���л���message����
	 * @return
	 * @throws IOException
	 */
	public static boolean toFile(String filePath,String dataFilePath, AbstractMessage message) {
		Envelope envelope=message.getEnvelope();
		String sendIdentify=envelope.getSendIdentify();
		ByteBuffer byteBuffer=putEnvelope(envelope);
		if (byteBuffer == null) {
			log.error("���л���Ϣ�ŷ�ͷ����ʧ��:SendIdentify="+sendIdentify);
			return false;
		}
		byteBuffer.flip();
		ByteBuffer dataBuffer=null;
		
		dataBuffer=ByteBufferHelper.getByteBuffer(dataFilePath);
		if (dataBuffer == null) {
			log.error("���л���Ϣ���ݲ���ʧ��:SendIdentify="+sendIdentify);
			return false;
		}
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(filePath, "rw");
		} catch (FileNotFoundException e) {
			log.error("û���ҵ�ָ�����ļ���filePath=" + filePath+e.getMessage());
			return false;
		}
		FileChannel fileChannel = file.getChannel();
		FileLock fileLock = null;
		try {
			fileLock = fileChannel.tryLock();
		} catch (IOException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)+e.getMessage());
			try {
				file.close();
				file = null;
			} catch (IOException e1) {
				log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e1.getMessage());
			}
			return false;
		}
		try {
			fileLock.release();
		} catch (IOException e) {
			log.error(ErrorCode.FILE_IO_ERROR_INFO+e.getMessage());	
			try {
				fileChannel.close();
				fileChannel = null;
				file.close();
				file = null;
			} catch (IOException e1) {
				log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e1.getMessage());
			}
			return false;
		}
		dataBuffer.position(0);
		try {
			if (fileChannel.write(byteBuffer) == 0 || fileChannel.write(dataBuffer) == 0) {
				log.error("д�ļ�ʧ�ܣ�filePath="+filePath);
				try {
					fileChannel.close();
					fileChannel = null;
					file.close();
					file = null;
				} catch (IOException e) {
					log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e.getMessage());
				}
				return false;
			}
		} catch (IOException e) {
			log.error("д�ļ�ʧ�ܣ�filePath="+filePath+ e.getMessage());
			return false;
		}finally{
			try {
				if(fileChannel!=null){
					fileChannel.close();
					fileChannel = null;
				}
				if(file!=null){
					file.close();
					file = null;
				}
			} catch (IOException e) {
				log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e.getMessage());
			}
		}
		return true;
	}

/**
 * ����Ϣ���ŷ�ͷ���л���ByteBuffer����
 * @param envelope
 * @return ByteBuffer | null
 */
	public static ByteBuffer putEnvelope(Envelope envelope) {
		String sendIdentify=envelope.getSendIdentify();
		int envolopeByteCount = 0;
		byte[] hiepIDByte;
		try {
			hiepIDByte = ConstantValue.HIEP_MESSAGE_ID_STRING.getBytes(ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л�hiepID�쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int hiepIDByteCount = hiepIDByte.length;
		envolopeByteCount += hiepIDByteCount + 4;
		byte[] hiepMessageEncodingByte;
		try {
			hiepMessageEncodingByte = ConstantValue.HIEP_MESSAGE_ENCODING
					.getBytes(ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л�HiepMessageEncoding�쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int hiepMessageEncodingByteCount = hiepMessageEncodingByte.length;
		envolopeByteCount += hiepMessageEncodingByteCount + 4;
		envolopeByteCount += 4 + 4 + 8;
		/** datatype,priority,originalFileLength */
		byte[] versionByte;
		try {
			versionByte = envelope.getVersion().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л��汾�����쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int versionByteCount = versionByte.length;
		envolopeByteCount += 4 + versionByteCount;
		byte[] dataIdentifyByte;
		try {
			dataIdentifyByte = envelope.getDataIdentify().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л����ݱ�־�쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int dataIdentifyByteCount = dataIdentifyByte.length;
		envolopeByteCount += 4 + dataIdentifyByteCount;
		byte[] sendIdentifyByte;
		try {
			sendIdentifyByte = envelope.getSendIdentify().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л����ͱ�־�쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int sendIdentifyByteCount = sendIdentifyByte.length;
		envolopeByteCount += 4 + sendIdentifyByteCount;
		byte[] dataTypeByte;
		try {
			dataTypeByte = envelope.getDataType().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л����������쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int dataTypeByteCount = dataTypeByte.length;
		envolopeByteCount += 4 + dataTypeByteCount;

		byte[] createDateTimeByte;
		try {
			createDateTimeByte = envelope.getCreateDateTime().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л�����ʱ���쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int createDateTimeByteCount = createDateTimeByte.length;
		envolopeByteCount += 4 + createDateTimeByteCount;

		byte[] expiredTimeByte;
		try {
			expiredTimeByte = envelope.getExpiredTime().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л�ʧЧʱ���쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int expiredTimeByteCount = expiredTimeByte.length;
		envolopeByteCount += 4 + expiredTimeByteCount;

		byte[] sourceAddressByte;
		try {
			sourceAddressByte = envelope.getSourceAddress().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л�Դ��ַ�쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int sourceAddressByteCount = sourceAddressByte.length;
		envolopeByteCount += 4 + sourceAddressByteCount;
		byte[] targetAddressByte;
		try {
			targetAddressByte = envelope.getTargetAddress().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л�Ŀ�ĵ�ַ�쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int targetAddressByteCount = targetAddressByte.length;
		envolopeByteCount += 4 + targetAddressByteCount;
		byte[] fromAddressByte;
		try {
			fromAddressByte = envelope.getFromAddress().getBytes(ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л�������ַ�쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int fromAddressByteCount = fromAddressByte.length;
		envolopeByteCount += 4 + fromAddressByteCount;
		byte[] sourceDataNameByte;
		try {
			sourceDataNameByte = envelope.getSourceDataName().getBytes(
					ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error("�ŷ�ͷ���л�Դ���������쳣��SendIdentify="+sendIdentify+e.getMessage());
			return null;
		}
		int sourceDataNameByteCount = sourceDataNameByte.length;
		envolopeByteCount += 4 + sourceDataNameByteCount;
		int messageDataByteOffset = envolopeByteCount + 4;
		
		ByteBuffer byteBuffer = ByteBuffer.allocate(messageDataByteOffset);
		byteBuffer.order(ConstantValue.HIEP_MESSAGE_BYTEORDER);
		byteBuffer.putInt(hiepIDByteCount);
		byteBuffer.put(hiepIDByte);
		byteBuffer.putInt(hiepMessageEncodingByteCount);
		byteBuffer.put(hiepMessageEncodingByte);
		byteBuffer.putInt(messageDataByteOffset);
		byteBuffer.putInt(envelope.getPriority());
		byteBuffer.putInt(envelope.getDataCompressType());
		byteBuffer.putLong(envelope.getOriginalFileLength());
		byteBuffer.putInt(versionByteCount);
		byteBuffer.put(versionByte);
		byteBuffer.putInt(dataIdentifyByteCount);
		byteBuffer.put(dataIdentifyByte);
		byteBuffer.putInt(sendIdentifyByteCount);
		byteBuffer.put(sendIdentifyByte);
		byteBuffer.putInt(dataTypeByteCount);
		byteBuffer.put(dataTypeByte);
		byteBuffer.putInt(createDateTimeByteCount);
		byteBuffer.put(createDateTimeByte);
		byteBuffer.putInt(expiredTimeByteCount);
		byteBuffer.put(expiredTimeByte);
		byteBuffer.putInt(sourceAddressByteCount);
		byteBuffer.put(sourceAddressByte);
		byteBuffer.putInt(fromAddressByteCount);
		byteBuffer.put(fromAddressByte);
		byteBuffer.putInt(targetAddressByteCount);
		byteBuffer.put(targetAddressByte);
		byteBuffer.putInt(sourceDataNameByteCount);
		byteBuffer.put(sourceDataNameByte);
		return byteBuffer;
	}
	
	public static String getStringFromByteBuffer(ByteBuffer byteBuffer){
		int stringByteCount =0;
		byte[] stringByte =null;
		stringByteCount = byteBuffer.getInt();
		stringByte = new byte[stringByteCount];
		try{
		byteBuffer.get(stringByte, 0, stringByteCount);
		}catch(BufferUnderflowException e){
			log.error(e.getMessage());
			return null;
		}catch(IndexOutOfBoundsException e){
			log.error(e.getMessage());
			return null;
		}
		String string = null;
		try {
			string = new String(stringByte, 0, stringByteCount,ConstantValue.HIEP_MESSAGE_ENCODING);
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage());
			return null;
		}
		return string;
	}

	public static Calendar getCalendarFromByteBuffer(int dateTimeByteCount,ByteBuffer byteBuffer){
		byte[] dateTimeByte = new byte[dateTimeByteCount];
		byteBuffer.get(dateTimeByte, 0, dateTimeByteCount);
		String dateTimeString = null;
		try{
			dateTimeString = new String(dateTimeByte, 0, dateTimeByteCount,ConstantValue.HIEP_MESSAGE_ENCODING);
			}catch(UnsupportedEncodingException e){
				log.error(e.getMessage());
				return null;
			}catch(IndexOutOfBoundsException e){
				log.error(e.getMessage());
				return null;
			}
		if (dateTimeString == null || dateTimeString.equals(ConstantValue.NULL_STRING)) {
			return null;
		}
		return CalenderAndString.stringToCalendar(dateTimeString);
	}
/**����Ϣ��Ŀ�ĵ�ַ��Դ��ַ��ת�������µ���Ϣ*/
	public static AbstractMessage getNewMessage(AbstractMessage message) {
		// TODO Auto-generated method stub
		return null;
	}
}
