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
 * 将一个信封配置文件和一份测试数据放在toolDir目录下面，生成一个heipMessage，放入到TLQ中
 * 
 * @author mojia
 * 
 */
public class PutTLQ {

	/**
	 * @param args
	 *            [0]:工具目录
	 * @param args
	 *            [1]:信封头配置文件名字
	 * @param args
	 *            [2]:标准的TLQAddress字符串 例如：tlq://111@localhost:10024/qcu1/localQueue
	 * @author mojia
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("传进来的参数个数不对");
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
			System.out.println("传进来的参数TLQ地址设置有误");
			System.exit(-1);
		}
		TLQConnector tlqConnector = new TLQConnector(address, null);
		try {
			tlqConnector.connect();
		} catch (ConnectException e1) {
			System.out.println("建立连接失败"+e1.getMessage());
			System.exit(-1);
		}
		if (!tlqConnector.isConnected()) {
			System.out.println("没有建立起连接");
			System.exit(-1);
		}
		Sender sender =tlqConnector.createSender();

		AbstractMessage message = null;
		ResultImp putTLQresult = null;
		String sendID = null;

		message = getHiepMessage(toolDir, envelopeConfigFileName);
		sendID = message.getEnvelope().getSendIdentify();

		/** 将生成的HiepMessage消息存放到配置的目录下面，让TLQ取走 */
		String hiepMessageFilePath = toolDir + sendID;
		if (!MessageHelper.toFile(hiepMessageFilePath, message)) {
			System.out.println("HiepMessage:" + sendID + "落地的过程中出错");
			System.exit(-1);
		}
		System.out.println("准备发送消息：" + sendID);
		try {
			putTLQresult = ((TLQueue)sender).put(message,toolDir);
		} catch (CommunicationException e) {
			e.printStackTrace();
			System.out.println("发送消息失败：" + sendID);
			System.exit(-1);
		}
		if (putTLQresult!=null && putTLQresult.getReturnCode() != ConstantValue.PROCESS_SUCCESS) {
			System.out.println("发送消息失败:" + sendID);
			System.exit(-1);
		}
		System.out.println("发送消息成功");
		tlqConnector.disConnect();
		/** 删除中间文件：HiepMessage落地文件 */
		File file = new File(hiepMessageFilePath);
		if (file.delete()) {
			System.out.println("文件" + file.getPath() + "删除成功");
		} else {
			System.out.println("文件" + file.getPath() + "删除失败");
		}
	}

	/** 从一个信封配置文件得到一个HiepMessage */
	public static AbstractMessage getHiepMessage(String toolDir,
			String envelopeConfigFileName) {
		AbstractMessage message = null;
		
		Envelope envelope = null;
		ByteBuffer byteBuffer = null;

		envelope = parseEnvelopeFromConfigFile(toolDir, envelopeConfigFileName);
		if (envelope == null) {
			System.out.println("信封头配置文件" + envelopeConfigFileName
					+ "组装成信封过程中出错");
			return null;
		}
		byteBuffer = ByteBufferHelper.getByteBuffer(toolDir
				+ envelope.getSourceDataName());
		if (byteBuffer == null) {
			System.out.println("获取文件：" + envelope.getSourceDataName()
					+ "中的信息数据过程中出错。");
			return null;
		}
		message = new MessageImp(envelope, byteBuffer);
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
		fromAddress=envelopeConfigMap.get(FROM_ADDRESS);
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
				dataCompressType, relativeExpiredTime, sourceAddress,fromAddress,
				targetAddress, sourceDataName);
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
