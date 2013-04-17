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
	 * 从指定文件中获取文件流ByteBuffer
	 * @param filePath 指定文件的路径
	 * @return 成功获取文件流则返回ByteBuffer，失败则返回null
	 */
	public static ByteBuffer getByteBuffer(String filePath) {
		File file = new File(filePath);
		return getByteBuffer(file);
	}
	/** 
	 * 从指定文件中获取文件流ByteBuffer
	 * @param file 指定的文件
	 * @return 成功获取文件流则返回ByteBuffer，失败则返回null
	 */
	public static ByteBuffer getByteBuffer(File file) {
		String filePath=file.getPath();
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			log.error("找不到指定文件，文件名为：" + filePath + e.getMessage());
			return null;
		}
		FileChannel fileChannel = fileInputStream.getChannel();
		MappedByteBuffer mappedByteBuffer = null;
		try {
			mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY,0, file.length());
		} catch (IOException e) {
			log.error("打开指定文件失败，文件名为："+filePath+ e.getMessage());
			return null;
		} finally {
			try {
				fileChannel.close();
				fileChannel=null;
				fileInputStream.close();
				fileInputStream=null;
			} catch (IOException e) {
				log.error("文件关闭失败，文件名为："+filePath+ e.getMessage());
			}
		}
		return mappedByteBuffer;
	}

	/**
	 * 将ByteBuffer流写到一个指定的文件中
	 * */
	public static boolean putByteBuffer(ByteBuffer byteBuffer,String filePath) {
		if (byteBuffer.capacity() == 0) {
			log.error("待操作的byteBuffer流的长度为0");
			return false;
		}
		RandomAccessFile file = null;
		try {
			file = new RandomAccessFile(filePath, "rw");
		} catch (FileNotFoundException e) {
			log.error("找不到指定文件：filePath=" + filePath + e.getMessage());
			return false;
		}
		FileChannel fileChannel = file.getChannel();
		try {
			int fileLength = fileChannel.write(byteBuffer);
			if (fileLength == 0) {
				log.error("写文件失败：filePath="+filePath);
				try {
					fileChannel.close();
					fileChannel = null;
					file.close();
					file = null;
				} catch (IOException e) {
					log.error("文件关闭失败：filePath="+filePath+ e.getMessage());
				}
				return false;
			}
		} catch (IOException e) {
			log.error("写文件失败：filePath="+filePath+ e.getMessage());
			return false;
		} finally {
			try {
				fileChannel.close();
				fileChannel = null;
				file.close();
				file = null;
			} catch (IOException e) {
				log.error("文件关闭失败：filePath="+filePath+ e.getMessage());
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
