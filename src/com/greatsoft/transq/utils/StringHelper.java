package com.greatsoft.transq.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHelper {
	/**
	 * ÅÐ¶Ï×Ö·û´®ÊÇ·ñÊÇÊý×Ö×Ö·û´®
	 * */
	public static boolean isNumberString(String string) {
		if (null==string || string.equals(ConstantValue.NULL_STRING)) {
			return false;
		}
		Pattern pattern = Pattern.compile(ConstantValue.NUMBER_STRING);
		return pattern.matcher(string).matches();
	}

	/**
	 * ÅÐ¶Ï×Ö·û´®ÊÇ·ñÎª¿Õ
	 * */
	public static boolean isNullString(String string) {
		if (null==string || string.equals(ConstantValue.NULL_STRING)) {
			return true;
		}
		return false;
	}
	
	/**ÅÐ¶Ï×Ö·û´®ÊÇ·ñÎª×¢ÊÍÐÐ*/
	public static boolean isComment(String line) {
		Pattern pattern = Pattern.compile("^#.*");
		Matcher matcher = pattern.matcher(line);
		return matcher.matches();
	}
}
