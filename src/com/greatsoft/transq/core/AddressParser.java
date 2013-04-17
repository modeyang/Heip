package com.greatsoft.transq.core;

import com.greatsoft.transq.exception.InvalidAddressException;

/**
 * 根据字符串表示的地址,解析系统标准地址对象
 * @author RAY
 *
 */
public interface AddressParser {
	/**
	 * 解析字符串表示的地址
	 * @param addressString 字符串表示的地址
	 * @return 返回输入字符串中包含的所有地址. 返回的地址类型可以是Address的不同实现. 
	 * @throws InvalidAddressException 参数addressString解析失败时.
	 * 
	 * 当传进来的是tonglinkQ的地址的时候，解析得到的是tonglinkQ的地址对象列表
	 * 当传进来的是webservices的地址的时候，解析得到的是webservices的地址对象的列表
	 */
	 public Address[] parse(String addressString) throws InvalidAddressException;	
}
