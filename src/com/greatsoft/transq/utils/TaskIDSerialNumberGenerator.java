package com.greatsoft.transq.utils;

/**���̲߳���taskID*/
public class TaskIDSerialNumberGenerator {
	
	private static volatile int taskID = 0;

	public static int nextSerialNumber() {
		return taskID++;
	}
}
