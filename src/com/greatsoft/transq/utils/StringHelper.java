package com.greatsoft.transq.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
	/**
	 * �ж��ַ����Ƿ��������ַ���
	 * */
	public static boolean isNumberString(String string) {
		if (null==string || string.equals(ConstantValue.NULL_STRING)) {
			return false;
		}
		Pattern pattern = Pattern.compile(ConstantValue.NUMBER_STRING);
		return pattern.matcher(string).matches();
	}

	/**
	 * �ж��ַ����Ƿ�Ϊ��
	 * */
	public static boolean isNullString(String string) {
		if (null==string || string.equals(ConstantValue.NULL_STRING)) {
			return true;
		}
		return false;
	}
	
	/**�ж��ַ����Ƿ�Ϊע����*/
	public static boolean isComment(String line) {
		Pattern pattern = Pattern.compile("^#.*");
		Matcher matcher = pattern.matcher(line);
		return matcher.matches();
	}
}
