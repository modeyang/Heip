package com.greatsoft.transq.tools;

import java.nio.ByteBuffer;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.utils.ByteBufferHelper;

public class UnpackHeader {

	/**
	 * �⿪��Ϣ��ͷ���鿴���ݶԲ��� ��һ�������ǣ��Ѿ����л����ļ�ȫ·�� �ڶ��������ǣ������ȡmessage������������ļ���Ŀ¼��Ŀ¼��β����"/"
	 * ��ȡ���ļ���dataIdentify����
	 */
	public static void main(String[] args) {
		/** �Ѿ����л����ļ�ȫ·�� */
		String filePath = args[0];
		/** �����ȡmessage������ԭʼ���ļ���Ŀ¼,Ŀ¼��β����"/" */
		String storeFileDirectory = args[1];
		ByteBuffer byteBuffer = ByteBufferHelper.getByteBuffer(filePath);
		AbstractMessage message = MessageHelper.getMessage(byteBuffer);
		if (message == null) {
			System.out.println("�ļ������л�Ϊ��Ϣ�Ĺ����г���");
			System.exit(-1);
		}
		System.out.println(message.getEnvelope());
		String newFilePath = storeFileDirectory+ message.getEnvelope().getSourceDataName();
		if (ByteBufferHelper.putByteBuffer(message.getData(),newFilePath)) {
			System.out.println("��Ϣ�������л����ļ������гɹ���FilePath="+newFilePath);
		} else {
			System.out.println("��Ϣ�������л����ļ������г���FilePath="+newFilePath);
		}
	}
}
