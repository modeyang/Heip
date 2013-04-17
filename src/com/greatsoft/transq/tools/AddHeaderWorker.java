package com.greatsoft.transq.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.Envelope;
import com.greatsoft.transq.core.message.EnvelopeImp;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.message.MessageImp;

public class AddHeaderWorker implements Runnable {

	private String envelopeConfigFileDir;
	private String testDataDir;
	private String newHiepMessageDir;

	public AddHeaderWorker(String envelopeConfigFileDir, String testDataDir,
			String newHiepMessageDir) {
		this.envelopeConfigFileDir = envelopeConfigFileDir;
		this.testDataDir = testDataDir;
		this.newHiepMessageDir = newHiepMessageDir;
	}

	@Override
	public void run() {
		Envelope envelope = null;
		AbstractMessage message = null;
		File dir = new File(this.envelopeConfigFileDir);
		File[] envelopeFiles = dir.listFiles();
		for (int i = 0; i < envelopeFiles.length; i++) {
			String envelopeConfigFileName = envelopeFiles[i].getName();
			envelope = parseEnvelopeFromConfigFile(this.envelopeConfigFileDir,
					envelopeConfigFileName);
			if (envelope == null) {
				System.out.println("第" + (i + 1) + "个信封头配置文件配置为信封头过程中出错。");
				return;
			}
			String dataFileName=this.testDataDir+ envelope.getSourceDataName();
			File dataFile=new File(dataFileName);
			long dataLength= dataFile.length();
			if(dataLength<=0){
				System.out.println("第" + (i + 1) + "个信封头配置文件配置为信封头过程中出错。");
				return;
			}
			envelope.setOriginalFileLength(dataLength);
			message = new MessageImp(envelope, dataFileName);
			System.out.println("第" + (i + 1) + "个message加头成功。");
			if (!MessageHelper.toFile(newHiepMessageDir + envelope.getSendIdentify(),dataFileName, message)) {
				System.out.println("将第" + (i + 1) + "个message落地的过程中出错");
				File file=new File(newHiepMessageDir + envelope.getSendIdentify());
				file.delete();
				return;
			}
			System.out.println("第" + (i + 1) + "个message落地成功。");
		}
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
