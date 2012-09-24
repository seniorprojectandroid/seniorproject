package edu.fiu.cs.seniorproject.data.provider;

import java.util.List;


import javax.xml.parsers.ParserConfigurationException;

import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;



public abstract class DataProvider {
	 
	 public abstract List<Event> getEventList(Location location, String category, String radius, String query) throws ParserConfigurationException;
	 
	 public abstract List<Place> getPlaceList(Location location, String category, String radius, String query);
	 
	 public abstract Event getEventDetails(String eventId);
	 
	 public abstract Place getPlaceDetails(String placeId);

}
