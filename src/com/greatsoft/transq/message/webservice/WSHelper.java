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

	/** ������Ŀ¼�����HIEP_processorMap.iniӳ�䵽�ڴ��� */
	public static Map<String, String> loadProcessorAddressConfig(String path) {
		String line = "";
		BufferedReader bufferedReader = null;
		Map<String, String> addressmap = new HashMap<String, String>();
		try {
			bufferedReader = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e1) {
			log.error("��ȡ�ļ�ʧ��");
		}
		String[] stringArray = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (line == null || line.equals("") || isComment(line)) {
					continue;
				}
				/** ���ո��TAB���ָ� */
				stringArray = line.split("(" + (char) 32 + "|" + (char) 9
						+ ")+");
				if (stringArray.length == 3) {
					if (addressmap.containsKey(stringArray[0].trim()
							+ stringArray[1].trim())) {
						/** ��ֵһ�������� */
						log.warn("�����ļ�����ͬ�ļ�¼");
						return null;
					} else {
						addressmap.put(
								stringArray[0].trim() + stringArray[1].trim(),
								stringArray[2].trim());
					}
				} else {
					log.error("�����ļ���������" + line);
					return null;
				}
			}
		} catch (IOException e1) {
			log.error("��ȡ��������");
			return null;
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				log.error("�ر����ó���");
				return null;
			}
			bufferedReader = null;
		}
		return addressmap;
	}

	/**
	 * ��ʼ��Webservice�����ļ�
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
	 * �õ����µ�ַHKEY_LOCAL_MACHINE\SOFTY\HIEP\CurrentVersion
	 * 
	 * @return
	 */
	public static String getHomePath() {
		RegistryKey software;
		String hiepConfigValue = "";
		try {
			/**
			 * ��ʱ��д����ע����HKEY_CURRENT_USER\Software\HIEP\CurrentVersionλ��ȡ��ֵ
			 */
			software = Registry.HKEY_CURRENT_USER.openSubKey("SOFTWARE");
			RegistryKey HIEPKey = software.openSubKey("HIEP");
			RegistryKey CurrentVersionKey = HIEPKey
					.openSubKey("CurrentVersion");

			/**
			 * �ҵ��ִ��HKEY_LOCAL_MACHINE\SoftWare\HIEP\
			 * CurrentVersion���HIEP_Config����ֵ
			 */

			hiepConfigValue = CurrentVersionKey.getStringValue("hiepHomePath");
			CurrentVersionKey.closeKey();
		} catch (Exception e) {
			log.error("��ȡHIEP_HOME_PATH��ֵ���ִ���" + e.getMessage());
			return "";
		}
		return hiepConfigValue.replaceAll("\\\\", "/");
	}

	/** �ж��ַ����Ƿ�Ϊע���� */
	public static boolean isComment(String line) {
		Pattern pattern = Pattern.compile("^#.*");
		Matcher matcher = pattern.matcher(line);
		return matcher.matches();
	}
}
