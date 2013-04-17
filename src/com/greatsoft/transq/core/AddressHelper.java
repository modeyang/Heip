package com.greatsoft.transq.core;

import org.apache.log4j.Logger;

import com.greatsoft.transq.message.queue.TLQAddress;
import com.greatsoft.transq.message.webservice.WSAddress;
import com.greatsoft.transq.utils.ConstantValue;
import com.greatsoft.transq.utils.ErrorCode;
import com.greatsoft.transq.utils.Log;

public class AddressHelper {
	private static Logger log = Log.getLog(AddressHelper.class);

	/**
	 * 将地址字符串转变为一个Address
	 * 
	 * @param addressString
	 * @return Address
	 */
	public static Address parse(String addressString) {
		if (addressString.startsWith(ConstantValue.TLQ_ADDRESS_BEGIN_STRING)) {
			return TLQAddress.parserOneAddress(addressString);
		} else if (addressString.startsWith("http://")) {
			return WSAddress.parserOneAddress(addressString);
		}
		return null;
	}

	/**
	 * 将给定的地址字符串转换为一个Address数组
	 * 
	 * @param addressString
	 * @return Address[]
	 */
	public static Address[] parseAddresses(String addressString) {
		String[] addressStringArray = addressString
				.split(ConstantValue.ADDRESS_SEPARATOR);
		if (addressStringArray == null) {
			return null;
		}
		int length = addressStringArray.length;
		if (length <= 0) {
			return null;
		}
		Address[] addressArray = new Address[length];
		for (int i = 0; i < length; i++) {
			addressArray[i] = parse(addressStringArray[i]);
			if (null == addressArray[i]) {
				log.error(ErrorCode.ADDRESS_PARSE_ERROR);
				return null;
			}
		}
		return addressArray;
	}
}
