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
	 * 解析由出发地到目的地的下一跳地址
	 * @param from 出发地地址
	 * @param target 目的地地址
	 * @return 从出发地到目的地的下一跳地址，null，表示未找到下一跳地址。 
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
	 * 将一组目的地地址按下一跳地址分组
	 * @param from 出发地
	 * @param targets 目的地列表
	 * @return 地址分组。分组中getNextHop返回null的元素返回所有不能到达的目的地，
	 * @see RouteGroup
	 */
	public static RouteGroup[] groupByNext(String fromAddressName,Address[] targets) {
		return groupByNext(fromAddressName,targets, Config.routerRecordList);
	}
/**
 * 将一组目的地地址按下一跳地址分组
 * @param fromAddressName 出发地
 * @param targets 目的地列表
 * @param routerRecordList 路由表
 * @return 地址分组 分组中getNextHop返回null的元素返回所有不能到达的目的地，
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
			 * 找出from和target[i]的下一跳,例如：[北京上海 武汉] 则找出 北京,上海的下一跳是武汉
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
				 * 如果得到的nextHop，曾经已经得到过,则直接将目的地址加入响应的目的地址队列中
				 */
				for (int index = 0; index < routeGroupArrayHasCount; index++) {
					routerGroup = routeGroupArray[index];
					if (routerGroup.getNextHop().equals(nextHop)) {
						routerGroup.addTargetAddress(targets[i].toString());
					}
				}
			} else {
				/**
				 * 检索出新的下一条地址，则将下一条地址和对应的目的地址组合为新的routeGroup，并加入routeGroupList中
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
	 * 通过匹配路由表得出fromAddress到targetAddress的下一跳地址 例如：路由记录如下： 北京 上海 武汉
	 * 则调用此函数之后返回"武汉"
	 * 此函数传进来的参数要求很严格。fromAddress和targetAddress两个字段必须和路由表中字段形式一样
	 * */
	public static String getNextAddress(String routeTablePath,String fromAddress, String targetAddress) {
		String line = null;
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(routeTablePath));
		} catch (FileNotFoundException e) {
			System.out.println("路由配置文件不存在。"+ e.getMessage());
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
					System.out.println("路由配置文件内的信息错误。line["+line+"]");
					return null;
				}
				if (strArray[0].trim().equals(fromAddress)
						&& strArray[1].trim().equals(targetAddress)) {
					return strArray[2].trim();
				}
			}
		} catch (IOException e) {
			System.out.println("路由配置文件内的信息错误。\n"+ e.getMessage());
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
