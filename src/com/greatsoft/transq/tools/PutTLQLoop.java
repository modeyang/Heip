package com.greatsoft.transq.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ConnectException;
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
import com.greatsoft.transq.utils.ErrorCode;

/**
 * ���������ļ���Դ��������������HiepMessage֮�������ķ��͵�ָ����TLQAddress��TLQ�С�
 * 
 * @author mojia
 * @param args
 *            [1]:�������ݴ��Ŀ¼
 * @param args
 *            [2]:�ŷ�ͷ�����ļ����Ŀ¼
 * @param args
 *            [3]:��ʱ���HiepMessage��Ŀ¼
 * @param args
 *            [4]:��׼��TLQAddress�ַ��� ���磺tlq://111@localhost:10024/qcu1/localQueue
 */
public class PutTLQLoop {
	private static final int THREAD_SLEEP_TIME = 3000;

	public static void main(String[] args) {
		if (args.length != 5) {
			System.out.println("�������Ĳ�����������");
			return;
		}
		final String testDataDir = args[0];
		final String envelopeConfigFileDir = args[1];
		final String hiepMessageDir = args[2];
		final String tlqAddress = args[3];
		final String countStr = args[4];

		int count = Integer.parseInt(countStr);

		System.out.println("testDataDir: " + testDataDir);
		System.out.println("envelopeConfigFileDir: " + envelopeConfigFileDir);
		System.out.println("hiepMessageDir: " + hiepMessageDir);
		System.out.println("tlqAddress: " + tlqAddress);

		TLQAddress address = TLQAddress.parserOneAddress(tlqAddress);
		if (address == null) {
			System.out.println("��������TLQ��ַ��������");
			return;
		}
		TLQConnector tlqConnector = new TLQConnector(address, null);
		try {
			tlqConnector.connect();
		} catch (ConnectException e1) {
			System.out.println("TLQ��������ʧ��");
			return;
		}
		if (!tlqConnector.isConnected()) {
			System.out.println("TLQ��������ʧ��");
			return;
		}
		Sender sender = tlqConnector.createSender();
		AbstractMessage message = null;
		File dir = new File(envelopeConfigFileDir);
		File[] envelopeFiles = null;
		String hiepMessageFilePath = null;
		ResultImp putTLQresult = null;
		String sendID = null;
		while (--count != 0) {
			envelopeFiles = dir.listFiles();
			for (int i = 0; i < envelopeFiles.length; i++) {
				message = getHiepMessage(envelopeFiles[i],
						envelopeConfigFileDir, testDataDir);
				sendID = message.getEnvelope().getSendIdentify();
				/** �����ɵ�HiepMessage��Ϣ��ŵ����õ�Ŀ¼���棬��TLQȡ�� */
				hiepMessageFilePath = hiepMessageDir + sendID;
				if (!MessageHelper.toFile(hiepMessageFilePath, testDataDir
						+ message.getEnvelope().getSourceDataName(), message)) {
					System.out
							.println("��Ϣ��SendIdentify=" + sendID + "��صĹ����г���");
					continue;
				}
				System.out.println("��ʼ������Ϣ��SendIdentify=" + sendID);
				try {
					putTLQresult = ((TLQueue) sender).put(message,
							hiepMessageDir);
				} catch (CommunicationException e) {
					System.out.println("������Ϣʧ�ܣ�SendIdentify=" + sendID
							+ e.getMessage());
					continue;
				}
				if (putTLQresult.getReturnCode() != ErrorCode.NO_ERROR) {
					System.out.println("������Ϣʧ��:SendIdentify=" + sendID);
					continue;
				}
				System.out.println("������Ϣ�ɹ�");
				message = null;
				/** ɾ���м��ļ���HiepMessage����ļ� */
				File file = new File(hiepMessageFilePath);
				if (file.delete()) {
					System.out.println("�ļ�" + file.getPath() + "ɾ���ɹ�");
				} else {
					System.out.println("�ļ�" + file.getPath() + "ɾ��ʧ��");
				}
			}
			try {
				Thread.sleep(THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/** ��һ���ŷ������ļ��õ�һ��HiepMessage */
	public static AbstractMessage getHiepMessage(File file,
			String envelopeConfigFileDir, String testDataDir) {
		AbstractMessage message = null;
		Envelope envelope = null;
		String envelopeConfigFileName = file.getName();
		envelope = parseEnvelopeFromConfigFile(envelopeConfigFileDir,
				envelopeConfigFileName);
		if (envelope == null) {
			System.out.println("�ŷ�ͷ�����ļ�" + file.getName() + "���������г���");
			return null;
		}
		String dataFileName = testDataDir + envelope.getSourceDataName();
		File dataFile = new File(dataFileName);
		envelope.setOriginalFileLength(dataFile.length());
		message = new MessageImp(envelope, dataFileName);
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
		fromAddress = envelopeConfigMap.get(FROM_ADDRESS);
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
				dataCompressType, relativeExpiredTime, sourceAddress,
				fromAddress, targetAddress, sourceDataName);
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
