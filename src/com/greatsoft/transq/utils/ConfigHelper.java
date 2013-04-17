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

	/** 加载注册表中的HIEP_HOME_PATH */
	public static boolean loadHiepHomePath() {
		if (!RegistryHelper.getHiepHomePath()) {
			System.out.println(ErrorCode
					.getErrorMessage(ErrorCode.LOAD_HIEP_HOME_PATH_ERROR));
			return false;
		}
		return true;
	}

	/** 检查主要的必备文件目录和配置文件是否存在 */
	public static boolean checkMainDirectory() {
		/** 检测HIEP_HOME_PATH目录是否存在 */
		if (!FileHelper.checkDirectory(Config.HIEP_HOME_PATH,
				ErrorCode.HIEP_HOME_PATH_DIRECTORY_ERROR)) {
			return false;
		}
		/** 检测CONFIG_DIRECTORY目录是否存在 */
		if (!FileHelper.checkDirectory(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY,
				ErrorCode.CONFIG_DIRECTORY_ERROR)) {
			return false;
		}
		/** 检测EXT_DIRECTORY目录是否存在 */
		if (!FileHelper.checkDirectory(Config.HIEP_HOME_PATH
				+ ConstantValue.EXT_DIRECTORY, ErrorCode.EXT_DIRECTORY_ERROR)) {
			return false;
		}
		/** 检测HIEP_config.ini文件是否存在 */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.HIEP_CONFIG_FILE_NAME,
				ErrorCode.HIEP_CONFIG_FILE_ERROR)) {
			return false;
		}
		/** 检测HIEP_config_custom.ini文件是否存在 */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.HIEP_CONFIG_CUSTOM_FILE_NAME,
				ErrorCode.HIEP_CONFIG_CUSTOM_FILE_ERROR)) {
			return false;
		}
		/** 检测dispatcherlog4j.properties文件是否存在 */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.DISPARTER_LOG4J_PROPERTIES_FILE_NAME,
				ErrorCode.DISPATCHER_LOG_4J_FILE_ERROR)) {
			return false;
		}
		/** 检测routerlog4j.properties文件是否存在 */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ROUTER_LOG4J_PROPERTIES_FILE_NAME,
				ErrorCode.ROUTER_LOG_4J_FILE_ERROR)) {
			return false;
		}
		/** 检测HIEP_routerTable.ini文件是否存在 */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ROUTER_TABLE_FILE_NAME,
				ErrorCode.ROUTER_TABLE_FILE_ERROR)) {
			return false;
		}
		/** 检测HIEP_processorMap.ini文件是否存在 */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.PROCESSOR_MAP_FILE_NAME,
				ErrorCode.PROCESSOR_MAP_FILE_ERROR)) {
			return false;
		}
		/** 检测HIEP_addressMap.ini文件是否存在 */
		if (!FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.ADDRESS_MAP_FILE_NAME,
				ErrorCode.ADDRESS_MAP_FILE_ERROR)) {
			return false;
		}
		/** 检测DELETE_MESSAGE_PATH目录是否存在 */
		Config.DELETE_MESSAGE_FILE_DIRECTORY = Config.HIEP_HOME_PATH
				+ ConstantValue.DELETE_MESSAGE_FILE_DIRECTORY;
		if (!FileHelper.isDirectory(Config.DELETE_MESSAGE_FILE_DIRECTORY)) {
			return false;
		}
		return true;
	}

	/** 初始化日志 */
	public static boolean createLog(String filePath) {
		if (!Log.init(filePath)) {
			System.out.println(ErrorCode
					.getErrorMessage(ErrorCode.CREATE_LOG_ERROR));
			return false;
		}
		System.out.println("日志初始化成功");
		return true;
	}

	/** 加载config目录下的config.ini文件内容和config_custom.ini文件内容 */
	public static boolean loadConfigFile() {
		int result = FileHelper.initConfigIniFile(Config.HIEP_HOME_PATH
				+ ConstantValue.CONFIG_DIRECTORY
				+ ConstantValue.HIEP_CONFIG_FILE_NAME);
		if (result == ErrorCode.LOCAL_SERVER_ADDRESS_NUM_ZERO) {
			log.info("没有配置消息中间件");
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

	/** 映射路由表HIEP_routerTable.ini到内存 */
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
		/** 检测路由记录中的所有下一跳地址都在文件HIEP_addressMap.ini有相应配置 */
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

	/** 检测路由记录中的所有下一跳地址都配置有对应的传输层的地址 */
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

	/** 检测并初始化数据库初始环境 */
	public static boolean loadDataBase() {
		if (FileHelper.checkFile(Config.HIEP_HOME_PATH
				+ Config.TASK_QUEUE_CONTAINER_NAME)) {
			log.info("任务记录队列管理容器存在");
		} else {
			log.info("任务记录队列管理容器不存在");
		}
		if (!TaskQueueManagerImp.getInstance(Config.TASK_QUEUE_CONTAINER_NAME)
				.init(Config.TASK_QUEUE_CONTAINER_NAME)) {
			log.error(ErrorCode
					.getErrorMessage(ErrorCode.CREATE_DATABASE_ERROR));
			return false;
		}
		return true;
	}

	/** 检测硬盘剩余空间是否足够 */
	public static boolean checkEnvironment() {
		if (!FileHelper.checkDiskSpace(Config.MIN_DISK_SPACE,
				Config.HIEP_HOME_PATH)) {
			log.error(ErrorCode.getErrorMessage(ErrorCode.MIN_DISK_SPACE_ERROR));
			return false;
		}
		return true;
	}

	/** 加载config目录下的HIEP_addressMap.ini */
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

	/** 加载config目录下的HIEP_processorMap.ini */
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

	/** 加载EXT目录下的所有JAR架包 */
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

	/** 加载EXT目录下的一个架包 */
	@SuppressWarnings({ "deprecation", "rawtypes", "unchecked" })
	public static boolean loadOneThirdJar(String jarPath) {
		URL urls[] = null;
		try {
			/** 包路径定义 */
			urls = new URL[] { new File(jarPath).toURL() };
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
			return false;
		}
		/** 获取类装载器，然后用反射方法调用类加载器的addURL方法，把新的jar路径添加进去 */
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

	/** 关闭与数据库的连接 */
	public static void closeDataBase() {
		TaskQueueManagerImp.getInstance(Config.TASK_QUEUE_CONTAINER_NAME)
				.closeConneciton();
	}
}
