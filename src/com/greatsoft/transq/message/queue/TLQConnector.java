package com.greatsoft.transq.message.queue;

import java.net.ConnectException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import com.greatsoft.transq.core.Connector;
import com.greatsoft.transq.core.Receiver;
import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;
import com.tongtech.tmqi.jmsclient.QueueReceiverImpl;
import com.tongtech.tmqi.jmsclient.QueueSenderImpl;
import com.tongtech.tmqi.jmsclient.QueueSessionImpl;

/**
 * Connector 的TonglinkQueue实现，使用远程工厂连接
 * @author ZL
 */
public class TLQConnector implements Connector {
	private static Logger log = Log.getLog(TLQConnector.class);

	public static final String tcf = "tongtech.jms.jndi.JmsContextFactory";
	public static final String remoteFactory = "RemoteConnectionFactory";
	public static final String factoryInitial = "java.naming.factory.initial";
	public static final String providerUrl ="java.naming.provider.url";
	/** 预留参数，用作后续扩展 */
	private Object options;
	/** TLQ地址 */
	private TLQAddress address;
	private String remoteUrl;
	private Queue remoteQueue = null;
	private QueueSessionImpl queueSession = null;
	private QueueConnection queueConnection = null;

	public TLQConnector(TLQAddress address, Object options) {
		this.options = options;
		this.address = address;
		this.remoteUrl = ConstantValue.TLQ+ConstantValue.MORE_PATH_STRING+address.getIp()+ConstantValue.COLON+ address.getPort();
	}

	public TLQConnector(String ip, int listenPort, Object options) {
		this.options = options;
		this.remoteUrl = ConstantValue.TLQ +ConstantValue.MORE_PATH_STRING + ip + ConstantValue.COLON + listenPort;
	}

	public void connect() throws ConnectException {
		Properties properties = new Properties();
		properties.put(factoryInitial, tcf);
		properties.put(providerUrl, remoteUrl);
		/** 初始化上下文 */
		Context context = null;		
		try {
			context = new InitialContext(properties);
		} catch (NamingException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_INITIAL_CONTEXT_ERROR) + e.getMessage());
			throw new ConnectException();
		}
		/**寻找连接工厂和队列*/
		QueueConnectionFactory queueConnectionFactory = null;
		try {
			queueConnectionFactory = (QueueConnectionFactory) context.lookup(remoteFactory);
			remoteQueue = (Queue) context.lookup(address.getQueueName());
		} catch (NamingException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_LOOKUP_ERROR)+e.getMessage());
			throw new ConnectException();
		}
		/**建立连接*/
		try {
			queueConnection = queueConnectionFactory.createQueueConnection();
			queueConnection.start();
			queueSession = (QueueSessionImpl) queueConnection.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_CREATE_CONNECTION_ERROR)+e.getMessage());
			throw new ConnectException();
		}
	}

	public Boolean isConnected() {
		if (queueConnection == null || queueSession == null) {
			return false;
		}
		return true;
	}
	
	public Sender createSender() {
		QueueSenderImpl queueSender=null;
		try {
			queueSender=(QueueSenderImpl) queueSession.createProducer(remoteQueue);
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_CREATE_SENDER_ERROR) + e.getMessage());
			return null;
		}
		return new TLQueue(queueSender, null);
	}


	public Receiver createReceiver() {
		QueueReceiverImpl queueReceiver=null;
		try {
			queueReceiver=(QueueReceiverImpl) queueSession.createConsumer(remoteQueue);
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_CREATE_RECEIVER_ERROR) + e.getMessage());
			return null;
		}
		return new TLQueue(queueReceiver, null);
	}

	public void disConnect() {
		try {
			if (queueSession != null) {
				queueSession.close();
			}
			if (queueConnection != null) {
				queueConnection.close();
			}
		} catch (JMSException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.TLQ_CLOSE_CONNECTION_ERROR) + e.getMessage());
		}
		log.info("TLQ连接关闭成功");
	}
}
