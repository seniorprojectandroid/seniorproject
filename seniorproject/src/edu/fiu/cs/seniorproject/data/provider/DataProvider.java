package edu.fiu.cs.seniorproject.data.provider;

import java.util.List;

<<<<<<< HEAD
import edu.fiu.cs.seniorproject.data.Location;

=======
>>>>>>> refs/remotes/origin/master
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;

public abstract class DataProvider {
	 
	 public abstract List<Event> getEventList(Location location, String category, String radius, String query);
	 
	 public abstract List<Place> getPlaceList(Location location, String category, String radius, String query);
	 
	 public abstract Event getEventDetails(String eventId);
	 
	 public abstract Place getPlaceDetails(String placeId, String reference);

	 public abstract SourceType getSource();
	 
	 public boolean supportEvents() {
		 return true;
	 }
	 
	 public boolean supportPlaces() {
		 return true;
	 }
}
