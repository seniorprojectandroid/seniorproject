package edu.fiu.cs.seniorproject.data;
import edu.fiu.cs.seniorproject.data.Location;

public class Place {
	
	private String id;
	private String name;
	private Location location;
	private String description;
	private String website;
	private String image;
	private SourceType source = SourceType.MBVCA;	// default source to Miami beach api

	public Place()
	{		
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
	
	public void setSource(SourceType source)
	{
		this.source = source;
	}
	
	public SourceType getSource()
	{
		return this.source;
	}

}
