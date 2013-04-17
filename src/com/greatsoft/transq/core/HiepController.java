package com.greatsoft.transq.core;

public interface HiepController {
	/** 停止数据交换系统 **/
	int stop(int timeout);

	/** 查看数据交换系统当前还活着的线程 **/
	String viewThreads();
}
