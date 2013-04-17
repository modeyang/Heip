package com.greatsoft.transq.tools;

import java.nio.ByteBuffer;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.utils.ByteBufferHelper;

public class UnpackHeader {

	/**
	 * 解开消息的头，查看数据对不对 第一个参数是：已经序列化的文件全路径 第二个参数是：存放提取message对象中起初的文件的目录，目录结尾包含"/"
	 * 提取的文件用dataIdentify命名
	 */
	public static void main(String[] args) {
		/** 已经序列化的文件全路径 */
		String filePath = args[0];
		/** 存放提取message对象中原始的文件的目录,目录结尾包含"/" */
		String storeFileDirectory = args[1];
		ByteBuffer byteBuffer = ByteBufferHelper.getByteBuffer(filePath);
		AbstractMessage message = MessageHelper.getMessage(byteBuffer);
		if (message == null) {
			System.out.println("文件反序列化为信息的过程中出错！");
			System.exit(-1);
		}
		System.out.println(message.getEnvelope());
		String newFilePath = storeFileDirectory+ message.getEnvelope().getSourceDataName();
		if (ByteBufferHelper.putByteBuffer(message.getData(),newFilePath)) {
			System.out.println("信息数据序列化到文件过程中成功！FilePath="+newFilePath);
		} else {
			System.out.println("信息数据序列化到文件过程中出错！FilePath="+newFilePath);
		}
	}
}
