package com.greatsoft.transq.core;

import java.util.List;

public class RouterRecord {

	private String local;
	private String target;
	private String next;

	public RouterRecord(String local, String target, String next) {
		super();
		this.local = local;
		this.target = target;
		this.next = next;
	}

	public String getlocal() {
		return local;
	}

	public String gettarget() {
		return target;
	}

	public String getnext() {
		return next;
	}
	
	@Override
	public String toString() {
		return "RouterRecord [localAddress=" + local + ", targetAddress="
				+ target + ", nextAddress=" + next + "]";
	}
	
	/**检测目的地址列表中的所有目的地址是否可达*/
	public static boolean checkRouterTable(List<RouterRecord> routerRecordList,List<String> targetList) {
		for(String target:targetList){
			if(!checkTarget(routerRecordList,target)){
				System.out.println("路由表中存在不可达的目的地址"+target);
				return false;
			}
		}
		return true;
	}
	
	/**检测目的地址是否可达*/
	public static boolean checkTarget(List<RouterRecord> routerRecordList,String target) {
		boolean flag=false;
		String next=null;
		for(RouterRecord routerRecord:routerRecordList){
			if(routerRecord.gettarget().equals(target)){
				next=routerRecord.getnext();
				if(next.equals(target)){
					flag=true;
				}else{
					flag=checkTarget(routerRecordList,next);
				}
			}
		}
		return flag;
	}
}
