package com.greatsoft.transq.message.webservice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import com.greatsoft.transq.core.message.EnvelopeImp;

public class WSClientHelper {
	public static WSMessage getWSMessage() {
		EnvelopeImp envelope = new EnvelopeImp();
		envelope.setCreateDateTime("asdf");
		envelope.setDataCompressType(0);
		envelope.setDataIdentify("000");
		envelope.setDataType("000");
		envelope.setExpiredTime("798");
		envelope.setFromAddress("北京");
		envelope.setOriginalFileLength(798);
		envelope.setPriority(5);
		envelope.setSendIdentify("1234");
		envelope.setSourceAddress("北京");
		envelope.setSourceDataName("asdf");
		envelope.setTargetAddress("村卫@北京");
		envelope.setVersion("1.00");

		/**
		 * 构造一个内容随机的文件
		 */
		File file = new File("temp");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		PrintWriter printer = null;
		try {
			printer = new PrintWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Random random = new Random();
		for (int i = 0; i < 100; i++) {
			printer.write(random.nextInt(100));
		}
		printer.flush();
		printer.close();
		printer = null;

		return new WSMessage(new DataHandler(new FileDataSource(file)),
				envelope);
	}
}
