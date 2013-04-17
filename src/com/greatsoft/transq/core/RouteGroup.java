package com.greatsoft.transq.core;

/**
 * ·�ɷ����б�
 * �����ӿڣ� ���ڱ��·�ɽ��������������һ����ַ��ͬ��Ŀ�ĵص�ַ
 * @author RAY
 *
 */
public interface RouteGroup {
	/**
	 * ��ȡ�÷������һ����ַ
	 * @return ��һ����ַ��null����ʾ�÷����µ�����Ŀ�ĵز��ܵ���
	 */
	String getNextHop();
	
	/**
	 * ������һ����ַ
	 * @param address ��һ����ַ
	 */
	void setNextHop(String nextHop);
	
	/**
	 * ���ظ÷��������Ŀ�ĵص�ַ
	 * @return �÷��������Ŀ�ĵص�ַ
	 */
	String[] getGroupedTargetAddress();
	
	/**
	 * ����������һ��Ŀ�ĵص�ַ
	 * @param address ����ӵ�ַ
	 */
	void addTargetAddress(String targetAddress);

	/**
	 * ɾ�������е�һ��Ŀ�ĵص�ַ
	 * @param address ��ɾ����Ŀ�ĵص�ַ
	 */
	void removeTargetAddress(String targetAddress);
	
	/**
	 * ��÷�����Ŀ�ĵص�ַ����
	 * @return ������Ŀ�ĵص�ַ����
	 */
	int getCount();
	
	/**
	 * ��÷���Ŀ�ĵص�ַ�б��еĵ�index����ַ
	 * @param index �б������±�
	 * @return ����Ŀ�ĵص�ַ�б��еĵ�index����ַ. 
	 * @throws IndexOutOfBoundsException ��index����������Χʱ�׳��쳣
	 */
	String getAddress(int index) throws IndexOutOfBoundsException;	
	
	/**
	 * ��÷���Ŀ�ĵص�ַ�б��е����е�ַ�����
	 */
	String getTargetAddress();
}
