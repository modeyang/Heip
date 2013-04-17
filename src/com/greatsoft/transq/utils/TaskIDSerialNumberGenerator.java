package com.greatsoft.transq.utils;

/**多线程产生taskID*/
public class TaskIDSerialNumberGenerator {
	
	private static volatile int taskID = 0;

	public static int nextSerialNumber() {
		return taskID++;
	}
}
