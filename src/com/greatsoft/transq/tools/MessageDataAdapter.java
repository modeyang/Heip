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
 * 根据配置文件和源数据生成批量的HiepMessage之后，批量的放送到指定的TLQAddress的TLQ中。
 * 
 * @param args
 *            [1]:信息配置文件的根目录
 * @param args
 *            [2]:临时存放HiepMessage的目录
 * @param args
 *            [3]:标准的TLQAddress字符串 例如：tlq://111@localhost:10024/qcu1/localQueue
 */
public class MessageDataAdapter {
	public MessageDataAdapter() {
	}

	public static void main(String[] args) {
		boolean flag = true;
		if (args.length != 3) {
			System.out.println("参数配置数目不正确，应该配置3个参数！");
			return;
		}
		/** 信息配置文件的根目录 */
		String messageConfigFilePath = args[0];
		/** 临时存放HiepMessage的目录 */
		String hiepMessageDir = args[1];

		String TLQAddressString = args[2];
		System.out.println("messageConfigFilePath=" + messageConfigFilePath
				+ ", \nhiepMessageDir=" + hiepMessageDir
				+ ", \nTLQAddressString=" + TLQAddressString);
		// 建立TLQ连接
		TLQAddress address = TLQAddress.parserOneAddress(TLQAddressString);
		if (null == address) {
			System.out.println("传进来的TLQ地址参数有误");
			return;
		}
		TLQConnector tlqConnector = new TLQConnector(address, null);
		try {
			tlqConnector.connect();
		} catch (ConnectException e1) {
			System.out.println("TLQ建立连接失败");
			return;
		}
		if (!tlqConnector.isConnected()) {
			System.out.println("TLQ建立连接失败");
			return;
		}
		Sender sender = tlqConnector.createSender();
		// 获取发送消息的目的地址和发送消息次数
		String targetAddress = null;
		int number = 0;
		Address[] targetAddressArray = null;
		while (flag) {
			System.out.println("输入发送信息的次数number和信息的目的地址targetAddress");
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(System.in));
			try {
				number = Integer.parseInt(bufferedReader.readLine());
			} catch (IOException e) {
				System.out.println("获取发送信息的次数失败");
				continue;
			}
			try {
				targetAddress = bufferedReader.readLine();
			} catch (IOException e) {
				System.out.println("读取数据异常");
			}

			System.out.println("number = " + number + ", targetAddress = "
					+ targetAddress);
			targetAddressArray = QueueAddressParser.getInstance().parse(
					targetAddress);
			if (null == targetAddressArray) {
				System.out.println("信息的目的地址解析失败");
				continue;
			}
			int targetAddressNumber = targetAddressArray.length;
			File[] subAddressFiles = null;
			File messageConfigFileDir = null;
			String messageConfigFilePathNew = null;
			for (; number > 0; number--) {
				// 发送一次信息
				for (int index = 0; (index < targetAddressNumber); index++) {
					// address=*@北京
					if (targetAddressArray[index].getSubAddress().equals("*")) {
						messageConfigFilePathNew = messageConfigFilePath
								+ targetAddressArray[index].getName() + "/";
						// 找到北京目录下的所有部门目录
						messageConfigFileDir = new File(
								messageConfigFilePathNew);
						subAddressFiles = messageConfigFileDir.listFiles();
						// 对每一个部门目录下的配置文件操作
						for (int fileIndex = 0; fileIndex < subAddressFiles.length; fileIndex++) {
							sendOneSubAddressMessage(
									subAddressFiles[fileIndex].getAbsolutePath()
											+ "/", (TLQueue) sender,
									hiepMessageDir);
						}
					} else {
						// 地址形式为：农合@北京
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
		/** 对每一信封头配置文件操作 */
		for (int envelopeConfigFileNumber = 0; envelopeConfigFileNumber < messageEnvelopeConfigFiles.length; envelopeConfigFileNumber++) {
			message = getHiepMessage(
					messageEnvelopeConfigFiles[envelopeConfigFileNumber],
					messageEnvelopeConfigFilePath, messageDataConfigFilePath);
			if (null == message) {
				System.out.println("HiepMessage解析过程中出错"
						+ messageEnvelopeConfigFiles[envelopeConfigFileNumber]);
				continue;
			}
			sendID = message.getEnvelope().getSendIdentify();

			/** 将生成的HiepMessage消息存放到配置的目录下面，让TLQ取走 */
			hiepMessageFilePath = hiepMessageDir + sendID;
			if (!MessageHelper.toFile(hiepMessageFilePath, message)) {
				System.out.println("HiepMessage:" + sendID + "落地的过程中出错");
				continue;
			}
			System.out.println("准备发送消息：sendIdentify=" + sendID);
			try {
				putTLQresult = sender.put(message, hiepMessageFilePath);
			} catch (CommunicationException e) {
				System.out.println("发送消息失败sendIdentify=" + sendID
						+ e.getMessage());
			}
			if (putTLQresult.getReturnCode() != ErrorCode.NO_ERROR) {
				System.out.println("发送消息失败sendIdentify=" + sendID);
				continue;
			}
			System.out.println("发送消息成功sendIdentify=" + sendID);
			/** 删除中间文件：HiepMessage落地文件 */
			File file = new File(hiepMessageFilePath);
			if (file.delete()) {
				System.out.println("文件" + file.getPath() + "删除成功");
			} else {
				System.out.println("文件" + file.getPath() + "删除失败");
			}
		}
	}

	/** 从一个信封配置文件得到一个HiepMessage */
	public static AbstractMessage getHiepMessage(File file,
			String envelopeConfigFileDir, String testDataDir) {
		AbstractMessage message = null;
		Envelope envelope = null;
		String envelopeConfigFileName = file.getName();
		envelope = parseEnvelopeFromConfigFile(envelopeConfigFileDir,
				envelopeConfigFileName);
		if (envelope == null) {
			System.out.println("信封头配置文件" + file.getName() + "解析过程中出错");
			return null;
		}
		String dataFileName = testDataDir + envelope.getSourceDataName();
		File dataFile = new File(dataFileName);
		envelope.setOriginalFileLength(dataFile.length());
		message = new MessageImp(envelope, dataFileName);
		System.out.println("生成一条message");
		return message;
	}

	/** 解析信封配置文件，得到一个信封对象 */
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
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的DATA_IDENTIFY配置有误");
			return null;
		}

		dataType = envelopeConfigMap.get(DATA_TYPE);
		if (isStringNull(dataType)) {
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的DATA_TYPE配置有误");
			return null;
		}

		if (isNumber(envelopeConfigMap.get(PRIORITY))) {
			priority = Integer.parseInt(envelopeConfigMap.get(PRIORITY));
			if (priority < 0 || priority > 9) {
				System.out.println("信封头配置文件：" + envelopeConfigFileName
						+ "的PRIORITY配置有误，应当是0-9之间的数字");
				return null;
			}
		} else {
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的PRIORITY配置有误，应当是0-9之间的数字");
			return null;
		}

		if (isNumber(envelopeConfigMap.get(DATA_COMPRESS_TYPE))) {
			dataCompressType = Integer.parseInt(envelopeConfigMap
					.get(DATA_COMPRESS_TYPE));
			if (dataCompressType < 0 || dataCompressType > 1) {
				System.out.println("信封头配置文件：" + envelopeConfigFileName
						+ "的DATA_COMPRESS_TYPE配置有误，应当是0或者1");
				return null;
			}
		} else {
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的DATA_COMPRESS_TYPE配置有误，应当是0或者1");
			return null;
		}

		if (isNumber(envelopeConfigMap.get(RELATIVE_EXPIRED_TIME))) {
			relativeExpiredTime = Integer.parseInt(envelopeConfigMap
					.get(RELATIVE_EXPIRED_TIME));
			if (relativeExpiredTime < 0) {
				System.out.println("信封头配置文件：" + envelopeConfigFileName
						+ "的RELATIVE_EXPIRED_TIME配置有误，应当是大于0的数字");
				return null;
			}
		} else {
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的RELATIVE_EXPIRED_TIME配置有误，应当是大于0的数字");
			return null;
		}

		sourceAddress = envelopeConfigMap.get(SOURCE_ADDRESS);
		if (isStringNull(sourceAddress)) {
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的SOURCE_ADDRESS配置有误");
			return null;
		}
		fromAddress = envelopeConfigMap.get(FROM_ADDRESS);
		if (isStringNull(fromAddress)) {
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的FROM_ADDRESS配置有误");
			return null;
		}
		targetAddress = envelopeConfigMap.get(TARGET_ADDRESS);
		if (isStringNull(targetAddress)) {
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的TARGET_ADDRESS配置有误");
			return null;
		}

		sourceDataName = envelopeConfigMap.get(SOURCE_DATA_NAME);
		if (isStringNull(sourceDataName)) {
			System.out.println("信封头配置文件：" + envelopeConfigFileName
					+ "的SOURCE_DATA_NAME配置有误");
			return null;
		}

		return new EnvelopeImp(dataIdentify, dataType, priority,
				dataCompressType, relativeExpiredTime, sourceAddress,
				fromAddress, targetAddress, sourceDataName);
	}

	/** 加载信封配置文件 */
	public static boolean loadConfigFile(String configFilePath,
			Map<String, String> configMap) {
		String line = "";
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(configFilePath));
		} catch (FileNotFoundException e) {
			System.out.println("没有找到配置文件：" + configFilePath + e.getMessage());
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
					System.out.println("读取配置文件：" + configFilePath
							+ "时出现错误。\n此行配置不标准[" + line + "]");
					return false;
				}
			}
			bufferedReader.close();
		} catch (IOException e) {
			System.out.println("读取配置文件：" + configFilePath + "时出现错误。\n"
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
	 * 判断字符串是否是数字字符串
	 * */
	private static boolean isNumber(String string) {
		if (string == null || string.equals("")) {
			return false;
		}

		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(string).matches();
	}

	/**
	 * 判断字符串是否为空
	 * */
	private static boolean isStringNull(String str) {
		if (str == null || str.equals("")) {
			return true;
		} else {
			return false;
		}
	}
}
