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
		
		return calendar.getTimeInMillis() / 1000L;
	}
}
