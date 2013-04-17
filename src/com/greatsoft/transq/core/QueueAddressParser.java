package com.greatsoft.transq.core;

import java.util.ArrayList;
import java.util.List;
import com.greatsoft.transq.utils.ConstantValue;
/**？思考：此类中无成员变量，只有方法，则不需要实例，此处使用单例模式 */
public class QueueAddressParser implements AddressParser {
	private static QueueAddressParser addressParserImp=null;

	private QueueAddressParser() {
	}

	public static QueueAddressParser getInstance() {
		if(null==addressParserImp){
			addressParserImp=new QueueAddressParser();
		}
		return addressParserImp;
	}

	/**
	 * 多地址中间用半角的逗号分隔,解析地址字符串的时候就按照逗号分割，进行解析 例如：*@北京;农合,村卫@北京,武汉;村卫@南京;南京;*@南京,北京
	 */
	public Address[] parse(String addressString) {
		if (null==addressString || addressString.equals(ConstantValue.NULL_STRING)) {
			return null;
		}
		String[] addressStr = addressString.split(ConstantValue.ADDRESS_SEPARATOR);
		int addressNumber = addressStr.length;
		if (addressNumber <= 0) {
			return null;
		}
		List<String> addresslist = new ArrayList<String>();
		Address[] addresses = null;
		Address address = null;
		int addressesConut =0;
		for (int index = 0; index < addressNumber; index++) {
			addresses = parseOneAddress(addressStr[index]);
			if (null == addresses) {
				return null;
			}else {
				addressesConut = addresses.length;
				for (int addressesIndex = 0; addressesIndex < addressesConut; addressesIndex++) {
					address = addresses[addressesIndex];
					String addressName = address.getName();
					String addressString1 = address.toString();
					if (address.getSubAddress().equals(ConstantValue.ALL_DEPARTMENT_STRING)) {
						String addressExample = null;
						for (int i = 0; i < addresslist.size(); i++) {
							addressExample = addresslist.get(i);
							String hiepAddress = addressExample.split(ConstantValue.PARSE_SINGLE_ADDRESS_STRING)[1];
							if (hiepAddress.equals(addressName)) {
								addresslist.remove(addressExample);
								i--;
							}
						}
						addresslist.add(addressString1);
					} else if (!(addresslist.contains((new QueueAddress(addressName)).toString()))) {
						if (!(addresslist.contains(addressString1))) {
							addresslist.add(addressString1);
						}
					}
				}
			}
		}
		int addressCount = addresslist.size();
		Address[] addressArray = new Address[addressCount];
		for (int index = 0; index < addressCount; index++) {
			String[] addressString2 = addresslist.get(index).split(ConstantValue.PARSE_SINGLE_ADDRESS_STRING);
			addressArray[index] = new QueueAddress(addressString2[0],addressString2[1]);
		}
		return addressArray;
	}

	/**
	 * 传进来一个单个地址字符串，解析得到一个地址对象 q1@qmanager 地址。
	 * 其正确的单地址格式支持：
	 * 1、"农合, 村卫@北京,武汉" 解析为："农合@北京；村卫@北京；农合@武汉；村卫@武汉;北京"
	 * 2、"*@北京" 解析为："*@北京"
	 * 3、"北京"  解析为："*@北京"
	 * @param addressString 单个地址字符串
	 * @return 解析成功返回：Address[] 解析出来的QueueAddress。解析失败返回null
	 */
	public Address[] parseOneAddress(String addressString) {
		if (null==addressString || addressString.equals(ConstantValue.NULL_STRING) || addressString.endsWith(ConstantValue.PARSE_SINGLE_ADDRESS_STRING)||addressString.endsWith(ConstantValue.SINGLE_ADDRESS_SPLIT_STRING)) {
			return null;
		}
		/**将数据处理部门和JHIEP交换节点地址分离*/
		String[] parseAddress = addressString.split(ConstantValue.PARSE_SINGLE_ADDRESS_STRING);
		int parseAddressLength = parseAddress.length;
		Address[] address =null;
		switch(parseAddressLength){
		case 1:/**地址格式为"北京"*/
			if (!parseAddress[0].equals(ConstantValue.NULL_STRING)) {
				if (1==parseAddress[0].split(ConstantValue.SINGLE_ADDRESS_SPLIT_STRING).length) {
					address = new Address[1];
					address[0] = new QueueAddress(parseAddress[0]);
				}
			}
			break;
		case 2:
			if (parseAddress[0].equals(ConstantValue.ALL_DEPARTMENT_STRING) && !parseAddress[1].equals(ConstantValue.NULL_STRING)){
				/**地址格式为"*@北京,武汉"*/
				String[] parseQueueManagerAddress = parseAddress[1].split(ConstantValue.SINGLE_ADDRESS_SPLIT_STRING);
				int parseQueueManagerAddressConut = parseQueueManagerAddress.length;
				address = new Address[parseQueueManagerAddressConut];
				for (int index = 0; index < parseQueueManagerAddressConut; index++) {
					if(parseQueueManagerAddress[index].equals(ConstantValue.NULL_STRING)){
						return null;
					}
					address[index] = new QueueAddress(parseQueueManagerAddress[index++]);
				}
			}else if (!parseAddress[0].equals(ConstantValue.NULL_STRING) && !parseAddress[1].equals(ConstantValue.NULL_STRING)) {
				/**地址格式为"农合, 村卫@北京,武汉"*/
				String[] parseQueueManagerAddress = parseAddress[1].split(ConstantValue.SINGLE_ADDRESS_SPLIT_STRING);
				int parseQueueManagerAddressCount = parseQueueManagerAddress.length;
				String[] parseSubAddress = parseAddress[0].split(ConstantValue.SINGLE_ADDRESS_SPLIT_STRING);
				int parseSubAddressCount = parseSubAddress.length;
				address = new Address[parseSubAddressCount*parseQueueManagerAddressCount];
				int addressIndex = 0;
				for (int index = 0; index < parseQueueManagerAddress.length; index++) {
					for (int subIndex = 0; subIndex < parseSubAddress.length; subIndex++) {
						if(parseQueueManagerAddress[index].equals(ConstantValue.NULL_STRING) || parseSubAddress[subIndex].equals(ConstantValue.NULL_STRING)){
							return null;
						}
						address[addressIndex++] = new QueueAddress(parseSubAddress[subIndex],parseQueueManagerAddress[index]);
					}
				}
			}
			break;
			default:
				return null;
		}
		return address;
	}
}
