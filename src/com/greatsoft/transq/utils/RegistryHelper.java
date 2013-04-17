package com.greatsoft.transq.utils;

import com.ice.jni.registry.NoSuchKeyException;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;

public class RegistryHelper {
	/** Ŀǰֻ��window�����´�ע�����ȡ��HIEP_HOME_PATH��ֵ */
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
	 * ��ȡWindow����ϵͳ�е�ע����HKEY_CURRENT_USER\Software\HIEP\CurrentVersion��ֵ
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
