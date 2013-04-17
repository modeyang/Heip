package com.greatsoft.transq.core.task;


/**
 * �����¼
 */
public interface Task {

	/**
	 * ����ID,�����Ψһ��ʶ��
	 * 
	 * @return ����ID
	 */
	public String getId();

	/**
	 * ����ID,�����Ψһ��ʶ��
	 * 
	 * @param id
	 *            ����ID
	 */
	public void setId(String id);

	/**
	 * �������ȼ� 0-9
	 * 
	 * @return �������ȼ�
	 */
	public int getPriority();

	/**
	 * �������ȼ� 0-9
	 * 
	 * @param priority
	 *            �������ȼ�
	 */
	public void setPriority(int priority);

	/**
	 * ���������ʱ��
	 * 
	 * @return ���������ʱ��
	 */
	public String getEnterTime();

	/**
	 * ���������ʱ��
	 * 
	 * @param enterTime
	 *            ���������ʱ��
	 */
	public void setEnterTime(String enterTime);

	/**
	 * ʧЧʱ��
	 * 
	 * @return ʧЧʱ��
	 */
	public String getExpiredTime();

	/**
	 * ʧЧʱ��
	 * 
	 * @param expiredTime
	 *            ʧЧʱ��
	 */
	public void setExpiredTime(String expiredTime);

	/**
	 * ����״̬
	 * 
	 * @return ����״̬
	 */
	public int getStatus();

	/**
	 * ����״̬
	 * 
	 * @param status
	 *            ����״̬
	 */
	public void setStatus(int status);

	/**
	 * �������
	 * 
	 * @return �������
	 */
	public String getDataType();

	/**
	 * �������
	 * 
	 * @param dataClass
	 *            �������
	 */
	public void setDataType(String dataType);

	/**
	 * ��һվ��ַ
	 * 
	 * @return ��һվ��ַ
	 */
	public String getToAddress();

	/**
	 * ��һվ��ַ
	 * 
	 * @param toAddress
	 *            ��һվ��ַ
	 */
	public void setToAddress(String toAddress);

	/**
	 * ������ʾ����
	 * 
	 * @return ������ʾ����
	 */
	public String getSourceDataName();

	/**
	 * �ļ���
	 * 
	 * @param dataName
	 *            ������ʾ����
	 */
	public void setSourceDataName(String sourcedataName);

	/**
	 * ����Ψһ��ʶ
	 * 
	 * @return ����Ψһ��ʶ
	 */
	public String getDataIdentfy();

	/**
	 * �ļ�Ψһ��ʶ
	 * 
	 * @param dataIdentfy
	 *            ����Ψһ��ʶ
	 */
	public void setDataIdentfy(String dataIdentfy);

	/**
	 * ����ļ���
	 * */
	public void setSendIdentify(String sendId);

	public String getSendIdentify();
}
