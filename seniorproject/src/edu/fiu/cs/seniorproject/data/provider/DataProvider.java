package edu.fiu.cs.seniorproject.data.provider;

import java.util.List;

import edu.fiu.cs.seniorproject.data.DateFilter;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.EventCategoryFilter;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.PlaceCategoryFilter;
import edu.fiu.cs.seniorproject.data.SourceType;

public abstract class DataProvider {
	 
	 protected boolean enabled = true;
	 protected int eventPageSize = 10;
	 protected int placePageSize = 10;
	 
	 public abstract List<Event> getEventList(Location location, EventCategoryFilter category, String radius, String query, DateFilter date);
	 
	 public abstract List<Place> getPlaceList(Location location, PlaceCategoryFilter category, String radius, String query);
	 
	 public abstract Event getEventDetails(String eventId, String reference);
	 
	 public abstract Place getPlaceDetails( String placeId );

	 public abstract SourceType getSource();
	 
	 public List<Event> getNextEventPage() {
		 return null;
	 }
	 
	 public List<Place> getNextPlacePage() {
		 return null;
	 }
	 
	 public void setEventPageSize(int size) {
		 this.eventPageSize = size;
	 }
	 
	 public void setPlacePageSize(int size) {
		 this.placePageSize = size;
	 }
	 
	 public boolean supportEvents() {
		 return true;
	 }
	 
	 public boolean supportPlaces() {
		 return true;
	 }
	 
	 protected String getEventCategory( EventCategoryFilter filter ) {
		 return filter != null ? String.valueOf(filter.Value()) : null;
	 }
	 
	 protected String getPlaceCategory( PlaceCategoryFilter filter ) {
		 return filter != null ? String.valueOf(filter.Value()) : null;
	 }
	 
	 public boolean isEnabled() {
		 return this.enabled;
	 }
	 
	 public void setEnabled( boolean value ) {
		 this.enabled = value;
	 }
}
