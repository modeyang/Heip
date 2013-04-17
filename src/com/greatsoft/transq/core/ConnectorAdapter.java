package com.greatsoft.transq.core;


/**
 * 根据地址创建于该地址类型匹配的connector
 * @author RAY
 *
 */
public interface ConnectorAdapter {
/**
 * 根据输入地址及控制选项,创建与地址匹配的connector
 * @param address  地址
 * @param options  创建选项
 * @return 可用的Connector 实例, 如果失败返回null
 * 
 * 根据传来不同的address，返回不同的Connector
 */
	 Connector  getConnector(Address address, Object options);
}
