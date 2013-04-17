package com.greatsoft.transq.message.queue;

import javax.jms.JMSException;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Receiver;
import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.Envelope;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.message.ResultHelper;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.CommunicationException;
import com.greatsoft.transq.exception.MessageFormatException;
import com.greatsoft.transq.exception.ReceiveException;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;
import com.tongtech.jms.FileMessage;
import com.tongtech.tmqi.jmsclient.QueueReceiverImpl;
import com.tongtech.tmqi.jmsclient.QueueSenderImpl;

/**
 * tonglingkQueue�Ľ���ʵ�֣�ͬʱʵ��sender��receiver
 * 
 * @author RAY
 * 
 */
public class TLQueue implements Receiver, Sender {
	/** ϵͳ��־��¼ */
	private static Logger log = Log.getLog(TLQueue.class);
	/** Ԥ������������������չ */
	private Object options;
	private QueueReceiverImpl queueReceiver;
	private QueueSenderImpl queueSender;

	public TLQueue(QueueReceiverImpl queueReceiver,QueueSenderImpl queueSender, Object options) {
		this.options = options;
		this.queueReceiver = queueReceiver;
		this.queueSender = queueSender;
	}

	public TLQueue(QueueReceiverImpl queueReceiver, Object options) {
		this.options = options;
		this.queueReceiver = queueReceiver;
		this.queueSender = null;
	}

	public TLQueue(QueueSenderImpl queueSender, Object options) {
		this.options = options;
		this.queueSender = queueSender;
		this.queueReceiver = null;
	}

	@Override
	public ResultImp put(AbstractMessage message) throws CommunicationException {
		Envelope envelope = message.getEnvelope();
		String sendIdentify=envelope.getSendIdentify();
		log.info("TLQ��ʼ������Ϣ:SendIdentify="+sendIdentify);
		FileMessage fileMessage = null;
		try {
			fileMessage = queueSender.getSession().createFileMessage(Config.NEW_MESSAGE_FILE_DIRECTORY + sendIdentify);
			fileMessage.setJMSPriority(envelope.getPriority());
			fileMessage.setJMSExpiration(Long.parseLong(envelope.getExpiredTime()));
			fileMessage.setJMSType(envelope.getDataType());
		} catch (JMSException e){
			log.error(ErrorCode.getErrorMessage(ErrorCode.CREATE_TLQ_MESSAGE_ERROR)+"SendIdentify="+sendIdentify+e.getMessage());
			return ResultHelper.getResult(ConstantValue.PUT_MESSAGE_FAILED);
		}
		try {
			queueSender.send(fileMessage);
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.PUT_MESSAGE_ERROR)+"SendIdentify="+sendIdentify+ e.getMessage());
		
			return ResultHelper.getResult(ConstantValue.PUT_MESSAGE_FAILED);
		}
		log.info("TLQ������Ϣ�ɹ�:SendIdentify="+sendIdentify);
		return ResultHelper.getResult(ConstantValue.PUT_MESSAGE_SUCCESS);
	}
	
	@Override
	public ResultImp put(AbstractMessage message, String fileDirectoryPath)throws CommunicationException {
		Envelope envelope = message.getEnvelope();
		String sendIdentify=envelope.getSendIdentify();
		log.info("TLQ��ʼ������Ϣ:SendIdentify="+sendIdentify);
		FileMessage fileMessage = null;
		try {
			fileMessage = queueSender.getSession().createFileMessage(fileDirectoryPath+sendIdentify);
			fileMessage.setJMSPriority(envelope.getPriority());
			fileMessage.setJMSExpiration(Long.parseLong(envelope.getExpiredTime()));
			fileMessage.setJMSType(envelope.getDataType());
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.CREATE_TLQ_MESSAGE_ERROR)+"SendIdentify="+sendIdentify+e.getMessage());
			return ResultHelper.getResult(ConstantValue.PUT_MESSAGE_FAILED);
		}
		try {
			queueSender.send(fileMessage);
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.PUT_MESSAGE_ERROR)+"SendIdentify="+sendIdentify+ e.getMessage());
			return ResultHelper.getResult(ConstantValue.PUT_MESSAGE_FAILED);
		}
		log.info("TLQ������Ϣ�ɹ�:SendIdentify="+sendIdentify);
		return ResultHelper.getResult(ConstantValue.PUT_MESSAGE_SUCCESS);
	}

	@Override
	public AbstractMessage get() throws ReceiveException,MessageFormatException{
		javax.jms.Message receiveMessage = null;
		try {
			receiveMessage = queueReceiver.receive(ConstantValue.WAIT_TIME);
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_RECEIVER_MESSAGE_ERROR)+e.getMessage());
			throw new ReceiveException();
		}
		if(receiveMessage==null){
			log.warn("TLQ�ȴ�������Ϣ��ʱ");
			return null;
		}
		AbstractMessage message = null;
		if (receiveMessage instanceof FileMessage) {
			String fileName = ((FileMessage) receiveMessage).getFile();
			log.info("TLQ���յ���Ϣ�ļ�"+ConstantValue.PATH+fileName);
			message = MessageHelper.getMessage(fileName);
		}else{
			try {
				receiveMessage.acknowledge();
			} catch (JMSException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_ACKNOWLEDGE_ERROR)+e.getMessage());
			}
			throw new MessageFormatException();
		}
		try {
			receiveMessage.acknowledge();
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_ACKNOWLEDGE_ERROR)+e.getMessage());
		}	
		if (message == null) {
			throw new MessageFormatException();
		}
		log.info("TLQ������Ϣ�ɹ�:SendIdentify="+message.getEnvelope().getSendIdentify());
		return message;
	}
}
