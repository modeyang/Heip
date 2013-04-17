package com.greatsoft.transq.core;

import java.util.List;

/**
 * ·�ɽ�����
 * ������Ŀ�ĵأ�����Ŀ�ĵص���һ��
 * @author RAY
 *
 */
public interface Router {
	/**
	 * �����ɳ����ص�Ŀ�ĵص���һ����ַ
	 * @param from �����ص�ַ
	 * @param target Ŀ�ĵص�ַ
	 * @return �ӳ����ص�Ŀ�ĵص���һ����ַ��null����ʾδ�ҵ���һ����ַ�� 
	 */
	Address getNextAddress(Address from, Address target);
	
	/**
	 * ȡ���ɳ����ص���Ŀ�ĵ�;�������е�ַ
	 * @param from �����ص�ַ
	 * @param target Ŀ�ĵص�ַ
	 * @return �ɳ����ص���Ŀ�ĵ�;�������е�ַ���ɽ���Զ���С�null���ӳ����ز��ܵ���Ŀ�ĵء�
	 */
	List<Address> getRouter(Address target);
	
	/**
	 * ��һ��Ŀ�ĵص�ַ����һ����ַ����
	 * @param from ������
	 * @param targets Ŀ�ĵ��б�
	 * @return ��ַ���顣������getNextHop����null��Ԫ�ط������в��ܵ����Ŀ�ĵأ�
	 * @see RouteGroup
	 */
	RouteGroup[] groupByNext(Address from, Address[] targets);
}
