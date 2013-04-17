package com.greatsoft.transq.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.core.RouterRecord;
import com.greatsoft.transq.core.task.TaskQueueManagerImp;

public class ConfigHelper {
	private static Logger log = Log.getLog(ConfigHelper.class);

	/** ����ע����е�HIEP_HOME_PATH */
	public static boolean loadHiepHomePath() {
		if (!RegistryHelper.getHiepHomePath()) {
			System.out.println(ErrorCode
					.getErrorMessage(ErrorCode.LOAD_HIEP_HOME_PATH_ERROR));
			return false;
		}
		return true;
	}

	/** �����Ҫ�ıر��ļ�Ŀ¼�������ļ��Ƿ���� */
	public static boolean checkMainDirectory() {
		/** ���HIEP_HOME_PATHĿ¼�Ƿ���� */
		if (!FileHelper.checkDirectory(Config.HIEP_HOME_PATH,
				ErrorCode.HIEP_HOME_PATH_DIRECTORY_ERROR)) {
			return false;
		}
		/** ���CONFIG_DIRECTORYĿ¼�Ƿ���� */
		if (!FileHelper.checkDirectory(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY,
				ErrorCode.CONFIG_DIRECTORY_ERROR)) {
			return false;
		}
		/** ���EXT_DIRECTORYĿ¼�Ƿ���� */
		if (!FileHelper.checkDirectory(Config.HIEP_HOME_PATH
				+ ConstantValue.EXT_DIRECTORY, ErrorCode.EXT_DIRECTORY_ERROR)) {
			return false;
		}
		/** ���HIEP_config.ini�ļ��Ƿ���� */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.HIEP_CONFIG_FILE_NAME,
				ErrorCode.HIEP_CONFIG_FILE_ERROR)) {
			return false;
		}
		/** ���HIEP_config_custom.ini�ļ��Ƿ���� */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.HIEP_CONFIG_CUSTOM_FILE_NAME,
				ErrorCode.HIEP_CONFIG_CUSTOM_FILE_ERROR)) {
			return false;
		}
		/** ���dispatcherlog4j.properties�ļ��Ƿ���� */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPARTER_LOG4J_PROPERTIES_FILE_NAME,
				ErrorCode.DISPATCHER_LOG_4J_FILE_ERROR)) {
			return false;
		}
		/** ���routerlog4j.properties�ļ��Ƿ���� */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ROUTER_LOG4J_PROPERTIES_FILE_NAME,
				ErrorCode.ROUTER_LOG_4J_FILE_ERROR)) {
			return false;
		}
		/** ���HIEP_routerTable.ini�ļ��Ƿ���� */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ROUTER_TABLE_FILE_NAME,
				ErrorCode.ROUTER_TABLE_FILE_ERROR)) {
			return false;
		}
		/** ���HIEP_processorMap.ini�ļ��Ƿ���� */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.PROCESSOR_MAP_FILE_NAME,
				ErrorCode.PROCESSOR_MAP_FILE_ERROR)) {
			return false;
		}
		/** ���HIEP_addressMap.ini�ļ��Ƿ���� */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ADDRESS_MAP_FILE_NAME,
				ErrorCode.ADDRESS_MAP_FILE_ERROR)) {
			return false;
		}
		/** ���DELETE_MESSAGE_PATHĿ¼�Ƿ���� */
		Config.DELETE_MESSAGE_FILE_DIRECTORY = Config.HIEP_HOME_PATH
				+ ConstantValue.DELETE_MESSAGE_FILE_DIRECTORY;
		if (!FileHelper.isDirectory(Config.DELETE_MESSAGE_FILE_DIRECTORY)) {
			return false;
		}
		return true;
	}

	/** ��ʼ����־ */
	public static boolean createLog(String filePath) {
		if (!Log.init(filePath)) {
			System.out.println(ErrorCode
					.getErrorMessage(ErrorCode.CREATE_LOG_ERROR));
			return false;
		}
		System.out.println("��־��ʼ���ɹ�");
		return true;
	}

	/** ����configĿ¼�µ�config.ini�ļ����ݺ�config_custom.ini�ļ����� */
	public static boolean loadConfigFile() {
		int result = FileHelper.initConfigIniFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.HIEP_CONFIG_FILE_NAME);
		if (result == ErrorCode.LOCAL_SERVER_ADDRESS_NUM_ZERO) {
			log.info("û��������Ϣ�м��");
			return false;
		} else if (result != ErrorCode.NO_ERROR) {
			log.error(ErrorCode.getErrorMessage(result));
			return false;
		}
		result = FileHelper.loadConfigCustomIniFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.HIEP_CONFIG_CUSTOM_FILE_NAME);
		if (result != ErrorCode.NO_ERROR) {
			log.error(ErrorCode.getErrorMessage(result));
			return false;
		}
		return true;
	}

	/** ӳ��·�ɱ�HIEP_routerTable.ini���ڴ� */
	public static boolean loadRouterTableConfig() {
		Config.routerRecordList = FileHelper.initRouterTableFile(
				Config.HIEP_HOME_PATH + ConstantValue.CONFIG_DIRECTORY
						+ ConstantValue.ROUTER_TABLE_FILE_NAME,
				Config.LOCAL_ADDRESS);
		if (null == Config.routerRecordList
				|| Config.routerRecordList.isEmpty()) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.LOAD_ROUTER_TABLE_CONFIG_ERROR));
			return false;
		}
		/** ���·�ɼ�¼�е�������һ����ַ�����ļ�HIEP_addressMap.ini����Ӧ���� */
		Map<String, Address> addressMap = FileHelper
				.initAddressMapFile(Config.HIEP_HOME_PATH
						+ ConstantValue.CONFIG_DIRECTORY
						+ ConstantValue.ADDRESS_MAP_FILE_NAME);
		if (addressMap == null || addressMap.isEmpty()) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.LOAD_ADDRESS_MAP_CONFIG_ERROR));
			return false;
		}
		if (!checkRouterAddress(addressMap, Config.routerRecordList)) {
			return false;
		}
		return true;
	}

	/** ���·�ɼ�¼�е�������һ����ַ�������ж�Ӧ�Ĵ����ĵ�ַ */
	public static boolean checkRouterAddress(Map<String, Address> addressMap,
			List<RouterRecord> routerRecordList) {
		if (addressMap == null || addressMap.isEmpty()
				|| routerRecordList == null || routerRecordList.isEmpty()) {
			return false;
		}
		String next = null;
		for (RouterRecord routerRecord : routerRecordList) {
			next = routerRecord.getnext();
			if (!addressMap.containsKey(next)) {
				log.error(ErrorCode
						.getErrorMessage(ErrorCode.ROUTER_NEXT_ADDRESS_MISSING_ADDRESS_ERROR)
						+ ConstantValue.ADDRESS + next);
				return false;
			}
		}
		return true;
	}

	/** ��Ⲣ��ʼ�����ݿ��ʼ���� */
	public static boolean loadDataBase() {
		if (FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ Config.TASK_QUEUE_CONTAINER_NAME)) {
			log.info("�����¼���й�����������");
		} else {
			log.info("�����¼���й�������������");
		}
		if (!TaskQueueManagerImp.getInstance(Config.TASK_QUEUE_CONTAINER_NAME)
				.init(Config.TASK_QUEUE_CONTAINER_NAME)) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.CREATE_DATABASE_ERROR));
			return false;
		}
		return true;
	}

	/** ���Ӳ��ʣ��ռ��Ƿ��㹻 */
	public static boolean checkEnvironment() {
		if (!FileHelper.checkDiskSpace(Config.MIN_DISK_SPACE,
				Config.HIEP_HOME_PATH)) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.MIN_DISK_SPACE_ERROR));
			return false;
		}
		return true;
	}

	/** ����configĿ¼�µ�HIEP_addressMap.ini */
	public static boolean loadAddressMapConfig() {
		Config.addressMap = FileHelper.initAddressMapFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ADDRESS_MAP_FILE_NAME);
		if (null == Config.addressMap || Config.addressMap.isEmpty()) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.LOAD_ADDRESS_MAP_CONFIG_ERROR));
			return false;
		}
		List<RouterRecord> routerRecordList = FileHelper.initRouterTableFile(
				Config.HIEP_HOME_PATH + ConstantValue.CONFIG_DIRECTORY
						+ ConstantValue.ROUTER_TABLE_FILE_NAME,
				Config.LOCAL_ADDRESS);
		if (null == routerRecordList || routerRecordList.isEmpty()) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.LOAD_ROUTER_TABLE_CONFIG_ERROR));
			return false;
		}
		if (!checkRouterAddress(Config.addressMap, routerRecordList)) {
			return false;
		}
		return true;
	}

	/** ����configĿ¼�µ�HIEP_processorMap.ini */
	public static boolean loadProcessorMapConfig() {
		Config.processorNameMap = FileHelper
				.initProcessorMapFile(Config.HIEP_HOME_PATH
						+ ConstantValue.CONFIG_DIRECTORY
						+ ConstantValue.PROCESSOR_MAP_FILE_NAME);
		if (null == Config.processorNameMap
				|| Config.processorNameMap.isEmpty()) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.LOAD_PROCESSOR_MAP_CONFIG_ERROR));
			return false;
		}
		return true;
	}

	/** ����EXTĿ¼�µ�����JAR�ܰ� */
	public static boolean loadThirdJar() {
		File extDirectory = new File(Config.HIEP_HOME_PATH
				+ ConstantValue.EXT_DIRECTORY);
		File[] jarFiles = extDirectory.listFiles();
		if (jarFiles == null || jarFiles.length == 0) {
			return false;
		}
		String filePath = null;
		for (int i = 0; i < jarFiles.length; i++) {
			filePath = jarFiles[i].getAbsolutePath();
			if (filePath.endsWith(ConstantValue.JAR)) {
				if (!loadOneThirdJar(filePath)) {
					log.error(ErrorCode.LOAD_THIRD_JAR_ERROR
							+ ConstantValue.PATH + filePath);
					return false;
				}
			}
		}
		return true;
	}

	/** ����EXTĿ¼�µ�һ���ܰ� */
	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	public static boolean loadOneThirdJar(String jarPath) {
		URL urls[] = null;
		try {
			/** ��·������ */
			urls = new URL[] { new File(jarPath).toURL() };
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
			return false;
		}
		/** ��ȡ��װ������Ȼ���÷��䷽���������������addURL���������µ�jar·����ӽ�ȥ */
		URLClassLoader urlLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
		Method method = null;
		try {
			method = sysclass.getDeclaredMethod(ConstantValue.ADDURL,
					new Class[] { URL.class });
		} catch (SecurityException e) {
			log.error(e.getMessage());
			return false;
		} catch (NoSuchMethodException e) {
			log.error(e.getMessage());
			return false;
		}
		if (null == method) {
			return false;
		}
		method.setAccessible(true);
		try {
			method.invoke(urlLoader, urls);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage());
			return false;
		} catch (IllegalAccessException e) {
			log.error(e.getMessage());
			return false;
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
			return false;
		}
		return true;
	}

	/** �ر������ݿ������ */
	public static void closeDataBase() {
		TaskQueueManagerImp.getInstance(Config.TASK_QUEUE_CONTAINER_NAME)
				.closeConneciton();
	}
}
