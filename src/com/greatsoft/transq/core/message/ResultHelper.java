package com.greatsoft.transq.core.message;
import com.greatsoft.transq.utils.ConstantValue;

public class ResultHelper {
	public static ResultImp getResult(int putResult) {
		switch (putResult) {
		case ConstantValue.PUT_MESSAGE_SUCCESS:
			return new ResultImp(ConstantValue.PROCESS_SUCCESS);
		case ConstantValue.PUT_MESSAGE_FAILED:
			return new ResultImp(ConstantValue.PROCESS_FAILED);
		}
		return null;
	}
}
