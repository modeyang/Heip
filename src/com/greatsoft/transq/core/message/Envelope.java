package com.greatsoft.transq.core.message;


/**
 * 消息信封接口
 * @author RAY
 *
 */
public interface Envelope {
	/**
	 * 数据结构版本号
	 */
	public String getVersion();

	/**
	 * 数据结构版本号
	 * 
	 * @param newVal
	 */
	public void setVersion(String newVal);


	/**
	 * 压缩方式:none=0；zip=1；
	 */
	public int getDataCompressType();

	/**
	 * 压缩方式:none=0；zip=1；
	 * 
	 * @param newVal
	 */
	public void setDataCompressType(int newVal);

	/**
	 * 创建时间
	 */
	public String getCreateDateTime();

	/**
	 * 创建时间
	 * 
	 * @param newVal
	 */
	public void setCreateDateTime(String newVal);

	/**
	 * 源发端平台填写，传输过程中不改变
	 */
	public String getSourceAddress();

	/**
	 * 源发端平台填写，传输过程中不改变
	 * 
	 * @param newVal
	 */
	public void setSourceAddress(String newVal);

	/**
	 * 本次发送地的交换地址
	 */
	public String getFromAddress();

	/**
	 * 本次发送地的交换地址
	 * 
	 * @param newVal
	 */
	public void setFromAddress(String newVal);

	/**
	 * 本次目的地地址，本次报文的下一个直接目的地。
	 */
	public String getTargetAddress();

	/**
	 * 本次目的地地址，本次报文的下一个直接目的地。
	 * 
	 * @param newVal
	 */
	public void setTargetAddress(String newVal);


	/**
	 * 数据类型
	 */
	public String getDataType();

	/**
	 * 数据类型
	 * 
	 * @param newVal
	 */
	public void setDataType(String val);

	/**
	 * 失效时间,绝对日期时间.
	 */
	public String getExpiredTime();

	/**
	 * 失效时间,绝对日期时间.
	 * 
	 * @param newVal
	 */
	public void setExpiredTime(String newVal);

	/**
	 * 优先级
	 */
	public int getPriority();

	/**
	 * 优先级
	 * 
	 * @param newVal
	 */
	public void setPriority(int newVal);

	/**
	 * 发送标识。
	 */
	public String getSendIdentify();

	/**
	 * 发送标识。
	 * 
	 * @param newVal
	 */
	public void setSendIdentify(String newVal);

	/**
	 * 数据标识用跟踪数据在交换平台中传输的情况
	 */
	public String getDataIdentify();

	/**
	 * 数据标识用跟踪数据在交换平台中传输的情况
	 * 
	 * @param newVal
	 */
	public void setDataIdentify(String newVal);
	
	/**
	 * 原始文件名称
	 * @return 原始文件名称
	 */
	public String getSourceDataName();
	/**
	 * 原始文件名称
	 * @param dataName 原始文件名称
	 */
	public void setSourceDataName(String sourcedataName);
	
	/**原始文件长度
	 * */
	public long getOriginalFileLength();
	
	public void setOriginalFileLength(long fileLength);
	
}
