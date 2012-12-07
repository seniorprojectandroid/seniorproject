package edu.fiu.cs.seniorproject.data;

import edu.fiu.cs.seniorproject.manager.AppLocationManager;

public class Location 
{
	private String latitude = null;
	private String longitude= null;
	private String address = null;
	
	public Location()
	{
		this(AppLocationManager.MIAMI_BEACH_LATITUDE, AppLocationManager.MIAMI_BEACH_LONGITUDE);
	}

	public Location(String lat, String lng) {
		this.latitude = lat;
		this.longitude = lng;
	}
	
	public String getLatitude() 
	{
		return latitude;
	}

	public void setLatitude(String latitude) 
	{
		this.latitude = (latitude != null && !latitude.equals("null")) ? latitude : null;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = (longitude != null && !longitude.equals("null")) ? longitude : null;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = (address != null && !address.equals("null")) ? address : null;
	}
	
	
}
