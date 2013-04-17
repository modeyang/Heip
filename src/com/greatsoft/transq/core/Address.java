package com.greatsoft.transq.core;
/**
 * 交换平台地址
 * @author  RAY  该接口实现类,须要重载 toString 方法,已获得该实现地址的字符串表示.
 */
public interface Address {
	/**
	 * 取得地址名称
	 * @return 地址名称,也就是给业务人员使用的名字。诸如：北京，上海之类的
	 * @uml.property  name="name"
	 */
	String getName();
	String getSubAddress();
	String toString();
}
