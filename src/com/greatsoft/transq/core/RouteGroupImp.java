package com.greatsoft.transq.core;

import com.greatsoft.transq.utils.ConstantValue;

public class RouteGroupImp implements RouteGroup {
	/**下一跳地址*/
	private String nextHop;
	/**路由地址字符串数组*/
	private String[] groupedAddress;
	/**路由地址字符串数组的个数*/
	private int groupedAddressCount;

	public RouteGroupImp(String nextHop) {
		this.nextHop = nextHop;
		this.groupedAddress = null;
		this.groupedAddressCount = 0;
	}

	public RouteGroupImp(String nextHop, String[] groupedAddress) {
		this.nextHop = nextHop;
		this.groupedAddress = groupedAddress;
		this.groupedAddressCount = groupedAddress.length;
	}

	@Override
	public final String getNextHop() {
		return nextHop;
	}

	@Override
	public final void setNextHop(String nextHop) {
		this.nextHop = nextHop;

	}

	@Override
	public String[] getGroupedTargetAddress() {
		return this.groupedAddress;
	}

	@Override
	public void addTargetAddress(String targetAddress) {
		if (groupedAddressCount == 0) {
			groupedAddressCount = 1;
			groupedAddress = new String[1];
			groupedAddress[0] = targetAddress;
		}else{
			int newTargetAddressCount = groupedAddressCount + 1;
			String[] newTargetAddress = new String[newTargetAddressCount];
			for (int index = 0; index < groupedAddressCount; index++) {
				newTargetAddress[index] = groupedAddress[index];
			}
			newTargetAddress[newTargetAddressCount - 1] = targetAddress;
			groupedAddress = newTargetAddress;
			groupedAddressCount = newTargetAddressCount;
		}
	}

	@Override
	public void removeTargetAddress(String targetAddress) {
		for (int index = 0; index < groupedAddressCount; index++) {
			if (groupedAddress[index].equals(targetAddress)) {
				int newTargetAddressCount = groupedAddressCount - 1;
				String[] newTargetAddress = new String[newTargetAddressCount];
				for (int newIndex = 0; newIndex < index; newIndex++) {
					newTargetAddress[newIndex] = groupedAddress[newIndex];
				}
				for (int newIndex = index; newIndex <= newTargetAddressCount; newIndex++) {
					newTargetAddress[newIndex] = groupedAddress[newIndex + 1];
				}
				groupedAddress = newTargetAddress;
				groupedAddressCount = newTargetAddressCount;
				return;
			}
		}
	}

	@Override
	public int getCount() {
		return groupedAddressCount;
	}

	@Override
	public String getAddress(int index) throws IndexOutOfBoundsException {
		if(index>=groupedAddressCount || index<0){
			throw new IndexOutOfBoundsException();
		}
		if (groupedAddressCount == 0) {
			return null;
		}
		return groupedAddress[index];
	}

	/**
	 * 通过RouteGroup得到新的新的信封头的目的地址字段
	 * */
	public String getTargetAddress() {
		if(groupedAddressCount==0){
			return null;
		}
		String addressString = null;
		String newTargetAddress = ConstantValue.NULL_STRING;
		for (int index = 0; index < groupedAddressCount; index++) {
			addressString = groupedAddress[index];
			newTargetAddress += addressString+ConstantValue.ADDRESS_SEPARATOR_ONE;
		}
		return newTargetAddress.substring(0, newTargetAddress.length() - 1);
	}
}
