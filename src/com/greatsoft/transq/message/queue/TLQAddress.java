package com.greatsoft.transq.message.queue;

import org.apache.log4j.Logger;

import com.greatsoft.transq.core.Address;
import com.greatsoft.transq.utils.ConstantValue;

/**
 * 地址的结构为：tlq://[userName[:password]@]ip:10024/qcuName/queueName 典型的三种tlq地址形式：
 * tlq://mo:111@127.0.0.1:8080/qcu/q tlq://mo@127.0.0.1:8080/qcu/q
 * tlq://127.0.0.1:8080/qcu/q
 * 
 * */
public class TLQAddress implements Address {
	private static Logger log = Logger.getLogger(TLQAddress.class);

	private String userName;
	private String password;
	private String ip;
	private int port;
	private String qcuName;
	private String queueName;

	public TLQAddress() {

	}

	public TLQAddress(String userName, String password, String ip, int port,
			String qcuName, String queueName) {
		this.userName = userName;
		this.password = password;
		this.ip = ip;
		this.port = port;
		this.queueName = queueName;
		this.qcuName = qcuName;
	}

	public TLQAddress(String userName, String ip, int port, String qcuName,
			String queueName) {
		this.userName = userName;
		password = "";
		this.ip = ip;
		this.port = port;
		this.queueName = queueName;
		this.qcuName = qcuName;
	}

	public TLQAddress(String ip, int port, String qcuName, String queueName) {
		userName = "";
		password = "";
		this.ip = ip;
		this.port = port;
		this.queueName = queueName;
		this.qcuName = qcuName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getQcuName() {
		return qcuName;
	}

	public void setQcuName(String qcuName) {
		this.qcuName = qcuName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/** 返回TLQ地址的：qcuName+queueName; */
	@Override
	public String getSubAddress() {
		return qcuName + queueName;
	}

	/** 返回TLQ地址的：ip+port */
	@Override
	public String getName() {
		return "" + ip + port;
	}

	@Override
	public String toString() {
		if ((userName == null || userName.equals(""))&& (password == null || password.equals(""))) {
			return "tlq://" + ip + ":" + port + "/" + qcuName + "/" + queueName;
		} else if (password == null || password.equals("")) {
			return "tlq://" + userName + "@" + ip + ":" + port + "/" + qcuName+ "/" + queueName;
		} else {
			return "tlq://" + userName + ":" + password + "@" + ip + ":" + port+ "/" + qcuName + "/" + queueName;
		}
	}

	/**
	 * 将一个tlq地址字符串转换为一个tlq地址,成功则返回一个TLQAddress,否则返回null
	 * 
	 * @param tlqAddressString
	 * @return
	 */
	public static TLQAddress parserOneAddress(String tlqAddressString) {

		String qcuName = null;
		String queueName = null;
		String user = null;
		String password = null;
		String ip = null;
		int port = 0;

		if (!tlqAddressString.startsWith(ConstantValue.TLQ_ADDRESS_BEGIN_STRING)) {
			log.error("地址字符串不是TLQ类型的地址："+tlqAddressString);
			return null;
		}
		String[] tlqAddressStringArray = tlqAddressString.split(ConstantValue.MORE_PATH_STRING);
		if (tlqAddressStringArray.length != 2|| !tlqAddressStringArray[0].equals(ConstantValue.TLQ)) {
			log.error("TLQ类型的地址格式有误："+tlqAddressString);
			return null;
		}
		String[] tlqAddressArray = tlqAddressStringArray[1].split("/");
		if (tlqAddressArray.length != 3) {
			log.error("TLQ类型的地址子元素有误："+tlqAddressString);
			return null;
		}
		qcuName = tlqAddressArray[1];
		queueName = tlqAddressArray[2];
		String[] userAndPassWordAndIpAndPortArray = tlqAddressArray[0]
				.split("@");
		if (userAndPassWordAndIpAndPortArray.length == 2) {
			String[] userAndPassWordArray = userAndPassWordAndIpAndPortArray[0]
					.split(":");
			if (userAndPassWordArray.length == 2) {
				user = userAndPassWordArray[0];
				password = userAndPassWordArray[1];
			} else if (userAndPassWordArray.length == 1) {
				user = userAndPassWordArray[0];
				password = null;
			} else {
				log.error("TLQ类型的地址解析错误:"+tlqAddressString);
				return null;
			}
			String[] ipAndPortArray = userAndPassWordAndIpAndPortArray[1]
					.split(":");
			if (ipAndPortArray.length != 2) {
				log.error("TLQ类型的地址解析错误:"+tlqAddressString);
				return null;
			}
			ip = ipAndPortArray[0];
			port = Integer.parseInt(ipAndPortArray[1]);
		} else if (userAndPassWordAndIpAndPortArray.length == 1) {
			String[] ipAndPortArray = userAndPassWordAndIpAndPortArray[0]
					.split(":");
			if (ipAndPortArray.length != 2) {
				log.error("TLQ类型的地址解析错误:"+tlqAddressString);
				return null;
			}
			ip = ipAndPortArray[0];
			port = Integer.parseInt(ipAndPortArray[1]);
		} else {
			log.error("TLQ类型的地址解析错误:"+tlqAddressString);
			return null;
		}
		return new TLQAddress(user, password, ip, port, qcuName, queueName);
	}
}
