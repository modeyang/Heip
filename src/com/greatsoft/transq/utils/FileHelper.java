package com.greatsoft.transq.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.AddressHelper;
import com.greatsoft.transq.core.RouterRecord;

public class FileHelper {
	private static Logger log = Log.getLog(FileHelper.class);

	/** 将信息文件从新消息队列移动到超时信息队列中 */
	public static boolean moveFromNewToExpired(String sendIdentify) {
		String oldFilePath = Config.NEW_MESSAGE_FILE_DIRECTORY + sendIdentify;
		String newFilePath = Config.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY
				+ sendIdentify;
		return moveFile(oldFilePath, newFilePath);
	}

	/** 将信息文件从新消息队列移动到错误信息队列中 */
	public static boolean moveFromNewToError(String sendIdentify) {
		String oldFilePath = Config.NEW_MESSAGE_FILE_DIRECTORY + sendIdentify;
		String newFilePath = Config.ERROR_MESSAGE_FILE_DIRECTORY + sendIdentify;
		return moveFile(oldFilePath, newFilePath);
	}

	/** 将信息文件从接收信息队列移动到错误信息队列中 */
	public static boolean moveFromReceivedToError(String sendIdentify) {
		String oldFilePath = Config.RECEIVED_MESSAGE_FILE_DIRECTORY
				+ sendIdentify;
		String newFilePath = Config.ERROR_MESSAGE_FILE_DIRECTORY + sendIdentify;
		return moveFile(oldFilePath, newFilePath);
	}

	/** 将信息文件从接收信息队列移动到超时信息队列中 */
	public static boolean moveFromReceivedToExpired(String sendIdentify) {
		String oldFilePath = Config.RECEIVED_MESSAGE_FILE_DIRECTORY
				+ sendIdentify;
		String newFilePath = Config.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY
				+ sendIdentify;
		return moveFile(oldFilePath, newFilePath);
	}

	/** 将信息文件从新消息队列中删除 */
	public static boolean deleteFromNew(String fileName) {
		String filePath = Config.NEW_MESSAGE_FILE_DIRECTORY + fileName;
		return deleteOneFile(filePath);
	}

	/** 将信息文件从接收消息队列中删除 */
	public static boolean deleteFromReceived(String fileName) {
		String filePath = Config.RECEIVED_MESSAGE_FILE_DIRECTORY + fileName;
		return deleteOneFile(filePath);
	}

	/**
	 * 删除单个文件
	 * 
	 * @param 文件的全路径名
	 */
	public static boolean deleteOneFile(String filePath) {
		File file = new File(filePath);
		boolean result = file.delete();
		file = null;
		return result;
	}

	/**
	 * 将文件从原来的位置移动到新位置
	 * 
	 * @param oldFilePath
	 *            文件原来的全路径名
	 * @param newFilePath
	 *            文件的目的全路径名
	 */
	public static boolean moveFile(String oldFilePath, String newFilePath) {
		File oldFile = new File(oldFilePath);
		File newFile = new File(newFilePath);
		boolean result = oldFile.renameTo(newFile);

		oldFile = null;
		newFile = null;
		return result;
	}

	public static boolean mkdir(String dir) {
		File file = new File(dir);
		return file.mkdirs();
	}

