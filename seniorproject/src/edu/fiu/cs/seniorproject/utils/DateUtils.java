package edu.fiu.cs.seniorproject.utils;

import java.util.Calendar;

public class DateUtils {
	
	public static final long ONE_DAY = 24 * 60 * 60;
	public static final long SEVEN_DAYS = 7 * 24 * 60 * 60;
	public static final long ONE_MONTH = 30 * 24 * 60 * 60;
	
	public static long getTodayTimeInMiliseconds() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.HOUR_OF_DAY);
		calendar.clear(Calendar.HOUR);
		
		return calendar.getTimeInMillis();
	}
	
	public static long getThisWeekendInMiliseconds() {
		Calendar calendar = Calendar.getInstance();
		
		int today = calendar.get(Calendar.DAY_OF_WEEK);
		if ( today != Calendar.SATURDAY ) {
			if ( today == Calendar.SUNDAY ) {
				calendar.add(Calendar.DAY_OF_WEEK, -1);	// go back to Saturday
			} else {
				calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // go forward to Saturday
			}
		}
		
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.HOUR_OF_DAY);
		calendar.clear(Calendar.HOUR);
		
		return calendar.getTimeInMillis();
	}
	
	public static long getNextWeekendInMiliseconds() {
		return getThisWeekendInMiliseconds() + SEVEN_DAYS * 1000;
	}
}
