package com.greatsoft.transq.core.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.greatsoft.transq.utils.ConstantValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class ResultImp {

	/**返回码**/
	private int returnCode;
	/**返回信息**/
	private String returnInfomation;
	/**返回消息**/
	private AbstractMessage returnMessage;

	public ResultImp() {
		this.returnCode = ConstantValue.PROCESS_SUCCESS;
		this.returnInfomation = ConstantValue.PROCESS_SUCCESS_INFO;
		this.returnMessage = null;
	}

	public ResultImp(int returnCode) {
		this.returnCode = returnCode;
		this.returnInfomation = getReturnCode(returnCode);
		this.returnMessage = null;
	}

	public ResultImp(int returnCode, AbstractMessage message) {
		this.returnCode = returnCode;
		this.returnInfomation = null;
		this.returnMessage = message;
	}

	public ResultImp(int returnCode, String returnInformation) {
		this.returnCode = returnCode;
		this.returnInfomation = returnInformation;
		this.returnMessage = null;
	}

	public ResultImp(int returnCode, String info, AbstractMessage message) {
		this.returnCode = returnCode;
		this.returnInfomation = info;
		this.returnMessage = message;
	}

	public AbstractMessage getReturnMessage() {
		return this.returnMessage;
	}

	public String getReturnInfo() {
		return this.returnInfomation;
	}

	private static String getReturnCode(int returnCode) {
		switch (returnCode) {
		case ConstantValue.PROCESS_SUCCESS:
			return ConstantValue.PROCESS_SUCCESS_INFO;
		case ConstantValue.PROCESS_FAILED:
			return ConstantValue.PROCESS_FAILED_INFO;
		}
		return null;
	}

	public int getReturnCode() {
		return this.returnCode;
	}
}
