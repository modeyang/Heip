package com.test;

import java.util.Calendar;
import java.util.UUID;

import com.greatsoft.transq.utils.CalenderAndString;

public class TestCalendar {
	public static void main(String args[]){
		/*String temp = CalenderAndString.calendarToString(Calendar.getInstance());
		System.out.println(temp);
		
		Calendar calendar = CalenderAndString.stringToCalendar(temp);
		System.out.println(calendar.toString());*/
		
		System.out.println(UUID.randomUUID().toString());
	}
}
