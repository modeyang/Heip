package com.greatsoft.transq.core.message;

import java.util.Calendar;
import java.util.UUID;

import com.greatsoft.transq.utils.CalenderAndString;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;

/**
 * ��Ϣ�ŷ�ӿڵĻ���ʵ��
 * 
 * @author
 */
public class EnvelopeImp implements Envelope {
	/**��Ϣ�İ汾*/
	private String version;
	/**��Ϣ�����ݵ�Ψһ��ʶ*/
	private String dataIdentify;
	/**��Ϣ�ķ��ͱ�ʶ*/
	private String sendIdentify;
	/**��Ϣ����������*/
	private String dataType;
	/**��Ϣ���͵����ȼ�*/
	private int priority;
	/**��Ϣ��ѹ������*/
	private int dataCompressType;
	/**��Ϣ�Ĵ���ʱ��*/
	private String createDateTime;
	/**��Ϣ��ʧЧʱ��*/
	private String expiredTime;
	/**��Ϣ��Դ��ַ*/
	private String sourceAddress;
	/**��Ϣ�ı��ν�����ַ*/
	private String fromAddress;
	/**��Ϣ��Ŀ�ĵ�ַ*/
	private String targetAddress;
	/**��Ϣ�����ݵ�ԭʼ�ļ�����*/
	private long originalFileLength;
	/**��Ϣ�����ݵ�����*/
	private String sourceDataName;
	
	public EnvelopeImp() {

	}

	/**
	 * �汾�ǳ�����createDateTime֮�����Ĭ�ϵ����ɹ��򣬿��Բ��ó����ڲ�����
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
	 * �ŷ�ͷ��һ�η�װʱ�� �汾��version���ñ�HIEP�˵�Ĭ�ϰ汾�� ����ʱ��createDateTimeΪ�ŷ�ͷ����ʱ��
	 * ʧЧʱ��expiredTimeΪcreateDateTime�������ʧЧʱ��relativeExpiredTime����������в���
	 * ���ͱ�ʶsendIdentifyΪHIEP�˱��ص�ַ+�ŷ�ͷ����ʱ��+���16λUUID��
	 * ���η��͵صĽ�����ַfromAddressΪHIEP�˱��ص�ַ
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
	 * ���վ��ŷ���µ�Ŀ�ĵ�ַ�������ŷ⡣
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
	 * ���վ��ŷ��������ŷ⡣Դ��ַ��Ŀ�ĵ�ַ�ֶζԻ�
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
	 * ���ȼ�
	 */
	public final int getPriority() {
		return priority;
	}

	/**
	 * ���ȼ�
	 * 
	 * @param newVal
	 */
	public final void setPriority(int newVal) {
		priority = newVal;
	}

	/**
	 * ���ͱ�ʶ��
	 */
	public final String getSendIdentify() {
		return sendIdentify;
	}

	/**
	 * ���ͱ�ʶ��
	 * 
	 * @param newVal
	 */
	public final void setSendIdentify(String newVal) {
		sendIdentify = newVal;
	}

	/**
	 * ���ݽṹ�汾��
	 */
	public final String getVersion() {
		return version;
	}

	/**
	 * ���ݽṹ�汾��
	 * 
	 * @param newVal
	 */
	public final void setVersion(String newVal) {
		version = newVal;
	}

	/**
	 * ���ݱ�ʶ�ø��������ڽ���ƽ̨�д�������
	 */
	public final String getDataIdentify() {
		return dataIdentify;
	}

	/**
	 * ���ݱ�ʶ�ø��������ڽ���ƽ̨�д�������
	 * 
	 * @param newVal
	 */
	public final void setDataIdentify(String newVal) {
		dataIdentify = newVal;
	}

	/**
	 * ѹ����ʽ:none=0��zip=1��
	 */
	public final int getDataCompressType() {
		return dataCompressType;
	}

	/**
	 * ѹ����ʽ:none=0��zip=1��
	 * 
	 * @param newVal
	 */
	public final void setDataCompressType(int newVal) {
		dataCompressType = newVal;
	}

	/**
	 * ����ʱ��
	 */
	public final String getCreateDateTime() {
		return createDateTime;
	}

	/**
	 * ����ʱ��
	 * 
	 * @param newVal
	 */
	public final void setCreateDateTime(String newVal) {
		createDateTime = newVal;
	}

	/**
	 * ʧЧʱ��,��������ʱ��.
	 */
	public final String getExpiredTime() {
		return expiredTime;
	}

	/**
	 * ʧЧʱ��,��������ʱ��.
	 * 
	 * @param newVal
	 */
	public final void setExpiredTime(String newVal) {
		expiredTime = newVal;
	}

	/**
	 * Դ����ƽ̨��д����������в��ı�
	 */
	public final String getSourceAddress() {
		return sourceAddress;
	}

	/**
	 * Դ����ƽ̨��д����������в��ı�
	 * 
	 * @param newVal
	 */
	public final void setSourceAddress(String newVal) {
		sourceAddress = newVal;
	}

	/**
	 * ���η��͵صĽ�����ַ
	 */
	public final String getFromAddress() {
		return fromAddress;
	}

	/**
	 * ���η��͵صĽ�����ַ
	 * 
	 * @param newVal
	 */
	public final void setFromAddress(String newVal) {
		fromAddress = newVal;
	}

	/**
	 * ����Ŀ�ĵص�ַ�����α��ĵ���һ��ֱ��Ŀ�ĵء�
	 */
	public final String getTargetAddress() {
		return targetAddress;
	}

	/**
	 * ����Ŀ�ĵص�ַ�����α��ĵ���һ��ֱ��Ŀ�ĵء�
	 * 
	 * @param newVal
	 */
	public final void setTargetAddress(String newVal) {
		targetAddress = newVal;
	}

	/**
	 * ҵ���������
	 */
	@Override
	public final String getDataType() {
		return dataType;
	}

	/**
	 * ҵ���������
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