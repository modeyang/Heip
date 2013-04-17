package com.greatsoft.client.api;

import java.io.File;
import java.net.ConnectException;

import com.greatsoft.transq.core.message.AbstractMessage;
import com.greatsoft.transq.core.message.ResultImp;
import com.greatsoft.transq.exception.CommunicationException;
import com.greatsoft.transq.exception.HiepMessageException;

/**
 * 数据交换系统同步方式demo
 * 
 * @author xinw24@gmail.com
 */
public class ClientDemo2 {

	public static void main(String[] args) {

		/** 传进一个SERVER地址的字符串，创建Client实例 */
		Client client = new Client(
				"http://localhost:9000/transq/services/centerService");
		/** 建立连接 */
		boolean flag = false;
		try {
			flag = client.connect();
		} catch (ConnectException e) {
			System.out.println("客户端连接异常" + e.getMessage());
			System.exit(-1);
		}
		if (!flag) {
			System.out.println("客户端连接失败");
			System.exit(-1);
		}
		/** 构造一条HIEP交换平台信息 */

		AbstractMessage message = client.createMessage("000",
		/** 数据类型 */
		1,
		/** 优先级，9》8》。。1 */
		0,
		/** 压缩方式 */
		30,
		/** 相对失效时间（分钟） */
		"武汉",
		/** 消息的源地址 */
		"村卫@武汉",
		/** 消息的源地址 */
		"村卫@北京",
		/** 消息的目的地址 */
		"lili.jpg",
		/** 数据的文件名 */
		new File("D:/lili.jpg")
		/** 数据的文件全路径 */
		);

		if (null == message) {
			System.out.println("构造消息失败，请检测消息数据文件");
			System.exit(-1);
		}
		System.out.println("构造一条消息成功：" + message.toString());
		/** 发送信息 */
		ResultImp result = null;
		try {
			result = client.put(message);
			if (result == null) {
				System.out.println("调用失败");
			} else if (result.getReturnMessage() == null) {
				System.out.println("返回消息为null");
			} else {
				System.out.println("调用成功" + result.getReturnCode() + "\t"
						+ result.getReturnInfo());
			}
			
		} catch (CommunicationException e) {
			System.out.println("消息发送异常，客户端通讯异常" + e.getMessage());
			System.exit(-1);
		} catch (HiepMessageException e) {
			System.out.println("消息发送异常，消息的内容异常" + e.getMessage());
			System.exit(-1);
		}
		System.out.println("消息发送成功");
		/** 关闭连接 */
		client.close();
	}

}
