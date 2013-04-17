package com.greatsoft.transq.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

public class ByteBufferHelper {
	private static Logger log = Logger.getLogger(ByteBufferHelper.class);
	/** 
	 * ��ָ���ļ��л�ȡ�ļ���ByteBuffer
	 * @param filePath ָ���ļ���·��
	 * @return �ɹ���ȡ�ļ����򷵻�ByteBuffer��ʧ���򷵻�null
	 */
	public static ByteBuffer getByteBuffer(String filePath) {
		File file = new File(filePath);
		return getByteBuffer(file);
	}
	/** 
	 * ��ָ���ļ��л�ȡ�ļ���ByteBuffer
	 * @param file ָ�����ļ�
	 * @return �ɹ���ȡ�ļ����򷵻�ByteBuffer��ʧ���򷵻�null
	 */
	public static ByteBuffer getByteBuffer(File file) {
		String filePath=file.getPath();
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			log.error("�Ҳ���ָ���ļ����ļ���Ϊ��" + filePath + e.getMessage());
			return null;
		}
		FileChannel fileChannel = fileInputStream.getChannel();
		MappedByteBuffer mappedByteBuffer = null;
		try {
			mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,0, file.length());
		} catch (IOException e) {
			log.error("��ָ���ļ�ʧ�ܣ��ļ���Ϊ��"+filePath+ e.getMessage());
			return null;
		} finally {
			try {
				fileChannel.close();
				fileChannel=null;
				fileInputStream.close();
				fileInputStream=null;
			} catch (IOException e) {
				log.error("�ļ��ر�ʧ�ܣ��ļ���Ϊ��"+filePath+ e.getMessage());
			}
		}
		return mappedByteBuffer;
	}

	/**
	 * ��ByteBuffer��д��һ��ָ�����ļ���
	 * */
	public static boolean putByteBuffer(ByteBuffer byteBuffer,String filePath) {
		if (byteBuffer.capacity() == 0) {
			log.error("��������byteBuffer���ĳ���Ϊ0");
			return false;
		}
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(filePath, "rw");
		} catch (FileNotFoundException e) {
			log.error("�Ҳ���ָ���ļ���filePath=" + filePath + e.getMessage());
			return false;
		}
		FileChannel fileChannel = file.getChannel();
		try {
			int fileLength = fileChannel.write(byteBuffer);
			if (fileLength == 0) {
				log.error("д�ļ�ʧ�ܣ�filePath="+filePath);
				try {
					fileChannel.close();
					fileChannel = null;
					file.close();
					file = null;
				} catch (IOException e) {
					log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e.getMessage());
				}
				return false;
			}
		} catch (IOException e) {
			log.error("д�ļ�ʧ�ܣ�filePath="+filePath+ e.getMessage());
			return false;
		} finally {
			try {
				fileChannel.close();
				fileChannel = null;
				file.close();
				file = null;
			} catch (IOException e) {
				log.error("�ļ��ر�ʧ�ܣ�filePath="+filePath+ e.getMessage());
				return false;
			}
		}
		return true;
	}

	public static String toString(ByteBuffer byteBuffer) {
		byteBuffer.flip();
		byte[] bytes = new byte[byteBuffer.capacity()];
		byteBuffer.get(bytes, 0, byteBuffer.capacity());
		return new String(bytes, 0, bytes.length);
	}
}
