package com.greatsoft.transq.core.message;


/**
 * ��Ϣ�ŷ�ӿ�
 * @author RAY
 *
 */
public interface Envelope {
	/**
	 * ���ݽṹ�汾��
	 */
	public String getVersion();

	/**
	 * ���ݽṹ�汾��
	 * 
	 * @param newVal
	 */
	public void setVersion(String newVal);


	/**
	 * ѹ����ʽ:none=0��zip=1��
	 */
	public int getDataCompressType();

	/**
	 * ѹ����ʽ:none=0��zip=1��
	 * 
	 * @param newVal
	 */
	public void setDataCompressType(int newVal);

	/**
	 * ����ʱ��
	 */
	public String getCreateDateTime();

	/**
	 * ����ʱ��
	 * 
	 * @param newVal
	 */
	public void setCreateDateTime(String newVal);

	/**
	 * Դ����ƽ̨��д����������в��ı�
	 */
	public String getSourceAddress();

	/**
	 * Դ����ƽ̨��д����������в��ı�
	 * 
	 * @param newVal
	 */
	public void setSourceAddress(String newVal);

	/**
	 * ���η��͵صĽ�����ַ
	 */
	public String getFromAddress();

	/**
	 * ���η��͵صĽ�����ַ
	 * 
	 * @param newVal
	 */
	public void setFromAddress(String newVal);

	/**
	 * ����Ŀ�ĵص�ַ�����α��ĵ���һ��ֱ��Ŀ�ĵء�
	 */
	public String getTargetAddress();

	/**
	 * ����Ŀ�ĵص�ַ�����α��ĵ���һ��ֱ��Ŀ�ĵء�
	 * 
	 * @param newVal
	 */
	public void setTargetAddress(String newVal);


	/**
	 * ��������
	 */
	public String getDataType();

	/**
	 * ��������
	 * 
	 * @param newVal
	 */
	public void setDataType(String val);

	/**
	 * ʧЧʱ��,��������ʱ��.
	 */
	public String getExpiredTime();

	/**
	 * ʧЧʱ��,��������ʱ��.
	 * 
	 * @param newVal
	 */
	public void setExpiredTime(String newVal);

	/**
	 * ���ȼ�
	 */
	public int getPriority();

	/**
	 * ���ȼ�
	 * 
	 * @param newVal
	 */
	public void setPriority(int newVal);

	/**
	 * ���ͱ�ʶ��
	 */
	public String getSendIdentify();

	/**
	 * ���ͱ�ʶ��
	 * 
	 * @param newVal
	 */
	public void setSendIdentify(String newVal);

	/**
	 * ���ݱ�ʶ�ø��������ڽ���ƽ̨�д�������
	 */
	public String getDataIdentify();

	/**
	 * ���ݱ�ʶ�ø��������ڽ���ƽ̨�д�������
	 * 
	 * @param newVal
	 */
	public void setDataIdentify(String newVal);
	
	/**
	 * ԭʼ�ļ�����
	 * @return ԭʼ�ļ�����
	 */
	public String getSourceDataName();
	/**
	 * ԭʼ�ļ�����
	 * @param dataName ԭʼ�ļ�����
	 */
	public void setSourceDataName(String sourcedataName);
	
	/**ԭʼ�ļ�����
	 * */
	public long getOriginalFileLength();
	
	public void setOriginalFileLength(long fileLength);
	
}
