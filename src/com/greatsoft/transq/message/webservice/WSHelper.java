package com.greatsoft.transq.message.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.greatsoft.transq.utils.ConstantValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;

public class WSHelper {
	private static Logger log = Logger.getLogger(WSHelper.class);

	/** 将配置目录下面的HIEP_processorMap.ini映射到内存中 */
	public static Map<String, String> loadProcessorAddressConfig(String path) {
		String line = "";
		BufferedReader bufferedReader = null;
		Map<String, String> addressmap = new HashMap<String, String>();
		try {
			bufferedReader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			log.error("读取文件失败");
		}
		String[] stringArray = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line == null || line.equals("") || isComment(line)) {
					continue;
				}
				/** 将空格和TAB都分割 */
				stringArray = line.split("(" + (char) 32 + "|" + (char) 9
						+ ")+");
				if (stringArray.length == 3) {
					if (addressmap.containsKey(stringArray[0].trim()
							+ stringArray[1].trim())) {
						/** 键值一样，报错 */
						log.warn("配置文件有相同的记录");
						return null;
					} else {
						addressmap.put(
								stringArray[0].trim() + stringArray[1].trim(),
								stringArray[2].trim());
					}
				} else {
					log.error("配置文件配置有误" + line);
					return null;
				}
			}
		} catch (IOException e1) {
			log.error("读取配置有误");
			return null;
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				log.error("关闭配置出错");
				return null;
			}
			bufferedReader = null;
		}
		return addressmap;
	}

	/**
	 * 初始化Webservice配置文件
	 * 
	 * @param path
	 */
	public static boolean initProperties(String path) {
		Properties properties = new Properties();

		try {
			InputStream inputStream = new BufferedInputStream(
					new FileInputStream(path));
			properties.load(inputStream);
			WSServerMain.ADDRESS = properties.getProperty("LOCALADDRESS");
			ConstantValue.CONNECTION_TIME = properties
					.getProperty("CONNECTION_TIME");
			ConstantValue.WSSERVER_WAITINT_TIME = properties
					.getProperty("WAITINT_TIME");

			if (ConstantValue.CONNECTION_TIME == null
					|| ConstantValue.WSSERVER_WAITINT_TIME == null) {
				return false;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 得到如下地址HKEY_LOCAL_MACHINE\SOFTY\HIEP\CurrentVersion
	 * 
	 * @return
	 */
	public static String getHomePath() {
		RegistryKey software;
		String hiepConfigValue = "";
		try {
			/**
			 * 暂时先写成在注册表的HKEY_CURRENT_USER\Software\HIEP\CurrentVersion位置取得值
			 */
			software = Registry.HKEY_CURRENT_USER.openSubKey("SOFTWARE");
			RegistryKey HIEPKey = software.openSubKey("HIEP");
			RegistryKey CurrentVersionKey = HIEPKey
					.openSubKey("CurrentVersion");

			/**
			 * 找到字串项”HKEY_LOCAL_MACHINE\SoftWare\HIEP\
			 * CurrentVersion项的HIEP_Config”的值
			 */

			hiepConfigValue = CurrentVersionKey.getStringValue("hiepHomePath");
			CurrentVersionKey.closeKey();
		} catch (Exception e) {
			log.error("获取HIEP_HOME_PATH的值出现错误" + e.getMessage());
			return "";
		}
		return hiepConfigValue.replaceAll("\\\\", "/");
	}

	/** 判断字符串是否为注释行 */
	public static boolean isComment(String line) {
		Pattern pattern = Pattern.compile("^#.*");
		Matcher matcher = pattern.matcher(line);
		return matcher.matches();
	}
}
