package com.greatsoft.transq.utils;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {

	public static Logger getLog(Class<?> className) {
		return Logger.getLogger(className);
	}

	public static boolean init(String filePath) {

		/** 删掉以前的日志文件data/log目录下面的文件 */
		String filesPath = Config.HIEP_HOME_PATH + "data/log/";
		File[] files = new File(filesPath).listFiles();
		for (File tempFile : files) {
			tempFile.delete();
		}

		File file = new File(filePath);
		
		if (!file.exists() || file.isDirectory()) {
			System.out.println(ErrorCode
					.getErrorMessage(ErrorCode.FILE_NOT_FOUND_ERROR)
					+ ConstantValue.PATH + filePath);
			return false;
		}
		PropertyConfigurator.configure(filePath);
		return true;
	}
}
