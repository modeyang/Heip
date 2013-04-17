package com.greatsoft.transq.core;

import com.greatsoft.transq.utils.ConstantValue;

/**
 * 地址的结构为：department@hiepName
 * */
public class QueueAddress implements Address {

	private String department;
	private String hiepName;

	public QueueAddress(String hiepName) {
		this.department = ConstantValue.ALL_DEPARTMENT_STRING;
		this.hiepName = hiepName;
	}

	public QueueAddress(String department, String hiepName) {
		super();
		this.department = department;
		this.hiepName = hiepName;
	}
	
	@Override
	public String getName() {
		return hiepName;
	}
	
	@Override
	public String getSubAddress() {
		return this.department;
	}

	@Override
	public String toString() {
		if (null==department || department.equals(ConstantValue.NULL_STRING)) {
			department=ConstantValue.ALL_DEPARTMENT_STRING;
		} 
		return department + "@" + hiepName;
	}
}
