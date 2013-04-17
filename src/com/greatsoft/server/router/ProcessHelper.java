package com.greatsoft.server.router;

import org.apache.log4j.Logger;
import com.greatsoft.transq.utils.FileHelper;

public class ProcessHelper {
	private static Logger log = Logger.getLogger(RouterServer.class);
	
	/**将消息从接受信息队列移动到错误信息队列*/
	public static void failedProcess(String sendIdentify) {
		if (FileHelper.moveFromReceivedToError(sendIdentify)) {
			log.info("将任务记录对应的信息文件放入错误信息队列成功：sendIdentify"+sendIdentify);
		} else {
			log.error("将任务记录对应的信息文件放入错误信息队列失败：sendIdentify"+sendIdentify);
		}
	}
	/**将消息从接受信息队列移动到超时信息队列*/
	public static void expiredProcess(String sendIdentify) {
		if(FileHelper.moveFromReceivedToExpired(sendIdentify)){
			log.info("将消息放入超时消息队列成功:SendIdentify="+sendIdentify);
		}else{
			log.error("将消息放入超时消息队列失败:SendIdentify="+sendIdentify);
		}
	}
	/**将消息从接受信息队列移动删除信息队列*/
	public static void successProcess(String sendIdentify) {
		if(FileHelper.moveFromReceivedToDelete(sendIdentify)){
			log.info("将消息删除成功:SendIdentify="+sendIdentify);
		}else{
			log.error("将消息删除失败:SendIdentify="+sendIdentify);
		}
	}
}
