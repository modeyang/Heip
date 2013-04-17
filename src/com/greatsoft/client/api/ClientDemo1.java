package com.greatsoft.client.api;

import java.io.File;
import java.net.ConnectException;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.exception.CommunicationException;
import com.greatsoft.transq.exception.HiepMessageException;

/**
 * �첽�ķ�ʽ�����ݽ���ϵͳ������Ϣ��demo
 * 
 * @author mojia xinw24@gmail.com
 */
public class ClientDemo1 {
	public static void main(String args[]) {
		/** ����һ��SERVER��ַ���ַ���������Clientʵ�� */
		Client client = new Client("tlq://111@localhost:10024/qcu1/localQueue");
		/** �������� */
		boolean flag = false;
		try {
			flag = client.connect();
		} catch (ConnectException e) {
			System.out.println("�ͻ��������쳣" + e.getMessage());
			System.exit(-1);
		}
		if (!flag) {
			System.out.println("�ͻ�������ʧ��");
			System.exit(-1);
		}
		/** ����һ��HIEP����ƽ̨��Ϣ */
		AbstractMessage message = client.createMessage("000", /** �������� */
		1, /** ���ȼ���9��8������1 */
		0, /** ѹ����ʽ */
		30, /** ���ʧЧʱ�䣨���ӣ� */
		"�人", /** ��Ϣ��Դ��ַ */
		"����@�人", /** ��Ϣ��Դ��ַ */
		"ũ��@����", /** ��Ϣ��Ŀ�ĵ�ַ */
		"lili.jpg", /** ���ݵ��ļ��� */
		new File("D:\\lili.jpg")/** ���ݵ��ļ�ȫ·�� */
		);
		if (null == message) {
			System.out.println("������Ϣʧ�ܣ�������Ϣ�����ļ�");
			System.exit(-1);
		}
		System.out.println("����һ����Ϣ�ɹ���" + message.toString());
		/** ������Ϣ */
		try {
			if (client.put(message) == null) {
				System.out.println("����ʧ��");
				return;
			}
		} catch (CommunicationException e) {
			System.out.println("��Ϣ�����쳣���ͻ��˷��������쳣" + e.getMessage());
			System.exit(-1);
		} catch (HiepMessageException e) {
			System.out.println("��Ϣ�����쳣����Ϣ�������쳣" + e.getMessage());
			System.exit(-1);
		}
		System.out.println("��Ϣ���ͳɹ�");
		/** �ر����� */
		client.close();
	}
}
