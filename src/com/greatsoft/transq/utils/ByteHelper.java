package com.greatsoft.transq.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ByteHelper {
	
	/** ���ļ�ת���ֽ��� */
	public static byte[] getBytesFromFile(String filePath) {
		byte[] bytes = null;
		FileOutputStream fileOutputStream = null;
			File file = new File(filePath);
			int length = (int) file.length();
			bytes = new byte[length];
			try {
				fileOutputStream = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				System.out.println("�Ҳ���ָ���ļ����ļ���Ϊ��" + filePath + "\n"
						+ e.getMessage());
				return null;
			}
			try {
				fileOutputStream.write(bytes, 0, length);
			} catch (IOException e) {
				System.out.println("��ָ���ļ�ʧ�ܣ��ļ���Ϊ��" + filePath + "\n"
						+ e.getMessage());
				return null;
			}
			try {
				fileOutputStream.flush();
			} catch (IOException e) {
				System.out.println("�ļ�������ʧ�ܣ��ļ���Ϊ��" + filePath + "\n"
						+ e.getMessage());
			}
			finally{
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					System.out.println("�ļ��ر�ʧ�ܣ��ļ���Ϊ��" + filePath + "\n"
							+ e.getMessage());
				}
			}		
		return bytes;
	}
}
