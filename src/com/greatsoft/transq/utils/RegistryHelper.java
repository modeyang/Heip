package com.greatsoft.transq.utils;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

public class RegistryHelper {
	/** 目前只做window环境下从注册表中取得HIEP_HOME_PATH的值 */
	public static boolean getHiepHomePath() {
		switch (getOS()) {
		case ConstantValue.OS_WINDOWS:
			return getWindowsHiepPath();
		case ConstantValue.OS_UNIX:
			return getUnixHiepPath();
		default:
			return false;
		}
	}

	private static int getOS() {
		return ConstantValue.OS_WINDOWS;
	}

	public static boolean getUnixHiepPath() {
		return true;
	}

	/**
	 * 获取Window操作系统中的注册表的HKEY_CURRENT_USER\Software\HIEP\CurrentVersion的值
	 * 
	 * @return
	 */
	private static boolean getWindowsHiepPath() {

		RegistryKey CurrentVersionKey = null;
		try {
			RegistryKey software = Registry.HKEY_CURRENT_USER
					.openSubKey(ConstantValue.SOFTWARE);
			RegistryKey HIEPKey = software.openSubKey(ConstantValue.HIEP);
			CurrentVersionKey = HIEPKey
					.openSubKey(ConstantValue.CURRENT_VERSION);
			String temp = CurrentVersionKey
					.getStringValue(ConstantValue.HIEP_HOME_PATH);
			Config.HIEP_HOME_PATH = replaceString(temp);
		} catch (NoSuchKeyException e) {
			System.out.println(ErrorCode
					.getErrorMessage(ErrorCode.GET_WINDOWS_HIEP_PATH_ERROR)
					+ e.getMessage());
			return false;
		} catch (RegistryException e) {
			System.out.println(ErrorCode
					.getErrorMessage(ErrorCode.GET_WINDOWS_HIEP_PATH_ERROR)
					+ e.getMessage());
			return false;
		} finally {
			if (CurrentVersionKey != null) {
				try {
					CurrentVersionKey.closeKey();
				} catch (RegistryException e) {
					System.out
							.println(ErrorCode
									.getErrorMessage(ErrorCode.GET_WINDOWS_HIEP_PATH_ERROR)
									+ e.getMessage());
					return false;
				}
			}
		}
		if (null == Config.HIEP_HOME_PATH
				|| Config.HIEP_HOME_PATH.equals(ConstantValue.NULL_STRING)
				|| !FileHelper.isDirectory(Config.HIEP_HOME_PATH)) {
			System.out.println(ErrorCode
					.getErrorMessage(ErrorCode.HIEP_HOME_PATH_ERROR)
					+ Config.HIEP_HOME_PATH);
			return false;
		}
		return true;
	}

	private static String replaceString(String temp) {

		return temp.replaceAll("\\\\", "/");
	}
}
