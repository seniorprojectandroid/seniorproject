package edu.fiu.cs.seniorproject.data;

public class Location 
{
	private String latitude = null;
	private String longitude= null;
	private String address = null;
	
	public Location()
	{
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
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
