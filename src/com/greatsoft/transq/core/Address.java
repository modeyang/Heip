package com.greatsoft.transq.core;
/**
 * ����ƽ̨��ַ
 * @author  RAY  �ýӿ�ʵ����,��Ҫ���� toString ����,�ѻ�ø�ʵ�ֵ�ַ���ַ�����ʾ.
 */
public interface Address {
	/**
	 * ȡ�õ�ַ����
	 * @return ��ַ����,Ҳ���Ǹ�ҵ����Աʹ�õ����֡����磺�������Ϻ�֮���
	 * @uml.property  name="name"
	 */
	String getName();
	String getSubAddress();
	String toString();
}
