package edu.fiu.cs.seniorproject.data;
import java.util.List;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.utils.Logger;

public class Place {
	
	public static String[] MIAMI_BEACH_ZIP_CODES = new String[]{ "33109", "33139", "33140", "33141", "33154" };
	
	private String id;
	private String name;
	private Location location;
	private String telephone;
	private String category;
	private String description;
	private String website;
	private String image;
	private String imageBase64;
	private String zipCode;
	private SourceType source = SourceType.MBVCA;	// default source to Miami beach api
	private List<Event> eventsAtPlace = null;

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
	
	public void setEventsAtPlace(List<Event> eventsAtPlace)
	{
		this.eventsAtPlace = eventsAtPlace;
	}
	
	public List<Event> getEventsAtPlace()
	{
		return this.eventsAtPlace;
	}
	
	public void setTelephone(String telep)
	{
		this.telephone = telep;
	}
	
	public String getTelephone() {
		return this.telephone;
	}
	
	public void setCategory(String cat)
	{
		this.category = cat;
	}
	
	public String getCategory() {
		return this.category;
	}

	@Override
	public String toString() {
		String l = this.location != null ? this.location.getLatitude() + ":" + this.location.getLongitude() + " addrees=" + this.location.getAddress() : "Unknow";
		return "Place id=" + this.id + " name=" + this.name + " loc=" + l + " " + super.toString();
	}

	public String getImageBase64() {
		return imageBase64;
	}

	public void setImageBase64(String imageBase64) {
		this.imageBase64 = imageBase64;
	}
	
	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	public static boolean IsInsideMiamiBeach( String zipCode ) {
		boolean isInside = false;
		
		if ( zipCode != null && !zipCode.isEmpty() ) {
			for( int i = 0; i < MIAMI_BEACH_ZIP_CODES.length; i++ ) {
				if ( zipCode.startsWith(MIAMI_BEACH_ZIP_CODES[i])) {
					isInside = true;
					break;
				}
			}
			
			if ( !isInside ) {
				Logger.Warning("Zip code rejected!!! code = " + zipCode );
			}
		}
		return isInside;
	}	
}
