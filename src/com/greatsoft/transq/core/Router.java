package com.greatsoft.transq.core;

import java.util.List;

/**
 * 路由解析器
 * 按给定目的地，解析目的地的下一跳
 * @author RAY
 *
 */
public interface Router {
	/**
	 * 解析由出发地到目的地的下一跳地址
	 * @param from 出发地地址
	 * @param target 目的地地址
	 * @return 从出发地到目的地的下一跳地址，null，表示未找到下一跳地址。 
	 */
	Address getNextAddress(Address from, Address target);
	
	/**
	 * 取得由出发地地至目的地途经的所有地址
	 * @param from 出发地地址
	 * @param target 目的地地址
	 * @return 由出发地地至目的地途经的所有地址，由近至远排列。null，从出发地不能到达目的地。
	 */
	List<Address> getRouter(Address target);
	
	/**
	 * 将一组目的地地址按下一跳地址分组
	 * @param from 出发地
	 * @param targets 目的地列表
	 * @return 地址分组。分组中getNextHop返回null的元素返回所有不能到达的目的地，
	 * @see RouteGroup
	 */
	RouteGroup[] groupByNext(Address from, Address[] targets);
}
