package com.greatsoft.transq.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.greatsoft.transq.utils.Config;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.StringHelper;

public class RouterImp /*implements Router*/ {
	/**
	 * �����ɳ����ص�Ŀ�ĵص���һ����ַ
	 * @param from �����ص�ַ
	 * @param target Ŀ�ĵص�ַ
	 * @return �ӳ����ص�Ŀ�ĵص���һ����ַ��null����ʾδ�ҵ���һ����ַ�� 
	 */
	public static Address getNextAddress(Address from, Address target) {
		String nextAddresssName = null;
		int routeRecordListSize = Config.routerRecordList.size();
		for (int i = 0; i < routeRecordListSize; i++) {
			if (from.toString().equals(
					Config.routerRecordList.get(i).getlocal())
					&& target.toString().equals(
							Config.routerRecordList.get(i).gettarget())) {
				nextAddresssName = Config.routerRecordList.get(i).getnext();
			}
		}
		return new QueueAddress(nextAddresssName);
	}
	
	/**
	 * ��һ��Ŀ�ĵص�ַ����һ����ַ����
	 * @param from ������
	 * @param targets Ŀ�ĵ��б�
	 * @return ��ַ���顣������getNextHop����null��Ԫ�ط������в��ܵ����Ŀ�ĵأ�
	 * @see RouteGroup
	 */
	public static RouteGroup[] groupByNext(String fromAddressName,Address[] targets) {
		return groupByNext(fromAddressName,targets, Config.routerRecordList);
	}
/**
 * ��һ��Ŀ�ĵص�ַ����һ����ַ����
 * @param fromAddressName ������
 * @param targets Ŀ�ĵ��б�
 * @param routerRecordList ·�ɱ�
 * @return ��ַ���� ������getNextHop����null��Ԫ�ط������в��ܵ����Ŀ�ĵأ�
 */
	public static RouteGroup[] groupByNext(String fromAddressName,Address[] targets, List<RouterRecord> routerRecordList) {
		int targetAddressCount = targets.length;
		int routeRecordListCount = routerRecordList.size();
		int routeGroupArrayHasCount = 0;
		RouteGroup[] routeGroupArray = new RouteGroup[targetAddressCount];
		List<String> nextHopList = new ArrayList<String>();
		String nextHop = null;
		RouteGroup routerGroup = null;
		RouterRecord routerRecord = null;
		for (int i = 0; i < targetAddressCount; i++) {
			/**
			 * �ҳ�from��target[i]����һ��,���磺[�����Ϻ� �人] ���ҳ� ����,�Ϻ�����һ�����人
			 */
			for (int j = 0; j < routeRecordListCount; j++) {
				routerRecord = routerRecordList.get(j);
				if (fromAddressName.equals(routerRecord.getlocal())
						&& targets[i].getName().equals(
								routerRecord.gettarget())) {
					nextHop = routerRecord.getnext();
					break;
				}
			}
			if (nextHopList.contains(nextHop)) {
				/**
				 * ����õ���nextHop�������Ѿ��õ���,��ֱ�ӽ�Ŀ�ĵ�ַ������Ӧ��Ŀ�ĵ�ַ������
				 */
				for (int index = 0; index < routeGroupArrayHasCount; index++) {
					routerGroup = routeGroupArray[index];
					if (routerGroup.getNextHop().equals(nextHop)) {
						routerGroup.addTargetAddress(targets[i].toString());
					}
				}
			} else {
				/**
				 * �������µ���һ����ַ������һ����ַ�Ͷ�Ӧ��Ŀ�ĵ�ַ���Ϊ�µ�routeGroup��������routeGroupList��
				 */
				nextHopList.add(nextHop);
				routerGroup = new RouteGroupImp(nextHop);
				routerGroup.addTargetAddress(targets[i].toString());
				routeGroupArray[routeGroupArrayHasCount++] = routerGroup;
			}
		}
		return routeGroupArray;
	}

	
	/**
	 * ͨ��ƥ��·�ɱ�ó�fromAddress��targetAddress����һ����ַ ���磺·�ɼ�¼���£� ���� �Ϻ� �人
	 * ����ô˺���֮�󷵻�"�人"
	 * �˺����������Ĳ���Ҫ����ϸ�fromAddress��targetAddress�����ֶα����·�ɱ����ֶ���ʽһ��
	 * */
	public static String getNextAddress(String routeTablePath,String fromAddress, String targetAddress) {
		String line = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(routeTablePath));
		} catch (FileNotFoundException e) {
			System.out.println("·�������ļ������ڡ�"+ e.getMessage());
		}
		String[] strArray = null;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				line = line.trim();
				if (StringHelper.isComment(line)) {
					continue;
				}
				if (line.trim() == null || line.equals(ConstantValue.NULL_STRING)) {
					continue;
				}
				strArray = line.split("(" + (char) 32 + "|" + (char) 9 + ")+");
				if(strArray.length!=3){
					System.out.println("·�������ļ��ڵ���Ϣ����line["+line+"]");
					return null;
				}
				if (strArray[0].trim().equals(fromAddress)
						&& strArray[1].trim().equals(targetAddress)) {
					return strArray[2].trim();
				}
			}
		} catch (IOException e) {
			System.out.println("·�������ļ��ڵ���Ϣ����\n"+ e.getMessage());
			return null;
		}finally{
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
