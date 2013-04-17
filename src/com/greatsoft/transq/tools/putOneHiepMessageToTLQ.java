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
 * 将一条HiepMessage放到qcu1的本地TLQ中
 * @author WX
 */
public class putOneHiepMessageToTLQ {
	/**
	 * 将一条生成好的HiepMessage放到本地的指定的TLQ中
	 * @param args
	 *            [0]: 标准的TLQAddress字符串 范例：tlq://localhost:10024/qcu1/localQueue
	 * @param args
	 *            [1]:一条HiepMessage的全路径
	 */
	public static void main(String args[]) {
		String tlqAddress = args[0];
		String hiepMessageFilePath = args[1];
		TLQAddress address = TLQAddress.parserOneAddress(tlqAddress);
		TLQConnector tlqConnector = new TLQConnector(address, null);
		try {
			tlqConnector.connect();
		} catch (ConnectException e1) {
			System.out.println("TLQ建立连接失败");
			return;
		}
		if (!tlqConnector.isConnected()) {
			System.out.println("TLQ建立连接失败");
			return;
		}
		Sender sender = tlqConnector.createSender();
		AbstractMessage message = MessageHelper.getMessage(hiepMessageFilePath);
		System.out.println("准备发送消息：" + message);
		ResultImp putresult = null;
		try {
			putresult = ((TLQueue)sender).put(message, hiepMessageFilePath);
		} catch (CommunicationException e) {
			System.out.println("发送消息失败\n");
			return;
		}
		if (putresult.getReturnCode() != ErrorCode.NO_ERROR) {
			System.out.println("发送消息失败\n");
			return;
		}
		System.out.println("发送消息成功");
		String sendID = message.getEnvelope().getSendIdentify();
		tlqConnector.disConnect();
		File file = new File(hiepMessageFilePath);
		if (file.delete()) {
			System.out.println("文件" + sendID + "删除成功");
		} else {
			if (file.delete()) {
				System.out.println("文件" + sendID + "删除成功");
			} else {
				if (file.delete()) {
					System.out.println("文件" + sendID + "删除成功");
				} else {
					System.out.println("文件" + sendID + "删除失败");
				}
			}
		}
	}
}
