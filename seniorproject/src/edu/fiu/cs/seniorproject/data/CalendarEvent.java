package edu.fiu.cs.seniorproject.data;

public class CalendarEvent {
	
	private long calendarID;
	private String calendarDisplayName;
	private String calendarAccountName;
	private String calendarAccountType;
	
	public CalendarEvent(long calendarID, String calendarDisplayName, String calendarAccountName, String calendarAccountType)
	{
		 this.calendarID = calendarID;
		 this.calendarDisplayName= calendarDisplayName;
		 this.calendarAccountName = calendarAccountName;
		 this.calendarAccountType =calendarAccountType;
	}
	
	/**
	 * @return the calendarId
	 */
	public long getCalendarId() {
		return calendarID;
	}
	
	/**
	 * @param calendarId the calendarId to set
	 */
	public void setCalendarId(long calendarId) {
		this.calendarID = calendarId;
	}
	
	
	/**
	 * @return the calendarDisplayName
	 */
	public String getCalendarDisplayName() {
		return calendarDisplayName;
	}
	
	
	/**
	 * @param calendarDisplayName the calendarDisplayName to set
	 */
	public void setCalendarDisplayName(String calendarDisplayName) {
		this.calendarDisplayName = calendarDisplayName;
	}
	/**
	 * @return the calendarAccountName
	 */
	public String getCalendarAccountName() {
		return calendarAccountName;
	}
	/**
	 * @param calendarAccountName the calendarAccountName to set
	 */
	public void setCalendarAccountName(String calendarAccountName) {
		this.calendarAccountName = calendarAccountName;
	}
	/**
	 * @return the calendarAccountType
	 */
	public String getCalendarAccountType() {
		return calendarAccountType;
	}
	/**
	 * @param calendarAccountType the calendarAccountType to set
	 */
	public void setCalendarAccountType(String calendarAccountType) {
		this.calendarAccountType = calendarAccountType;
	}
	

}
