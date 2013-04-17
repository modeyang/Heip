package com.greatsoft.transq.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.Envelope;
import com.greatsoft.transq.core.message.EnvelopeImp;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.message.MessageImp;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.CommunicationException;
import com.greatsoft.transq.message.queue.TLQAddress;
import com.greatsoft.transq.message.queue.TLQConnector;
import com.greatsoft.transq.message.queue.TLQueue;
import com.greatsoft.transq.utils.ByteBufferHelper;
import com.greatsoft.transq.utils.ConstantValue;

/**
 * ��һ���ŷ������ļ���һ�ݲ������ݷ���toolDirĿ¼���棬����һ��heipMessage�����뵽TLQ��
 * 
 * @author mojia
 * 
 */
public class PutTLQ {

	/**
	 * @param args
	 *            [0]:����Ŀ¼
	 * @param args
	 *            [1]:�ŷ�ͷ�����ļ�����
	 * @param args
	 *            [2]:��׼��TLQAddress�ַ��� ���磺tlq://111@localhost:10024/qcu1/localQueue
	 * @author mojia
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("�������Ĳ�����������");
			System.exit(-1);
		}
		final String toolDir = args[0];
		final String envelopeConfigFileName = args[1];
		final String tlqAddress = args[2];

		System.out.println("toolDir: " + args[0]);
		System.out.println("envelopeConfigFileName: " + args[1]);
		System.out.println("tlqAddress: " + args[2]);

		TLQAddress address = TLQAddress.parserOneAddress(tlqAddress);
		if (address == null) {
			System.out.println("�������Ĳ���TLQ��ַ��������");
			System.exit(-1);
		}
		TLQConnector tlqConnector = new TLQConnector(address, null);
		try {
			tlqConnector.connect();
		} catch (ConnectException e1) {
			System.out.println("��������ʧ��"+e1.getMessage());
			System.exit(-1);
		}
		if (!tlqConnector.isConnected()) {
			System.out.println("û�н���������");
			System.exit(-1);
		}
		Sender sender =tlqConnector.createSender();

		AbstractMessage message = null;
		ResultImp putTLQresult = null;
		String sendID = null;

		message = getHiepMessage(toolDir, envelopeConfigFileName);
		sendID = message.getEnvelope().getSendIdentify();

		/** �����ɵ�HiepMessage��Ϣ��ŵ����õ�Ŀ¼���棬��TLQȡ�� */
		String hiepMessageFilePath = toolDir + sendID;
		if (!MessageHelper.toFile(hiepMessageFilePath, message)) {
			System.out.println("HiepMessage:" + sendID + "��صĹ����г���");
			System.exit(-1);
		}
		System.out.println("׼��������Ϣ��" + sendID);
		try {
			putTLQresult = ((TLQueue)sender).put(message,toolDir);
		} catch (CommunicationException e) {
			e.printStackTrace();
			System.out.println("������Ϣʧ�ܣ�" + sendID);
			System.exit(-1);
		}
		if (putTLQresult!=null && putTLQresult.getReturnCode() != ConstantValue.PROCESS_SUCCESS) {
			System.out.println("������Ϣʧ��:" + sendID);
			System.exit(-1);
		}
		System.out.println("������Ϣ�ɹ�");
		tlqConnector.disConnect();
		/** ɾ���м��ļ���HiepMessage����ļ� */
		File file = new File(hiepMessageFilePath);
		if (file.delete()) {
			System.out.println("�ļ�" + file.getPath() + "ɾ���ɹ�");
		} else {
			System.out.println("�ļ�" + file.getPath() + "ɾ��ʧ��");
		}
	}

	/** ��һ���ŷ������ļ��õ�һ��HiepMessage */
	public static AbstractMessage getHiepMessage(String toolDir,
			String envelopeConfigFileName) {
		AbstractMessage message = null;
		
		Envelope envelope = null;
		ByteBuffer byteBuffer = null;

		envelope = parseEnvelopeFromConfigFile(toolDir, envelopeConfigFileName);
		if (envelope == null) {
			System.out.println("�ŷ�ͷ�����ļ�" + envelopeConfigFileName
					+ "��װ���ŷ�����г���");
			return null;
		}
		byteBuffer = ByteBufferHelper.getByteBuffer(toolDir
				+ envelope.getSourceDataName());
		if (byteBuffer == null) {
			System.out.println("��ȡ�ļ���" + envelope.getSourceDataName()
					+ "�е���Ϣ���ݹ����г���");
			return null;
		}
		message = new MessageImp(envelope, byteBuffer);
		System.out.println("����һ��message");

		return message;
	}

	/** �����ŷ������ļ����õ�һ���ŷ���� */
	public static Envelope parseEnvelopeFromConfigFile(
			String envelopeConfigFilePath, String envelopeConfigFileName) {

		final String DATA_IDENTIFY = "DATA_IDENTIFY";
		final String DATA_TYPE = "DATA_TYPE";
		final String PRIORITY = "PRIORITY";
		final String DATA_COMPRESS_TYPE = "DATA_COMPRESS_TYPE";
		final String RELATIVE_EXPIRED_TIME = "RELATIVE_EXPIRED_TIME";
		final String SOURCE_ADDRESS = "SOURCE_ADDRESS";
		final String FROM_ADDRESS = "FROM_ADDRESS";
		final String TARGET_ADDRESS = "TARGET_ADDRESS";
		final String SOURCE_DATA_NAME = "SOURCE_DATA_NAME";

		String dataIdentify = null;
		String dataType = null;
		int priority = 0;
		int dataCompressType = 0;
		int relativeExpiredTime = 0;
		String sourceAddress = null;
		String fromAddress = null;
		String targetAddress = null;
		String sourceDataName = null;

		Map<String, String> envelopeConfigMap = new HashMap<String, String>();
		String envelopeConfigFileAllPath = envelopeConfigFilePath
				+ envelopeConfigFileName;
		if (!loadConfigFile(envelopeConfigFileAllPath, envelopeConfigMap)) {
			return null;
		}

		dataIdentify = envelopeConfigMap.get(DATA_IDENTIFY);
		if (isStringNull(dataIdentify)) {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��DATA_IDENTIFY��������");
			return null;
		}

		dataType = envelopeConfigMap.get(DATA_TYPE);
		if (isStringNull(dataType)) {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��DATA_TYPE��������");
			return null;
		}

		if (isNumber(envelopeConfigMap.get(PRIORITY))) {
			priority = Integer.parseInt(envelopeConfigMap.get(PRIORITY));
			if (priority < 0 || priority > 9) {
				System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
						+ "��PRIORITY��������Ӧ����0-9֮�������");
				return null;
			}
		} else {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��PRIORITY��������Ӧ����0-9֮�������");
			return null;
		}

		if (isNumber(envelopeConfigMap.get(DATA_COMPRESS_TYPE))) {
			dataCompressType = Integer.parseInt(envelopeConfigMap
					.get(DATA_COMPRESS_TYPE));
			if (dataCompressType < 0 || dataCompressType > 1) {
				System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
						+ "��DATA_COMPRESS_TYPE��������Ӧ����0����1");
				return null;
			}
		} else {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��DATA_COMPRESS_TYPE��������Ӧ����0����1");
			return null;
		}

		if (isNumber(envelopeConfigMap.get(RELATIVE_EXPIRED_TIME))) {
			relativeExpiredTime = Integer.parseInt(envelopeConfigMap
					.get(RELATIVE_EXPIRED_TIME));
			if (relativeExpiredTime < 0) {
				System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
						+ "��RELATIVE_EXPIRED_TIME��������Ӧ���Ǵ���0������");
				return null;
			}
		} else {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��RELATIVE_EXPIRED_TIME��������Ӧ���Ǵ���0������");
			return null;
		}

		sourceAddress = envelopeConfigMap.get(SOURCE_ADDRESS);
		if (isStringNull(sourceAddress)) {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��SOURCE_ADDRESS��������");
			return null;
		}
		fromAddress=envelopeConfigMap.get(FROM_ADDRESS);
		if (isStringNull(fromAddress)) {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��FROM_ADDRESS��������");
			return null;
		}
		targetAddress = envelopeConfigMap.get(TARGET_ADDRESS);
		if (isStringNull(targetAddress)) {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��TARGET_ADDRESS��������");
			return null;
		}

		sourceDataName = envelopeConfigMap.get(SOURCE_DATA_NAME);
		if (isStringNull(sourceDataName)) {
			System.out.println("�ŷ�ͷ�����ļ���" + envelopeConfigFileName
					+ "��SOURCE_DATA_NAME��������");
			return null;
		}

		return new EnvelopeImp(dataIdentify, dataType, priority,
				dataCompressType, relativeExpiredTime, sourceAddress,fromAddress,
				targetAddress, sourceDataName);
	}

	/** �����ŷ������ļ� */
	public static boolean loadConfigFile(String configFilePath,
			Map<String, String> configMap) {
		String line = "";
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(configFilePath));
		} catch (FileNotFoundException e) {
			System.out.println("û���ҵ������ļ���" + configFilePath + e.getMessage());
			return false;
		}
		String[] strArray = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (isComment(line) || line == null || line.equals("")) {
					continue;
				}
				strArray = line.split("=");
				if (strArray.length == 2) {
					configMap.put(strArray[0].trim(), strArray[1].trim());
				} else {
					System.out.println("��ȡ�����ļ���" + configFilePath
							+ "ʱ���ִ���\n�������ò���׼[" + line + "]");
					return false;
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			System.out.println("��ȡ�����ļ���" + configFilePath + "ʱ���ִ���\n"
					+ e.getMessage());
			return false;
		}
		return true;
	}

	public static boolean isComment(String line) {
		Pattern pattern = Pattern.compile("^#.*");
		Matcher matcher = pattern.matcher(line);
		return matcher.matches();

	}

	/**
	 * �ж��ַ����Ƿ��������ַ���
	 * */
	private static boolean isNumber(String string) {
		if (string == null || string.equals("")) {
			return false;
		}

		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(string).matches();
	}

	/**
	 * �ж��ַ����Ƿ�Ϊ��
	 * */
	private static boolean isStringNull(String str) {
		if (str == null || str.equals("")) {
			return true;
		} else {
			return false;
		}
	}
}
