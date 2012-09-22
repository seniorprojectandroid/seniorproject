package edu.fiu.cs.seniorproject.data.provider;

import java.util.LinkedList;

import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Place;



public interface DataProvider {
	
	 public String getEvent();
	 
	 public String getPlaces();
	 
	 public LinkedList<Event> getEventList(String query);
	 
	 public LinkedList<Place> getPlaceList(String query);
	 
	 public Event getEventDetails(String location, String eventId);
	 
	 public Place getPlaceDeetails(String location, String placeId);

}
