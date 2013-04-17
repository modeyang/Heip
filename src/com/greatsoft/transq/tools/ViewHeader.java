package com.greatsoft.transq.tools;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.MessageHelper;

/**
 * 从一个已经序列化的文件中提取message对象的头信息，打印屏幕。
 * 第一个参数(args[0])是：一个已经序列化的文件全路径 
 * */
public class ViewHeader {
	public static void main(String args[]) {
		AbstractMessage message = MessageHelper.getMessage(args[0]);
		if(message==null){
			System.out.println("文件反序列化为信息的过程中出错！");
			System.exit(-1);
		}
		System.out.println(message.getEnvelope());
	}
}
