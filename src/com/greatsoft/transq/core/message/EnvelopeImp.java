package com.greatsoft.transq.core.message;

import java.util.Calendar;
import java.util.UUID;

import com.greatsoft.transq.utils.CalenderAndString;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;

/**
 * 消息信封接口的基本实现
 * 
 * @author
 */
public class EnvelopeImp implements Envelope {
	/**消息的版本*/
	private String version;
	/**消息的数据的唯一标识*/
	private String dataIdentify;
	/**消息的发送标识*/
	private String sendIdentify;
	/**消息的数据类型*/
	private String dataType;
	/**消息发送的优先级*/
	private int priority;
	/**消息的压缩类型*/
	private int dataCompressType;
	/**消息的创建时间*/
	private String createDateTime;
	/**消息的失效时间*/
	private String expiredTime;
	/**消息的源地址*/
	private String sourceAddress;
	/**消息的本次交换地址*/
	private String fromAddress;
	/**消息的目的地址*/
	private String targetAddress;
	/**消息的数据的原始文件长度*/
	private long originalFileLength;
	/**消息的数据的名称*/
	private String sourceDataName;
	
	public EnvelopeImp() {

	}

	/**
	 * 版本是常量，createDateTime之类的有默认的生成规则，可以不用出现在参数中
	 */
	public EnvelopeImp(String version, String dataIdentify,
			String sendIdentify, String dataType, int priority,
			int dataCompressType, String createDateTime, String expiredTime,
			String sourceAddress, String fromAddress, String targetAddress,
			String sourceDataName, long originalFileLength) {
		super();
		this.version = version;
		this.dataIdentify = dataIdentify;
		this.sendIdentify = sendIdentify;
		this.dataType = dataType;
		this.priority = priority;
		this.dataCompressType = dataCompressType;
		this.createDateTime = createDateTime;
		this.expiredTime = expiredTime;
		this.sourceAddress = sourceAddress;
		this.fromAddress = fromAddress;
		this.targetAddress = targetAddress;
		this.sourceDataName = sourceDataName;
		this.originalFileLength = originalFileLength;
	}

	/**
	 * 信封头第一次封装时， 版本号version采用本HIEP端的默认版本， 生成时间createDateTime为信封头产生时间
	 * 失效时间expiredTime为createDateTime加上相对失效时间relativeExpiredTime，传输过程中不变
	 * 发送标识sendIdentify为HIEP端本地地址+信封头产生时间+随机16位UUID码
	 * 本次发送地的交换地址fromAddress为HIEP端本地地址
	 * */
	public EnvelopeImp(String dataIdentify, String dataType, int priority,
			int dataCompressType, int relativeExpiredTime,
			String sourceAddress, String fromAddress,String targetAddress, String sourceDataName) {
		this.version = ConstantValue.MESSAGE_VERSION;
		this.dataIdentify = dataIdentify;
		Calendar now = Calendar.getInstance();
		this.createDateTime = CalenderAndString.calendarToString(now);
		now.add(Calendar.MINUTE, relativeExpiredTime);
		this.expiredTime = CalenderAndString.calendarToString(now);
		this.dataType = dataType;
		this.priority = priority;
		this.dataCompressType = dataCompressType;
		this.sourceAddress = sourceAddress;
		this.fromAddress =fromAddress;
		this.targetAddress = targetAddress;
		this.sourceDataName = sourceDataName;
		this.originalFileLength = 0;
		this.sendIdentify = this.createDateTime+ConstantValue.UUID_SEPERATER+newUUIDstring();
	}

	/**
	 * 依照旧信封和新的目的地址生成新信封。
	 * */
	public EnvelopeImp(Envelope oldEnvelope, String targetAddress) {
		this.version = ConstantValue.MESSAGE_VERSION;
		this.dataIdentify = oldEnvelope.getDataIdentify();
		this.dataType = oldEnvelope.getDataType();
		this.priority = oldEnvelope.getPriority();
		this.dataCompressType = oldEnvelope.getDataCompressType();
		this.createDateTime = CalenderAndString.calendarToString(Calendar
				.getInstance());
		this.expiredTime = oldEnvelope.getExpiredTime();
		this.sourceAddress = oldEnvelope.getSourceAddress();
		this.fromAddress = Config.LOCAL_ADDRESS;
		this.targetAddress = targetAddress;
		this.sourceDataName = oldEnvelope.getSourceDataName();
		this.originalFileLength = oldEnvelope.getOriginalFileLength();
		this.sendIdentify = this.createDateTime+ConstantValue.UUID_SEPERATER+newUUIDstring();
	}

