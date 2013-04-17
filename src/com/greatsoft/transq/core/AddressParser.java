package com.greatsoft.transq.core;

import com.greatsoft.transq.exception.InvalidAddressException;

/**
 * �����ַ�����ʾ�ĵ�ַ,����ϵͳ��׼��ַ����
 * @author RAY
 *
 */
public interface AddressParser {
	/**
	 * �����ַ�����ʾ�ĵ�ַ
	 * @param addressString �ַ�����ʾ�ĵ�ַ
	 * @return ���������ַ����а��������е�ַ. ���صĵ�ַ���Ϳ�����Address�Ĳ�ͬʵ��. 
	 * @throws InvalidAddressException ����addressString����ʧ��ʱ.
	 * 
	 * ������������tonglinkQ�ĵ�ַ��ʱ�򣬽����õ�����tonglinkQ�ĵ�ַ�����б�
	 * ������������webservices�ĵ�ַ��ʱ�򣬽����õ�����webservices�ĵ�ַ������б�
	 */
	 public Address[] parse(String addressString) throws InvalidAddressException;	
}
