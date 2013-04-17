package com.greatsoft.transq.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.QueueAddressParser;
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
 * @param args
 *            [1]:��Ϣ�����ļ��ĸ�Ŀ¼
 * @param args
 *            [2]:��ʱ���HiepMessage��Ŀ¼
 * @param args
 *            [3]:��׼��TLQAddress�ַ��� ���磺tlq://111@localhost:10024/qcu1/localQueue
 */
public class MessageDataAdapter {
	public MessageDataAdapter() {
	}

	public static void main(String[] args) {
		boolean flag = true;
		if (args.length != 3) {
			System.out.println("����������Ŀ����ȷ��Ӧ������3��������");
			return;
		}
		/** ��Ϣ�����ļ��ĸ�Ŀ¼ */
		String messageConfigFilePath = args[0];
		/** ��ʱ���HiepMessage��Ŀ¼ */
		String hiepMessageDir = args[1];

		String TLQAddressString = args[2];
		System.out.println("messageConfigFilePath=" + messageConfigFilePath
				+ ", \nhiepMessageDir=" + hiepMessageDir
				+ ", \nTLQAddressString=" + TLQAddressString);
		// ����TLQ����
		TLQAddress address = TLQAddress.parserOneAddress(TLQAddressString);
		if (null == address) {
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
		// ��ȡ������Ϣ��Ŀ�ĵ�ַ�ͷ�����Ϣ����
		String targetAddress = null;
		int number = 0;
		Address[] targetAddressArray = null;
		while (flag) {
			System.out.println("���뷢����Ϣ�Ĵ���number����Ϣ��Ŀ�ĵ�ַtargetAddress");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			try {
				number = Integer.parseInt(bufferedReader.readLine());
			} catch (IOException e) {
				System.out.println("��ȡ������Ϣ�Ĵ���ʧ��");
				continue;
			}
			try {
				targetAddress = bufferedReader.readLine();
			} catch (IOException e) {
				System.out.println("��ȡ�����쳣");
			}

			System.out.println("number = " + number + ", targetAddress = "
					+ targetAddress);
			targetAddressArray = QueueAddressParser.getInstance().parse(
					targetAddress);
			if (null == targetAddressArray) {
				System.out.println("��Ϣ��Ŀ�ĵ�ַ����ʧ��");
				continue;
			}
			int targetAddressNumber = targetAddressArray.length;
			File[] subAddressFiles = null;
			File messageConfigFileDir = null;
			String messageConfigFilePathNew = null;
			for (; number > 0; number--) {
				// ����һ����Ϣ
				for (int index = 0; (index < targetAddressNumber); index++) {
					// address=*@����
					if (targetAddressArray[index].getSubAddress().equals("*")) {
						messageConfigFilePathNew = messageConfigFilePath
								+ targetAddressArray[index].getName() + "/";
						// �ҵ�����Ŀ¼�µ����в���Ŀ¼
						messageConfigFileDir = new File(
								messageConfigFilePathNew);
						subAddressFiles = messageConfigFileDir.listFiles();
						// ��ÿһ������Ŀ¼�µ������ļ�����
						for (int fileIndex = 0; fileIndex < subAddressFiles.length; fileIndex++) {
							sendOneSubAddressMessage(
									subAddressFiles[fileIndex].getAbsolutePath()
											+ "/", (TLQueue) sender,
									hiepMessageDir);
						}
					} else {
						// ��ַ��ʽΪ��ũ��@����
						messageConfigFilePathNew = messageConfigFilePath
								+ targetAddressArray[index].getName() + "/"
								+ targetAddressArray[index].getSubAddress()
								+ "/";
						sendOneSubAddressMessage(messageConfigFilePathNew,
								(TLQueue) sender, hiepMessageDir);
					}
				}
			}
		}
		tlqConnector.disConnect();
	}

	public static void sendOneSubAddressMessage(String messageConfigFilePath,
			TLQueue sender, String hiepMessageDir) {
		String messageEnvelopeConfigFilePath = messageConfigFilePath
				+ "envelope/";
		String messageDataConfigFilePath = messageConfigFilePath + "data/";
		File messageEnvelopeConfigFilesDir = new File(
				messageEnvelopeConfigFilePath);
		File[] messageEnvelopeConfigFiles = messageEnvelopeConfigFilesDir
				.listFiles();
		AbstractMessage message = null;
		String sendID = null;
		String hiepMessageFilePath = null;
		ResultImp putTLQresult = null;
		/** ��ÿһ�ŷ�ͷ�����ļ����� */
		for (int envelopeConfigFileNumber = 0; envelopeConfigFileNumber < messageEnvelopeConfigFiles.length; envelopeConfigFileNumber++) {
			message = getHiepMessage(
					messageEnvelopeConfigFiles[envelopeConfigFileNumber],
					messageEnvelopeConfigFilePath, messageDataConfigFilePath);
			if (null == message) {
				System.out.println("HiepMessage���������г���"
						+ messageEnvelopeConfigFiles[envelopeConfigFileNumber]);
				continue;
			}
			sendID = message.getEnvelope().getSendIdentify();

			/** �����ɵ�HiepMessage��Ϣ��ŵ����õ�Ŀ¼���棬��TLQȡ�� */
			hiepMessageFilePath = hiepMessageDir + sendID;
			if (!MessageHelper.toFile(hiepMessageFilePath, message)) {
				System.out.println("HiepMessage:" + sendID + "��صĹ����г���");
				continue;
			}
			System.out.println("׼��������Ϣ��sendIdentify=" + sendID);
			try {
				putTLQresult = sender.put(message, hiepMessageFilePath);
			} catch (CommunicationException e) {
				System.out.println("������Ϣʧ��sendIdentify=" + sendID
						+ e.getMessage());
			}
			if (putTLQresult.getReturnCode() != ErrorCode.NO_ERROR) {
				System.out.println("������Ϣʧ��sendIdentify=" + sendID);
				continue;
			}
			System.out.println("������Ϣ�ɹ�sendIdentify=" + sendID);
			/** ɾ���м��ļ���HiepMessage����ļ� */
			File file = new File(hiepMessageFilePath);
			if (file.delete()) {
				System.out.println("�ļ�" + file.getPath() + "ɾ���ɹ�");
			} else {
				System.out.println("�ļ�" + file.getPath() + "ɾ��ʧ��");
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
