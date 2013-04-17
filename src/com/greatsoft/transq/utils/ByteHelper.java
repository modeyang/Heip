package com.greatsoft.transq.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteHelper {
	
	/** 将文件转成字节流 */
	public static byte[] getBytesFromFile(String filePath) {
		byte[] bytes = null;
		FileOutputStream fileOutputStream = null;
			File file = new File(filePath);
			int length = (int) file.length();
			bytes = new byte[length];
			try {
				fileOutputStream = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				System.out.println("找不到指定文件，文件名为：" + filePath + "\n"
						+ e.getMessage());
				return null;
			}
			try {
				fileOutputStream.write(bytes, 0, length);
			} catch (IOException e) {
				System.out.println("打开指定文件失败，文件名为：" + filePath + "\n"
						+ e.getMessage());
				return null;
			}
			try {
				fileOutputStream.flush();
			} catch (IOException e) {
				System.out.println("文件流更新失败，文件名为：" + filePath + "\n"
						+ e.getMessage());
			}
			finally{
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					System.out.println("文件关闭失败，文件名为：" + filePath + "\n"
							+ e.getMessage());
				}
			}		
		return bytes;
	}
}