	/**
	 * 依照旧信封生成新信封。源地址和目的地址字段对换
	 * */
	public EnvelopeImp(Envelope oldEnvelope) {
		this.version = ConstantValue.MESSAGE_VERSION;
		this.dataIdentify = oldEnvelope.getDataIdentify();
		this.dataType = oldEnvelope.getDataType();
		this.priority = oldEnvelope.getPriority();
		this.dataCompressType = oldEnvelope.getDataCompressType();
		Calendar now = Calendar.getInstance();
		this.createDateTime = CalenderAndString.calendarToString(now);
		now.add(Calendar.MINUTE, Config.RELATIVE_EXPIRED_TIME);
		this.expiredTime = CalenderAndString.calendarToString(now);
		this.sourceAddress = oldEnvelope.getTargetAddress();
		this.fromAddress = Config.LOCAL_ADDRESS;
		this.targetAddress = oldEnvelope.getSourceAddress();
		this.sourceDataName = oldEnvelope.getSourceDataName();
		this.originalFileLength = oldEnvelope.getOriginalFileLength();
		this.sendIdentify = this.createDateTime+ConstantValue.UUID_SEPERATER+newUUIDstring();
	}

	/**
	 * 优先级
	 */
	public final int getPriority() {
		return priority;
	}

	/**
	 * 优先级
	 * 
	 * @param newVal
	 */
	public final void setPriority(int newVal) {
		priority = newVal;
	}

	/**
	 * 发送标识。
	 */
	public final String getSendIdentify() {
		return sendIdentify;
	}

	/**
	 * 发送标识。
	 * 
	 * @param newVal
	 */
	public final void setSendIdentify(String newVal) {
		sendIdentify = newVal;
	}

	/**
	 * 数据结构版本号
	 */
	public final String getVersion() {
		return version;
	}

	/**
	 * 数据结构版本号
	 * 
	 * @param newVal
	 */
	public final void setVersion(String newVal) {
		version = newVal;
	}

	/**
	 * 数据标识用跟踪数据在交换平台中传输的情况
	 */
	public final String getDataIdentify() {
		return dataIdentify;
	}

	/**
	 * 数据标识用跟踪数据在交换平台中传输的情况
	 * 
	 * @param newVal
	 */
	public final void setDataIdentify(String newVal) {
		dataIdentify = newVal;
	}

	/**
	 * 压缩方式:none=0；zip=1；
	 */
	public final int getDataCompressType() {
		return dataCompressType;
	}

	/**
	 * 压缩方式:none=0；zip=1；
	 * 
	 * @param newVal
	 */
	public final void setDataCompressType(int newVal) {
		dataCompressType = newVal;
	}

	/**
	 * 创建时间
	 */
	public final String getCreateDateTime() {
		return createDateTime;
	}

	/**
	 * 创建时间
	 * 
	 * @param newVal
	 */
	public final void setCreateDateTime(String newVal) {
		createDateTime = newVal;
	}

	/**
	 * 失效时间,绝对日期时间.
	 */
	public final String getExpiredTime() {
		return expiredTime;
	}

	/**
	 * 失效时间,绝对日期时间.
	 * 
	 * @param newVal
	 */
	public final void setExpiredTime(String newVal) {
		expiredTime = newVal;
	}

	/**
	 * 源发端平台填写，传输过程中不改变
	 */
	public final String getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * 源发端平台填写，传输过程中不改变
	 * 
	 * @param newVal
	 */
	public final void setSourceAddress(String newVal) {
		sourceAddress = newVal;
	}

	/**
	 * 本次发送地的交换地址
	 */
	public final String getFromAddress() {
		return fromAddress;
	}

	/**
	 * 本次发送地的交换地址
	 * 
	 * @param newVal
	 */
	public final void setFromAddress(String newVal) {
		fromAddress = newVal;
	}

	/**
	 * 本次目的地地址，本次报文的下一个直接目的地。
	 */
	public final String getTargetAddress() {
		return targetAddress;
	}

	/**
	 * 本次目的地地址，本次报文的下一个直接目的地。
	 * 
	 * @param newVal
	 */
	public final void setTargetAddress(String newVal) {
		targetAddress = newVal;
	}

	/**
	 * 业务数据类别
	 */
	@Override
	public final String getDataType() {
		return dataType;
	}

	/**
	 * 业务数据类别
	 */
	@Override
	public final void setDataType(String val) {
		this.dataType = val;
	}

	@Override
	public String getSourceDataName() {
		return this.sourceDataName;
	}

	@Override
	public void setSourceDataName(String sourcedataName) {
		this.sourceDataName = sourcedataName;
	}

	@Override
	public long getOriginalFileLength() {
		return this.originalFileLength;
	}

	@Override
	public void setOriginalFileLength(long fileLength) {
		this.originalFileLength = fileLength;
	}

	@Override
	public String toString() {
		return "EnvelopeImp [\nversion=" + version + "\nsendIdentify="
				+ sendIdentify + "\ndataIdentify=" + dataIdentify
				+ "\ndataType=" + dataType + "\ndataCompressType="
				+ dataCompressType + "\npriority=" + priority
				+ "\ncreateDateTime=" + createDateTime + "\nexpiredTime="
				+ expiredTime + "\nfromAddress=" + fromAddress
				+ "\nsourceAddress=" + sourceAddress + "\ntargetAddress="
				+ targetAddress + "\nsourceDataName=" + sourceDataName
				+ "\noriginalFileLength=" + originalFileLength + "\n]";
	}
	
	public static String newUUIDstring(){
		return UUID.randomUUID().toString();
	}
}