package com.greatsoft.transq.core.task;


/**
 * 任务记录
 */
public interface Task {

	/**
	 * 任务ID,任务的唯一标识号
	 * 
	 * @return 任务ID
	 */
	public String getId();

	/**
	 * 任务ID,任务的唯一标识号
	 * 
	 * @param id
	 *            任务ID
	 */
	public void setId(String id);

	/**
	 * 任务优先级 0-9
	 * 
	 * @return 任务优先级
	 */
	public int getPriority();

	/**
	 * 任务优先级 0-9
	 * 
	 * @param priority
	 *            任务优先级
	 */
	public void setPriority(int priority);

	/**
	 * 任务进队列时间
	 * 
	 * @return 任务进队列时间
	 */
	public String getEnterTime();

	/**
	 * 任务进队列时间
	 * 
	 * @param enterTime
	 *            任务进队列时间
	 */
	public void setEnterTime(String enterTime);

	/**
	 * 失效时间
	 * 
	 * @return 失效时间
	 */
	public String getExpiredTime();

	/**
	 * 失效时间
	 * 
	 * @param expiredTime
	 *            失效时间
	 */
	public void setExpiredTime(String expiredTime);

	/**
	 * 任务状态
	 * 
	 * @return 任务状态
	 */
	public int getStatus();

	/**
	 * 任务状态
	 * 
	 * @param status
	 *            任务状态
	 */
	public void setStatus(int status);

	/**
	 * 数据类别
	 * 
	 * @return 数据类别
	 */
	public String getDataType();

	/**
	 * 数据类别
	 * 
	 * @param dataClass
	 *            数据类别
	 */
	public void setDataType(String dataType);

	/**
	 * 下一站地址
	 * 
	 * @return 下一站地址
	 */
	public String getToAddress();

	/**
	 * 下一站地址
	 * 
	 * @param toAddress
	 *            下一站地址
	 */
	public void setToAddress(String toAddress);

	/**
	 * 数据显示名称
	 * 
	 * @return 数据显示名称
	 */
	public String getSourceDataName();

	/**
	 * 文件名
	 * 
	 * @param dataName
	 *            数据显示名称
	 */
	public void setSourceDataName(String sourcedataName);

	/**
	 * 数据唯一标识
	 * 
	 * @return 数据唯一标识
	 */
	public String getDataIdentfy();

	/**
	 * 文件唯一标识
	 * 
	 * @param dataIdentfy
	 *            数据唯一标识
	 */
	public void setDataIdentfy(String dataIdentfy);

	/**
	 * 落地文件名
	 * */
	public void setSendIdentify(String sendId);

	public String getSendIdentify();
}
