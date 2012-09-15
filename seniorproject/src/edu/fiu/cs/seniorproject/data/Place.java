package edu.fiu.cs.seniorproject.data;

public class Place {
	
	private String name = null;
	private String location = null;
	private String description = null;
	
	public Place()
	{
		
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
