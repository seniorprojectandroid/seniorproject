package edu.fiu.cs.seniorproject.data;

public class Event
{
	
	private String id = null;
	private String name =null;
	private String time = null;
	private String description = null;
	private Location location = null;
	private String image = null;
	private SourceType source = SourceType.GOOGLE_PLACE;
	private String url = null;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location != null && location.getLatitude() != null && location.getLongitude() != null ? location : null;
	}

	public SourceType getSource() {
		return source;
	}

	public void setSource(SourceType source) {
		this.source = source;
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
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Event id=" + this.id + " name=" + this.name + " " + super.toString();
	}
}
