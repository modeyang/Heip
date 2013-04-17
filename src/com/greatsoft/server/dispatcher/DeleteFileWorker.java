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
			log.info("DeleteFileWorker ��ʼ����");

			/** ɾ��ɾ�������е���Ϣ�ļ� */
			doWork(Config.HIEP_HOME_PATH
					+ ConstantValue.DELETE_MESSAGE_FILE_DIRECTORY);
			/** ɾ����ʱ */
			doWork(Config.HIEP_HOME_PATH
					+ Config.EXPIRED_TIME_MESSAGE_FILE_DIRECTORY);
			/** ɾ���ļ�Ŀ¼ģ��TLQ���м���Ϣ�ļ� */
			doWork(Config.ERROR_MESSAGE_FILE_DIRECTORY);

			try {
				Thread.sleep(ConstantValue.THRED_GET_WRITE_DATABASE_INTERVAL * 500);
			} catch (InterruptedException e) {
				log.error("˯���ж�");
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
						log.info("DeleteFileWorkerɾ���ļ�" + path + "�ɹ�");
					} else {
						log.error("DeleteFileWorkerɾ���ļ�" + path + "ʧ��");
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
