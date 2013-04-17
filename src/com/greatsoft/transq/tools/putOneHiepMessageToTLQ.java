package com.greatsoft.transq.tools;

import java.io.File;
import java.net.ConnectException;

import com.greatsoft.transq.core.Sender;
import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.MessageHelper;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.CommunicationException;
import com.greatsoft.transq.message.queue.TLQAddress;
import com.greatsoft.transq.message.queue.TLQConnector;
import com.greatsoft.transq.message.queue.TLQueue;
import com.greatsoft.transq.utils.ErrorCode;

/**
 * ��һ��HiepMessage�ŵ�qcu1�ı���TLQ��
 * @author WX
 */
public class putOneHiepMessageToTLQ {
	/**
	 * ��һ�����ɺõ�HiepMessage�ŵ����ص�ָ����TLQ��
	 * @param args
	 *            [0]: ��׼��TLQAddress�ַ��� ������tlq://localhost:10024/qcu1/localQueue
	 * @param args
	 *            [1]:һ��HiepMessage��ȫ·��
	 */
	public static void main(String args[]) {
		String tlqAddress = args[0];
		String hiepMessageFilePath = args[1];
		TLQAddress address = TLQAddress.parserOneAddress(tlqAddress);
		TLQConnector tlqConnector = new TLQConnector(address, null);
		try {
			tlqConnector.connect();
		} catch (ConnectException e1) {
			System.out.println("TLQ��������ʧ��");
			return;
		}
		if (!tlqConnector.isConnected()) {
			System.out.println("TLQ��������ʧ��");
			return;
		}
		Sender sender = tlqConnector.createSender();
		AbstractMessage message = MessageHelper.getMessage(hiepMessageFilePath);
		System.out.println("׼��������Ϣ��" + message);
		ResultImp putresult = null;
		try {
			putresult = ((TLQueue)sender).put(message, hiepMessageFilePath);
		} catch (CommunicationException e) {
			System.out.println("������Ϣʧ��\n");
			return;
		}
		if (putresult.getReturnCode() != ErrorCode.NO_ERROR) {
			System.out.println("������Ϣʧ��\n");
			return;
		}
		System.out.println("������Ϣ�ɹ�");
		String sendID = message.getEnvelope().getSendIdentify();
		tlqConnector.disConnect();
		File file = new File(hiepMessageFilePath);
		if (file.delete()) {
			System.out.println("�ļ�" + sendID + "ɾ���ɹ�");
		} else {
			if (file.delete()) {
				System.out.println("�ļ�" + sendID + "ɾ���ɹ�");
			} else {
				if (file.delete()) {
					System.out.println("�ļ�" + sendID + "ɾ���ɹ�");
				} else {
					System.out.println("�ļ�" + sendID + "ɾ��ʧ��");
				}
			}
		}
	}
}
