package com.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import com.greatsoft.transq.utils.FileHelper;

public class TestFile {

	public static void main(String[] args) {

		if(FileHelper.moveFile("C:/Users/mojia/Desktop/basic.txt","C:/Users/mojia/Desktop/test/test.txt")){
			System.out.println("ok");
		}else{
			System.out.println("no");
		}
			
	}
	
	/**
	 * 将文件oldFilePath移动到新位置newFilePath
	 * 
	 * @param oldFilePath
	 * @param newFilePath
	 */
	public static boolean moveFile(String oldFilePath, String newFilePath) {
		File oldFile = new File(oldFilePath);
		File newFile = new File(newFilePath);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File("tmp"), false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		FileChannel fileChannel = out.getChannel();
		FileLock fileLock = null;
		while (true) {
			try {
				fileLock = fileChannel.tryLock();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("移到文件获取锁失败");
			}
			if (fileLock != null) {
				break;
			} else {
				System.out.println("有其他线程在操作这个文件");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		boolean result = oldFile.renameTo(newFile);
		try {
			fileLock.release();
			fileChannel.close();
			out.close();
			out = null;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("移动文件释放锁失败");
		}

		return result;
	}

}
