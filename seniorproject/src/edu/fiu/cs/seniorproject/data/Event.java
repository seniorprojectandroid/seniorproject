package edu.fiu.cs.seniorproject.data;

public class Event {
	
	private String name =null;
	private String time = null;
	private String desscription = null;
	private Location location = null;
	private SourceType source = SourceType.GOOGLE_PLACE;
	
	public Event()
	{
		
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesscription() {
		return desscription;
	}

	public void setDesscription(String desscription) {
		this.desscription = desscription;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public SourceType getSource() {
		return source;
	}

	public void setSource(SourceType source) {
		this.source = source;
	}

}
