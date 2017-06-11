package com.shsy.motoinspect.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static Date getTargetDate(int days){
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, calendar.get(Calendar.DATE)-(days-1));
		
		SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
		Date target =null;
		try {
			target = dft.parse(dft.format(calendar.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return target;
	}
	
	
	/**
	 * 比较两个日期之间的大小 
	 * @param d1
	 * @param d2
	 * @return 前者大于后者返回true 反之false 
	 */
	public static boolean compareDate(Date d1, Date d2) {  
	    Calendar c1 = Calendar.getInstance();  
	    Calendar c2 = Calendar.getInstance();  
	    c1.setTime(d1);  
	    c2.setTime(d2);  
	  
	    int result = c1.compareTo(c2);  
	    if (result >= 0)  
	        return true;  
	    else  
	        return false;  
	}
	
	
	public static boolean compareDate(String strDate,int days){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date= sdf.parse(strDate);
			Date target = getTargetDate(days);
			return compareDate(date,target);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
}
