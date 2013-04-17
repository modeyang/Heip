package com.greatsoft.server.dispatcher;

import java.io.File;

import org.apache.log4j.Logger;

import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;

public class DeleteFileWorker implements Runnable {

	private static Logger log = Logger.getLogger(DeleteFileWorker.class);

	private boolean isRunning = true;

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public DeleteFileWorker(boolean flag) {
		this.isRunning = flag;
	}

	@Override
	public void run() {

		while (isRunning) {
			log.info("DeleteFileWorker 开始工作");

			/** 删除删除队列中的消息文件 */
			doWork(Config.HIEP_HOME_PATH
					+ ConstantValue.DELETE_MESSAGE_FILE_DIRECTORY);
			/** 删除超时 */
			doWork(Config.HIEP_HOME_PATH
					+ Config.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY);
			/** 删除文件目录模拟TLQ的中间消息文件 */
			doWork(Config.ERROR_MESSAGE_FILE_DIRECTORY);

			try {
				Thread.sleep(ConstantValue.THRED_GET_WRITE_DATABASE_INTERVAL * 500);
			} catch (InterruptedException e) {
				log.error("睡眠中断");
			}
		}
	}

	public static boolean doWork(String path) {
		File file = new File(path);
		File[] files = file.listFiles();

		if (files != null && files.length != 0) {

			for (int i = 0; i < files.length; i++) {
				File tempFile = files[i];
				if (tempFile.exists() && tempFile.isFile()) {
					if (tempFile.delete()) {
						log.info("DeleteFileWorker删除文件" + path + "成功");
					} else {
						log.error("DeleteFileWorker删除文件" + path + "失败");
					}
				}

				files[i] = null;
			}
			return true;
		}

		file = null;
		return false;
	}

}
