package com.greatsoft.transq.core;


/**
 * ���ݵ�ַ�����ڸõ�ַ����ƥ���connector
 * @author RAY
 *
 */
public interface ConnectorAdapter {
/**
 * ���������ַ������ѡ��,�������ַƥ���connector
 * @param address  ��ַ
 * @param options  ����ѡ��
 * @return ���õ�Connector ʵ��, ���ʧ�ܷ���null
 * 
 * ���ݴ�����ͬ��address�����ز�ͬ��Connector
 */
	 Connector  getConnector(Address address, Object options);
}