	/**
	 * 判断路径是否为合理的目录文件的路径，如果为合理的目录文件的路径，则如果不存在此目录，创建此目录
	 * 
	 * @param string
	 *            目录文件的路径
	 * @return true or false
	 */
	public static boolean isDirectory(String string) {
		if (string.contains(ConstantValue.MORE_PATH_STRING)) {
			log.error("路径配置中出现'//'");
			return false;
		} else if (!(string.endsWith("/"))) {
			log.error("路径配置不是以'/'结尾");
			return false;
		} else if (string.startsWith("/")) {
			log.error("路径配置是以'/'开始");
			return false;
		} else if (string.split("/").length > 20) {
			log.error("路径过深");
			return false;
		}
		File file = new File(string);
		if (file.exists()) {
			if (file.isDirectory()) {
				return true;
			}

		} else {
			if (file.mkdirs()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检测指定路径的文件目录是否存在
	 * 
	 * @param filePath
	 * @param errorCode
	 * @return
	 */
	public static boolean checkDirectory(String filePath, int errorCode) {
		File file = new File(filePath);
		if (!file.exists() || !file.isDirectory()) {
			System.out.println(ErrorCode.getErrorMessage(errorCode)
					+ ConstantValue.PATH + filePath);
			return false;
		}
		return true;
	}

	/**
	 * 检测指定路径的文件是否存在
	 * 
	 * @param filePath
	 * @param errorCode
	 * @return
	 */
	public static boolean checkFile(String filePath, int errorCode) {
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			System.out.println(ErrorCode.getErrorMessage(errorCode)
					+ ConstantValue.PATH + filePath);
			return false;
		}
		return true;
	}

	/**
	 * 检测指定路径的文件是否存在
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean checkFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory()) {
			return false;
		}
		return true;
	}

	/** 加载config目录下的系统配置文件 */
	public static Map<String, String> loadConfigFile(String filePath) {
		String line = ConstantValue.NULL_STRING;
		Map<String, String> configMap = new HashMap<String, String>();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_NOT_FOUND_ERROR)
					+ ConstantValue.PATH + filePath + e.getMessage());
			return null;
		}
		String[] strArray = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line == null || line.equals(ConstantValue.NULL_STRING)
						|| StringHelper.isComment(line)) {
					continue;
				}
				strArray = line.split(ConstantValue.EQUAL_OPERATOR);
				if (strArray.length < 2) {
					continue;
				} else if (strArray.length == 2) {
					configMap.put(strArray[0].trim(), strArray[1].trim());
				} else {
					log.error(ErrorCode
							.getErrorMessage(ErrorCode.LINE_INFORMATION_NOT_STARDAND_ERROR)
							+ ConstantValue.LINE + line);
					return null;
				}
			}
		} catch (IOException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
					+ e.getMessage());
			return null;
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
						+ e.getMessage());
			}
		}
		return configMap;
	}

	/**
	 * 将配置文件中的内容加载到Config的对应字段中
	 * 
	 * @param hiepConfigMap
	 */
	public static int initConfigIniFile(String filePath) {
		Map<String, String> configMap = loadConfigFile(filePath);
		if (configMap == null) {
			return ErrorCode.LOAD_HIEP_CONFIG_INI_ERROR;
		}
		/** HIEP地址信息配置小节 */
		final String LOCAL_ADDRESS = "LOCAL_ADDRESS";
		final String LOCAL_SERVER_ADDRESS_NUM = "LOCAL_SERVER_ADDRESS_NUM";
		final String LOCAL_SERVER_ADDRESS = "LOCAL_SERVER_ADDRESS";
		/** 任务记录队列配置小节 */
		final String TASK_QUEUE_CONTAINER_NAME = "TASK_QUEUE_CONTAINER_NAME";
		final String TASK_QUEUE_COUNT = "TASK_QUEUE_COUNT";
		final String TASK_QUEUE_NAME = "TASK_QUEUE_NAME";
		final String TASK_QUEUE_LOCAL_NAME = "TASK_QUEUE_LOCAL_NAME";
		final String TASK_QUEUE_REMOTE_NAME = "TASK_QUEUE_REMOTE_NAME";
		final String MAX_TASK_LIST_COUNT = "MAX_TASK_LIST_COUNT";
		/** 消息队列目录配置小节 */
		final String RECEIVED_MESSAGE_FILE_DIRECTORY = "RECEIVED_MESSAGE_FILE_DIRECTORY";
		final String NEW_MESSAGE_FILE_DIRECTORY = "NEW_MESSAGE_FILE_DIRECTORY";
		final String EXPIRED_TIME_MESSAGE_FILE_DIRECTORY = "EXPIRED_TIME_MESSAGE_FILE_DIRECTORY";
		final String ERROR_MESSAGE_FILE_DIRECTORY = "ERROR_MESSAGE_FILE_DIRECTORY";
		final String SEND_MESSAGE_FILE_DIRECTORY = "SEND_MESSAGE_FILE_DIRECTORY";
		/** 系统运行硬件环境配置小节 */
		final String MIN_DISK_SPACE = "MIN_DISK_SPACE";
		final String ROUTER_PORT = "ROUTER_PORT";
		final String DISPATCHER_PORT = "DISPATCHER_PORT";
		final String THREAD_NUMBER = "THREAD_NUMBER";
		// LOCAL_ADDRESS
		Config.LOCAL_ADDRESS = configMap.get(LOCAL_ADDRESS);
		if (StringHelper.isNullString(Config.LOCAL_ADDRESS)) {
			return ErrorCode.LOCAL_ADDRESS_ERROR;
		}
		// LOCAL_SERVER_ADDRESS_NUM
		String string = configMap.get(LOCAL_SERVER_ADDRESS_NUM);
		if (StringHelper.isNumberString(string)) {
			Config.LOCAL_SERVER_ADDRESS_NUM = Integer.parseInt(string);
			if (Config.LOCAL_SERVER_ADDRESS_NUM < 0
					|| Config.LOCAL_SERVER_ADDRESS_NUM >= 10) {
				return ErrorCode.LOCAL_SERVER_ADDRESS_NUM_ERROR;
			} else if (Config.LOCAL_SERVER_ADDRESS_NUM == 0) {
				return ErrorCode.LOCAL_SERVER_ADDRESS_NUM_ZERO;
			}
		} else {
			return ErrorCode.LOCAL_SERVER_ADDRESS_NUM_ERROR;
		}
		// LOCAL_SERVER_ADDRESS
		Config.LOCAL_SERVER_ADDRESS = configMap.get(LOCAL_SERVER_ADDRESS);
		if (StringHelper.isNullString(Config.LOCAL_SERVER_ADDRESS)) {
			return ErrorCode.LOCAL_SERVER_ADDRESS_PARSE_ERROR;
		}
		Config.localServerAddress = AddressHelper
				.parseAddresses(Config.LOCAL_SERVER_ADDRESS);
		if (Config.localServerAddress == null
				|| Config.localServerAddress.length <= 0) {
			return ErrorCode.LOCAL_SERVER_ADDRESS_PARSE_ERROR;
		}
		if (Config.localServerAddress.length != Config.LOCAL_SERVER_ADDRESS_NUM) {
			return ErrorCode.LOCAL_SERVER_ADDRESS_EQUAL_ERROR;
		}
		// TASK_QUEUE_CONTAINER_NAME
		Config.TASK_QUEUE_CONTAINER_NAME = configMap
				.get(TASK_QUEUE_CONTAINER_NAME);
		if (StringHelper.isNullString(Config.TASK_QUEUE_CONTAINER_NAME)) {
			return ErrorCode.TASK_QUEUE_CONTAINER_NAME_ERROR;
		}
		// TASK_QUEUE_COUNT
		string = configMap.get(TASK_QUEUE_COUNT);
		if (StringHelper.isNumberString(string)) {
			Config.TASK_QUEUE_COUNT = Integer.parseInt(string);
			switch (Config.TASK_QUEUE_COUNT) {
			case 1:
				// TASK_QUEUE_NAME
				Config.TASK_QUEUE_NAME = configMap.get(TASK_QUEUE_NAME);
				if (StringHelper.isNullString(Config.TASK_QUEUE_NAME)) {
					return ErrorCode.TASK_QUEUE_NAME_ERROR;
				}
				Config.TASK_QUEUE_LOCAL_NAME = Config.TASK_QUEUE_NAME;
				Config.TASK_QUEUE_REMOTE_NAME = Config.TASK_QUEUE_NAME;
				break;
			case 2:
				// TASK_QUEUE_LOCAL_NAME
				Config.TASK_QUEUE_LOCAL_NAME = configMap
						.get(TASK_QUEUE_LOCAL_NAME);
				if (StringHelper.isNullString(Config.TASK_QUEUE_LOCAL_NAME)) {
					return ErrorCode.TASK_QUEUE_LOCAL_NAME_ERROR;
				}
				// TASK_QUEUE_REMOTE_NAME
				Config.TASK_QUEUE_REMOTE_NAME = configMap
						.get(TASK_QUEUE_REMOTE_NAME);
				if (StringHelper.isNullString(Config.TASK_QUEUE_REMOTE_NAME)) {
					return ErrorCode.TASK_QUEUE_REMOTE_NAME_ERROR;
				}
				break;
			default:
				return ErrorCode.TASK_QUEUE_COUNT_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION + TASK_QUEUE_COUNT
					+ ConstantValue.EQUAL_OPERATOR + Config.TASK_QUEUE_COUNT);
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ TASK_QUEUE_LOCAL_NAME + ConstantValue.EQUAL_OPERATOR
					+ Config.TASK_QUEUE_LOCAL_NAME);
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ TASK_QUEUE_REMOTE_NAME + ConstantValue.EQUAL_OPERATOR
					+ Config.TASK_QUEUE_REMOTE_NAME);
		}
		// MAX_TASK_LIST_COUNT
		string = configMap.get(MAX_TASK_LIST_COUNT);
		if (StringHelper.isNumberString(string)) {
			Config.MAX_TASK_LIST_COUNT = Integer.parseInt(string);
			if (Config.LOCAL_SERVER_ADDRESS_NUM < 1
					|| Config.LOCAL_SERVER_ADDRESS_NUM >= 2000) {
				return ErrorCode.LOCAL_SERVER_ADDRESS_NUM_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ MAX_TASK_LIST_COUNT + ConstantValue.EQUAL_OPERATOR
					+ Config.MAX_TASK_LIST_COUNT);
		}
		// RECEIVED_MESSAGE_FILE_DIRECTORY
		string = configMap.get(RECEIVED_MESSAGE_FILE_DIRECTORY);
		if (!StringHelper.isNullString(string)) {
			if (!isDirectory(string)) {
				return ErrorCode.RECEIVED_MESSAGE_FILE_DIRECTORY_ERROR;
			}
			Config.RECEIVED_MESSAGE_FILE_DIRECTORY = string;
		} else {
			Config.RECEIVED_MESSAGE_FILE_DIRECTORY = Config.HIEP_HOME_PATH
					+ ConstantValue.RELATIVE_RECEIVED_MESSAGE_FILE_DIRECTORY;
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ RECEIVED_MESSAGE_FILE_DIRECTORY
					+ ConstantValue.EQUAL_OPERATOR
					+ Config.RECEIVED_MESSAGE_FILE_DIRECTORY);
			if (!isDirectory(Config.RECEIVED_MESSAGE_FILE_DIRECTORY)) {
				return ErrorCode.RECEIVED_MESSAGE_FILE_DIRECTORY_ERROR;
			}
		}
		// NEW_MESSAGE_FILE_DIRECTORY
		string = configMap.get(NEW_MESSAGE_FILE_DIRECTORY);
		if (!StringHelper.isNullString(string)) {
			if (!isDirectory(string)) {
				return ErrorCode.NEW_MESSAGE_FILE_DIRECTORY_ERROR;
			}
			Config.NEW_MESSAGE_FILE_DIRECTORY = string;
		} else {
			Config.NEW_MESSAGE_FILE_DIRECTORY = Config.HIEP_HOME_PATH
					+ ConstantValue.RELATIVE_NEW_MESSAGE_FILE_DIRECTORY;
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ NEW_MESSAGE_FILE_DIRECTORY + ConstantValue.EQUAL_OPERATOR
					+ Config.NEW_MESSAGE_FILE_DIRECTORY);
			if (!isDirectory(Config.NEW_MESSAGE_FILE_DIRECTORY)) {
				return ErrorCode.NEW_MESSAGE_FILE_DIRECTORY_ERROR;
			}
		}
		// EXPIRED_TIME_MESSAGE_FILE_DIRECTORY
		string = configMap.get(EXPIRED_TIME_MESSAGE_FILE_DIRECTORY);
		if (!StringHelper.isNullString(string)) {
			if (!isDirectory(string)) {
				return ErrorCode.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY_ERROR;
			}
			Config.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY = string;
		} else {
			Config.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY = Config.HIEP_HOME_PATH
					+ ConstantValue.RELATIVE_EXPIRED_TIME_MESSAGE_FILE_DIRECTORY;
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ EXPIRED_TIME_MESSAGE_FILE_DIRECTORY
					+ ConstantValue.EQUAL_OPERATOR
					+ Config.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY);
			if (!isDirectory(Config.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY)) {
				return ErrorCode.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY_ERROR;
			}
		}
		// ERROR_MESSAGE_FILE_DIRECTORY
		string = configMap.get(ERROR_MESSAGE_FILE_DIRECTORY);
		if (!StringHelper.isNullString(string)) {
			if (!isDirectory(string)) {
				return ErrorCode.ERROR_MESSAGE_FILE_DIRECTORY_ERROR;
			}
			Config.ERROR_MESSAGE_FILE_DIRECTORY = string;
		} else {
			Config.ERROR_MESSAGE_FILE_DIRECTORY = Config.HIEP_HOME_PATH
					+ ConstantValue.RELATIVE_ERROR_MESSAGE_FILE_DIRECTORY;
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ ERROR_MESSAGE_FILE_DIRECTORY
					+ ConstantValue.EQUAL_OPERATOR
					+ Config.ERROR_MESSAGE_FILE_DIRECTORY);
			if (!isDirectory(Config.ERROR_MESSAGE_FILE_DIRECTORY)) {
				return ErrorCode.ERROR_MESSAGE_FILE_DIRECTORY_ERROR;
			}
		}
		// SEND_MESSAGE_FILE_DIRECTORY
		string = configMap.get(SEND_MESSAGE_FILE_DIRECTORY);
		if (!StringHelper.isNullString(string)) {
			if (!isDirectory(string)) {
				return ErrorCode.SEND_MESSAGE_FILE_DIRECTORY_ERROR;
			}
			Config.SEND_MESSAGE_FILE_DIRECTORY = string;
		} else {
			Config.SEND_MESSAGE_FILE_DIRECTORY = Config.HIEP_HOME_PATH
					+ ConstantValue.RELATIVE_SEND_MESSAGE_FILE_DIRECTORY;
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ SEND_MESSAGE_FILE_DIRECTORY
					+ ConstantValue.EQUAL_OPERATOR
					+ Config.SEND_MESSAGE_FILE_DIRECTORY);
			if (!isDirectory(Config.SEND_MESSAGE_FILE_DIRECTORY)) {
				return ErrorCode.SEND_MESSAGE_FILE_DIRECTORY_ERROR;
			}
		}
		// MIN_DISK_SPACE
		string = configMap.get(MIN_DISK_SPACE);
		if (StringHelper.isNumberString(string)) {
			int tmp = Integer.parseInt(string);
			if (tmp < 2 || tmp > 65535) {
				return ErrorCode.MIN_DISK_SPACE_CONFIG_ERROR;
			}
			Config.MIN_DISK_SPACE = tmp * 1024L * 1024L * 1024L;
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION + MIN_DISK_SPACE
					+ ConstantValue.EQUAL_OPERATOR + Config.MIN_DISK_SPACE);
		}
		// ROUTER_PORT
		string = configMap.get(ROUTER_PORT);
		if (StringHelper.isNumberString(string)) {
			Config.ROUTER_PORT = Integer.parseInt(string);
			if (Config.ROUTER_PORT < 1025 || Config.ROUTER_PORT > 65535) {
				return ErrorCode.ROUTER_PORT_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION + ROUTER_PORT
					+ ConstantValue.EQUAL_OPERATOR + Config.ROUTER_PORT);
		}
		// DISPATCHER_PORT
		string = configMap.get(DISPATCHER_PORT);
		if (StringHelper.isNumberString(string)) {
			Config.DISPATCHER_PORT = Integer.parseInt(string);
			if (Config.DISPATCHER_PORT < 1025 || Config.DISPATCHER_PORT > 65535) {
				return ErrorCode.DISPATCHER_PORT_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION + DISPATCHER_PORT
					+ ConstantValue.EQUAL_OPERATOR + Config.DISPATCHER_PORT);
		}
		if (Config.DISPATCHER_PORT == Config.ROUTER_PORT) {
			return ErrorCode.DISPATCHER_PORT_EQUAL_ROUTER_PORT_ERROR;
		}
		// THREAD_NUMBER
		string = configMap.get(THREAD_NUMBER);
		if (StringHelper.isNumberString(string)) {
			Config.THREAD_NUMBER = Integer.parseInt(string);
			if (Config.THREAD_NUMBER < 0 || Config.DISPATCHER_PORT < 1024) {
				return ErrorCode.THREAD_NUMBER_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION + THREAD_NUMBER
					+ ConstantValue.EQUAL_OPERATOR + Config.THREAD_NUMBER);
		}
		return ErrorCode.NO_ERROR;
	}

	/** 加载config目录下的config_custom.ini文件内容 */
	public static int loadConfigCustomIniFile(String filePath) {
		Map<String, String> configMap = loadConfigFile(filePath);
		if (configMap == null) {
			return ErrorCode.LOAD_HIEP_CONFIG_CUSTOM_INI_ERROR;
		}
		/** 系统轮循时间配置小节 */
		final String ROUTER_PROCESSING_INTERVAL = "ROUTER_PROCESSING_INTERVAL";
		final String DISPARTER_PROCESSING_INTERVAL = "DISPARTER_PROCESSING_INTERVAL";
		/** 消息处理配置小节 */
		final String RECEIVE_MESSAGE_PRIORITY_MODE = "RECEIVE_MESSAGE_PRIORITY_MODE";
		final String LOCAL_MESSAGE_PROCESS_PRIORITY_MODE = "LOCAL_MESSAGE_PROCESS_PRIORITY_MODE";
		final String REMOTE_MESSAGE_PROCESS_PRIORITY_MODE = "REMOTE_MESSAGE_PROCESS_PRIORITY_MODE";
		final String RELATIVE_EXPIRED_TIME = "RELATIVE_EXPIRED_TIME";
		final String EXCHANGE_TRACE_MODE = "EXCHANGE_TRACE_MODE";

		// ROUTER_PROCESSING_INTERVAL
		String string = configMap.get(ROUTER_PROCESSING_INTERVAL);
		if (StringHelper.isNumberString(string)) {
			Config.ROUTER_PROCESSING_INTERVAL = Integer.parseInt(string);
			if (Config.ROUTER_PROCESSING_INTERVAL < 1
					|| Config.ROUTER_PROCESSING_INTERVAL > 65535) {
				return ErrorCode.ROUTER_PROCESSING_INTERVAL_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ ROUTER_PROCESSING_INTERVAL + ConstantValue.EQUAL_OPERATOR
					+ Config.ROUTER_PROCESSING_INTERVAL);
		}
		// DISPARTER_PROCESSING_INTERVAL
		string = configMap.get(DISPARTER_PROCESSING_INTERVAL);
		if (StringHelper.isNumberString(string)) {
			Config.DISPARTER_PROCESSING_INTERVAL = Integer.parseInt(string);
			if (Config.DISPARTER_PROCESSING_INTERVAL < 1
					|| Config.DISPARTER_PROCESSING_INTERVAL > 65535) {
				return ErrorCode.DISPARTER_PROCESSING_INTERVAL_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ DISPARTER_PROCESSING_INTERVAL
					+ ConstantValue.EQUAL_OPERATOR
					+ Config.DISPARTER_PROCESSING_INTERVAL);
		}
		// RECEIVE_MESSAGE_PRIORITY_MODE
		string = configMap.get(RECEIVE_MESSAGE_PRIORITY_MODE);
		if (StringHelper.isNumberString(string)) {
			Config.RECEIVE_MESSAGE_PRIORITY_MODE = Integer.parseInt(string);
			if (Config.RECEIVE_MESSAGE_PRIORITY_MODE < 1
					|| Config.RECEIVE_MESSAGE_PRIORITY_MODE > 3) {
				return ErrorCode.RECEIVE_MESSAGE_PRIORITY_MODE_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ RECEIVE_MESSAGE_PRIORITY_MODE
					+ ConstantValue.EQUAL_OPERATOR
					+ Config.RECEIVE_MESSAGE_PRIORITY_MODE);
		}
		// LOCAL_MESSAGE_PROCESS_PRIORITY_MODE
		string = configMap.get(LOCAL_MESSAGE_PROCESS_PRIORITY_MODE);
		if (StringHelper.isNumberString(string)) {
			Config.LOCAL_MESSAGE_PROCESS_PRIORITY_MODE = Integer
					.parseInt(string);
			if (Config.LOCAL_MESSAGE_PROCESS_PRIORITY_MODE < 1
					|| Config.LOCAL_MESSAGE_PROCESS_PRIORITY_MODE > 3) {
				return ErrorCode.LOCAL_MESSAGE_PROCESS_PRIORITY_MODE_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ LOCAL_MESSAGE_PROCESS_PRIORITY_MODE
					+ ConstantValue.EQUAL_OPERATOR
					+ Config.LOCAL_MESSAGE_PROCESS_PRIORITY_MODE);
		}
		// REMOTE_MESSAGE_PROCESS_PRIORITY_MODE
		string = configMap.get(REMOTE_MESSAGE_PROCESS_PRIORITY_MODE);
		if (StringHelper.isNumberString(string)) {
			Config.REMOTE_MESSAGE_PROCESS_PRIORITY_MODE = Integer
					.parseInt(string);
			if (Config.REMOTE_MESSAGE_PROCESS_PRIORITY_MODE < 1
					|| Config.REMOTE_MESSAGE_PROCESS_PRIORITY_MODE > 3) {
				return ErrorCode.REMOTE_MESSAGE_PROCESS_PRIORITY_MODE_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ REMOTE_MESSAGE_PROCESS_PRIORITY_MODE
					+ ConstantValue.EQUAL_OPERATOR
					+ Config.LOCAL_MESSAGE_PROCESS_PRIORITY_MODE);
		}
		// RELATIVE_EXPIRED_TIME
		string = configMap.get(RELATIVE_EXPIRED_TIME);
		if (StringHelper.isNumberString(string)) {
			Config.RELATIVE_EXPIRED_TIME = Integer.parseInt(string);
			if (Config.RELATIVE_EXPIRED_TIME < 1) {
				return ErrorCode.RELATIVE_EXPIRED_TIME_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ RELATIVE_EXPIRED_TIME + ConstantValue.EQUAL_OPERATOR
					+ Config.RELATIVE_EXPIRED_TIME);
		}
		// EXCHANGE_TRACE_MODE
		string = configMap.get(EXCHANGE_TRACE_MODE);
		if (StringHelper.isNumberString(string)) {
			Config.EXCHANGE_TRACE_MODE = Integer.parseInt(string);
			if (Config.EXCHANGE_TRACE_MODE < 1
					|| Config.EXCHANGE_TRACE_MODE > 2) {
				return ErrorCode.EXCHANGE_TRACE_MODE_ERROR;
			}
		} else {
			log.info(ConstantValue.USE_DEFAULT_CONFIGURATION
					+ EXCHANGE_TRACE_MODE + ConstantValue.EQUAL_OPERATOR
					+ Config.RELATIVE_EXPIRED_TIME);
		}
		return ErrorCode.NO_ERROR;
	}

	/** 加载config目录下的config_custom.ini文件内容，只加载本地地址出发的所有路由记录 */
	public static List<RouterRecord> initRouterTableFile(String filePath,
			String localAddress) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_NOT_FOUND_ERROR)
					+ ConstantValue.PATH + filePath + e.getMessage());
			return null;
		}
		List<String> targetList = new ArrayList<String>();
		List<RouterRecord> routerRecordList = new ArrayList<RouterRecord>();
		String[] addressArray = null;
		RouterRecord routerRecord = null;
		String line = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line == null || line.equals(ConstantValue.NULL_STRING)
						|| StringHelper.isComment(line)) {
					continue;
				}
				addressArray = line.split(ConstantValue.TAB_OR_SPACE);
				if (addressArray.length == 3) {
					if (localAddress.equals(addressArray[0])) {
						if (!targetList.contains(addressArray[1])) {
							targetList.add(addressArray[1]);
							routerRecord = new RouterRecord(localAddress,
									addressArray[1], addressArray[2]);
							routerRecordList.add(routerRecord);
						} else {
							log.error(ErrorCode
									.getErrorMessage(ErrorCode.DUPLICATE_TARGET)
									+ ConstantValue.LINE + line);
							return null;
						}
					}
					continue;
				} else {
					log.error(ErrorCode
							.getErrorMessage(ErrorCode.LINE_INFORMATION_NOT_STARDAND_ERROR)
							+ ConstantValue.LINE + line);
					return null;
				}
			}
		} catch (IOException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
					+ e.getMessage());
			return null;
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
						+ e.getMessage());
			}
		}
		return routerRecordList;
	}

	/** 加载config目录下的HIEP_addressMap.ini文件内容 */
	public static Map<String, Address> initAddressMapFile(String filePath) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_NOT_FOUND_ERROR)
					+ ConstantValue.PATH + filePath + e.getMessage());
			return null;
		}
		String[] strArray = null;
		String line = ConstantValue.NULL_STRING;
		Map<String, Address> addressMap = new HashMap<String, Address>();
		Address address = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line == null || line.equals(ConstantValue.NULL_STRING)
						|| StringHelper.isComment(line)) {
					continue;
				}
				strArray = line.split(ConstantValue.EQUAL_OPERATOR);
				if (strArray.length == 2) {
					address = AddressHelper.parse(strArray[1].trim());
					if (address == null) {
						log.error(ErrorCode
								.getErrorMessage(ErrorCode.ADDRESS_PARSE_ERROR)
								+ ConstantValue.LINE + line);
						return null;
					}
					addressMap.put(strArray[0].trim(), address);
				} else {
					log.error(ErrorCode
							.getErrorMessage(ErrorCode.LINE_INFORMATION_NOT_STARDAND_ERROR)
							+ ConstantValue.LINE + line);
					return null;
				}
			}
		} catch (IOException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
					+ e.getMessage());
			return null;
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
						+ e.getMessage());
			}
		}
		return addressMap;
	}

	/** 检测指定路径的硬盘剩余空间是否大于给定值 */
	public static boolean checkDiskSpace(long minDiskSpace, String filePath) {
		File win = new File(filePath);
		return (win.getFreeSpace() > minDiskSpace);
	}

	/** 创建指定路径的文件 */
	public static boolean createFile(String filePath) {
		File file = new File(filePath);
		boolean flag = false;
		if (!file.exists()) {
			try {
				flag = file.createNewFile();
			} catch (IOException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
						+ e.getMessage());
			}
		}
		return flag;
	}

	/** 加载config目录下的HIEP_processorMap.ini */
	public static Map<String, String> initProcessorMapFile(String filePath) {
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_NOT_FOUND_ERROR)
					+ ConstantValue.PATH + filePath + e.getMessage());
			return null;
		}
		Map<String, String> processorNameMap = new HashMap<String, String>();
		String[] stringArray = null;
		String line = null;
		String subAddressAndType = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line == null || line.equals(ConstantValue.NULL_STRING)
						|| StringHelper.isComment(line)) {
					continue;
				}
				stringArray = line.split(ConstantValue.TAB_OR_SPACE);
				if (stringArray.length == 3) {
					subAddressAndType = stringArray[0].trim()
							+ stringArray[1].trim();
					if (!processorNameMap.containsKey(subAddressAndType)) {
						processorNameMap.put(subAddressAndType,
								stringArray[2].trim());
					} else {
						log.error(ErrorCode
								.getErrorMessage(ErrorCode.DUPLICATE_PROCESSOR_NAME)
								+ ConstantValue.SUBADRESS
								+ stringArray[0]
								+ ConstantValue.TYPE + stringArray[1]);
						return null;
					}
				} else if (stringArray.length == 2) {
					subAddressAndType = stringArray[0].trim();
					if (!processorNameMap.containsKey(subAddressAndType)) {
						processorNameMap.put(subAddressAndType,
								stringArray[1].trim());
					} else {
						log.error(ErrorCode
								.getErrorMessage(ErrorCode.DUPLICATE_PROCESSOR_NAME)
								+ ConstantValue.SUBADRESS + stringArray[0]);
						return null;
					}
				} else {
					log.error(ErrorCode
							.getErrorMessage(ErrorCode.LINE_INFORMATION_NOT_STARDAND_ERROR)
							+ ConstantValue.LINE + line);
					return null;
				}
			}
		} catch (IOException e) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
					+ e.getMessage());
			return null;
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				log.error(ErrorCode.getErrorMessage(ErrorCode.FILE_IO_ERROR)
						+ e.getMessage());
			}
		}
		return processorNameMap;
	}

	public static boolean moveToDelete(String fileName) {
		File file = new File(fileName);
		String newFilePath = Config.DELETE_MESSAGE_FILE_DIRECTORY
				+ file.getName();
		return moveFile(fileName, newFilePath);
	}

	public static boolean moveFromReceivedToDelete(String sendIdentify) {
		String oldFilePath = Config.RECEIVED_MESSAGE_FILE_DIRECTORY
				+ sendIdentify;
		String newFilePath = Config.DELETE_MESSAGE_FILE_DIRECTORY
				+ sendIdentify;
		return moveFile(oldFilePath, newFilePath);
	}

	public static boolean moveFromNewToDelete(String sendIdentify) {
		String oldFilePath = Config.NEW_MESSAGE_FILE_DIRECTORY + sendIdentify;
		String newFilePath = Config.DELETE_MESSAGE_FILE_DIRECTORY
				+ sendIdentify;
		return moveFile(oldFilePath, newFilePath);
	}
}
