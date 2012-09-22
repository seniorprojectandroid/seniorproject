package edu.fiu.cs.seniorproject.data.provider;

import java.util.List;

import android.location.Location;

import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Place;



public abstract class DataProvider {
	 
	 public abstract List<Event> getEventList(Location location, String category, String radius, String query);
	 
	 public abstract List<Place> getPlaceList(Location location, String category, String radius, String query);
	 
	 public abstract Event getEventDetails(String eventId);
	 
	 public abstract Place getPlaceDetails(String placeId);

}
