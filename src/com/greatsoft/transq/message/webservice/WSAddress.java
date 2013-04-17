package com.greatsoft.transq.message.webservice;

import com.greatsoft.transq.core.Address;

/**
 * webservice�ĵ�ַ���� ������http://localhost:9000/transq/services/centerService
 * 
 * @author mojia
 * 
 */
public class WSAddress implements Address {

	private String ip;
	private String port;
	private String subAddress;

	public WSAddress() {

	}

	public WSAddress(String ip, String port, String subAddress) {
		this.ip = ip;
		this.port = port;
		this.subAddress = subAddress;
	}

	/**
	 * ����IP:PORT
	 */
	@Override
	public String getName() {
		return ip + port;
	}

	@Override
	public String getSubAddress() {
		return subAddress;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setSubAddress(String subAddress) {
		this.subAddress = subAddress;
	}

	/**
	 * ��һ��ws��ַ�ַ���ת��Ϊһ��ws��ַ,�ɹ��򷵻�һ��WSAddress,���򷵻�null
	 * 
	 * @param tlqAddressString
	 * @return
	 */
	public static WSAddress parserOneAddress(String wsAddressString) {
		if (!wsAddressString.startsWith("http://")) {
			return null;
		}
		String lastAddress = wsAddressString.substring(7);
		String[] parts = lastAddress.split("/");
		
		String ip = parts[0].split(":")[0];
		String port = parts[0].split(":")[1];
		StringBuilder subAddress=new StringBuilder("");
		for(int i=1;i<parts.length;i++){
			if(i==parts.length-1){
				subAddress.append(parts[i]);
			}else{
				subAddress.append(parts[i]+"/");
			}
		}
		
		WSAddress address = new WSAddress(ip, port, subAddress.toString());

		return address;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("http://");
		sb.append(ip + ":");
		sb.append(port + "/");
		sb.append(subAddress);

		return sb.toString();
	}
}
