package com.greatsoft.transq.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.log4j.Logger;

public class CalenderAndString {
	private static Logger log = Log.getLog(CalenderAndString.class);
	
	public static String calendarToString(Calendar calendar) {
		DateFormat dateFormat = new SimpleDateFormat(ConstantValue.DATEFORMAT);
		return dateFormat.format(calendar.getTime());
	}
	
	public static Calendar stringToCalendar(String string) {
		DateFormat dateFormat = new SimpleDateFormat(ConstantValue.DATEFORMAT);
		Date date = null;
		Calendar calendar = Calendar.getInstance();
		try {
			date = dateFormat.parse(string);
			calendar.setTime(date);
		} catch (ParseException e) {
			log.error("将字符串转换成Calendar实例出现异常：string="+string+e.getMessage());
			return null;
		}
		return calendar;
	}

	

}
