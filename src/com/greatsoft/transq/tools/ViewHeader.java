package com.greatsoft.transq.tools;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.MessageHelper;

/**
 * ��һ���Ѿ����л����ļ�����ȡmessage�����ͷ��Ϣ����ӡ��Ļ��
 * ��һ������(args[0])�ǣ�һ���Ѿ����л����ļ�ȫ·�� 
 * */
public class ViewHeader {
	public static void main(String args[]) {
		AbstractMessage message = MessageHelper.getMessage(args[0]);
		if(message==null){
			System.out.println("�ļ������л�Ϊ��Ϣ�Ĺ����г���");
			System.exit(-1);
		}
		System.out.println(message.getEnvelope());
	}
}
