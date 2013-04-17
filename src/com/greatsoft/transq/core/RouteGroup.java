package com.greatsoft.transq.core;

/**
 * 路由分组列表
 * 帮助接口， 用于表达路由结果。容纳所有下一跳地址相同的目的地地址
 * @author RAY
 *
 */
public interface RouteGroup {
	/**
	 * 获取该分组的下一跳地址
	 * @return 下一跳地址，null，表示该分组下的所有目的地不能到达
	 */
	String getNextHop();
	
	/**
	 * 设置下一跳地址
	 * @param address 下一跳地址
	 */
	void setNextHop(String nextHop);
	
	/**
	 * 返回该分组得所有目的地地址
	 * @return 该分组得所有目的地地址
	 */
	String[] getGroupedTargetAddress();
	
	/**
	 * 向分组中添加一个目的地地址
	 * @param address 待添加地址
	 */
	void addTargetAddress(String targetAddress);

	/**
	 * 删除分组中的一个目的地地址
	 * @param address 待删除的目的地地址
	 */
	void removeTargetAddress(String targetAddress);
	
	/**
	 * 获得分组中目的地地址个数
	 * @return 分组中目的地地址个数
	 */
	int getCount();
	
	/**
	 * 获得分组目的地地址列表中的第index个地址
	 * @param index 列表索引下标
	 * @return 分组目的地地址列表中的第index个地址. 
	 * @throws IndexOutOfBoundsException 当index超出索引范围时抛出异常
	 */
	String getAddress(int index) throws IndexOutOfBoundsException;	
	
	/**
	 * 获得分组目的地地址列表中的所有地址的组合
	 */
	String getTargetAddress();
}
