package com.greatsoft.server.router;

import org.apache.log4j.Logger;
import com.greatsoft.transq.utils.FileHelper;

public class ProcessHelper {
	private static Logger log = Logger.getLogger(RouterServer.class);
	
	/**����Ϣ�ӽ�����Ϣ�����ƶ���������Ϣ����*/
	public static void failedProcess(String sendIdentify) {
		if (FileHelper.moveFromReceivedToError(sendIdentify)) {
			log.info("�������¼��Ӧ����Ϣ�ļ����������Ϣ���гɹ���sendIdentify"+sendIdentify);
		} else {
			log.error("�������¼��Ӧ����Ϣ�ļ����������Ϣ����ʧ�ܣ�sendIdentify"+sendIdentify);
		}
	}
	/**����Ϣ�ӽ�����Ϣ�����ƶ�����ʱ��Ϣ����*/
	public static void expiredProcess(String sendIdentify) {
		if(FileHelper.moveFromReceivedToExpired(sendIdentify)){
			log.info("����Ϣ���볬ʱ��Ϣ���гɹ�:SendIdentify="+sendIdentify);
		}else{
			log.error("����Ϣ���볬ʱ��Ϣ����ʧ��:SendIdentify="+sendIdentify);
		}
	}
	/**����Ϣ�ӽ�����Ϣ�����ƶ�ɾ����Ϣ����*/
	public static void successProcess(String sendIdentify) {
		if(FileHelper.moveFromReceivedToDelete(sendIdentify)){
			log.info("����Ϣɾ���ɹ�:SendIdentify="+sendIdentify);
		}else{
			log.error("����Ϣɾ��ʧ��:SendIdentify="+sendIdentify);
		}
	}
}
