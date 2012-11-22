package edu.fiu.cs.seniorproject.data.provider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateFormat;

import edu.fiu.cs.seniorproject.client.EventfulRestClient;
import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.DateFilter;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.EventCategoryFilter;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.PlaceCategoryFilter;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.utils.DateUtils;
import edu.fiu.cs.seniorproject.utils.Logger;

public class EventFullProvider extends DataProvider
{
	private EventfulRestClient myRestClient;
	
	//private final static String IMAGE_BASE_URL = "http://www.eventful.com";	
	
	private int currentPage = 1;
	private Location currentLocation = null;
	private String currentRadius = null;
	private DateFilter currentDateFilter = null;
	private EventCategoryFilter currentEventCategory = null;
	private PlaceCategoryFilter currentPlaceFilter = null;
	
	 public EventFullProvider()
	 {
		 this.myRestClient = new EventfulRestClient(AppConfig.EVENTFUL_APP_ID);
	 }
	 
	@Override
	public List<Event> getEventList(Location location, EventCategoryFilter category, String radius, String query, DateFilter date ) 
	{
		this.currentPage = 1;
		this.currentLocation = location;
		this.currentEventCategory = category;
		this.currentRadius = radius;
		this.currentDateFilter = date;
			
		 List<Event> myEventList = null;
		 String myListRequestClient = this.myRestClient.getEventList(query, location, getDatesFromFilter(date), getEventCategory(category), (int)Math.ceil(Double.valueOf(radius)), 1, this.eventPageSize ); 
		 if ( myListRequestClient != null && !myListRequestClient.isEmpty() )
		 {
				myEventList = this.parseEventList(myListRequestClient);
		 }
		 return myEventList;
	}
	
	@Override
	public List<Event> getNextEventPage() {
		this.currentPage++;
		List<Event> myEventList = null;
		 String myListRequestClient = this.myRestClient.getEventList(null, this.currentLocation, getDatesFromFilter(this.currentDateFilter), getEventCategory(this.currentEventCategory), (int)Math.ceil(Double.valueOf(this.currentRadius)), this.currentPage, this.eventPageSize); 
		 if ( myListRequestClient != null && !myListRequestClient.isEmpty() )
		 {
				myEventList = this.parseEventList(myListRequestClient);
		 }
		 return myEventList;
	}
	
	@Override
	public Event getEventDetails(String eventId, String reference) {
		String eventStr = this.myRestClient.getEventDetails(eventId);
		
		Event event = null;
		
		if ( eventStr != null && !eventStr.isEmpty() ) {
			try {
				JSONObject json = new JSONObject(eventStr);
				if ( json!= null ) {
					event = this.parseEvent(json);
				}
			} catch (JSONException e) {
				event = null;
				Logger.Warning("Exception decoding json from eventful " + e.getMessage() );
			}
			
		}
		return event;
	}
	 
	@Override
	public List<Place> getPlaceList(Location location, PlaceCategoryFilter category, String radius, String query)
	{	
		this.currentPage = 1;
		this.currentLocation = location;
		this.currentPlaceFilter = category;
		this.currentRadius = radius;
		
		List<Place> myPlaceList = null;
		
		String keywords = query != null ? query : this.getPlaceCategory(category);
		String myListRequestClient = this.myRestClient.getPlaceList(keywords, location, this.placePageSize, 1, Integer.valueOf(radius) );
		
		if ( myListRequestClient != null && !myListRequestClient.isEmpty() )
		{
			myPlaceList = this.parsePlaceList(myListRequestClient);
		}
		return myPlaceList;
	}
	
	@Override
	public List<Place> getNextPlacePage() {
		this.currentPage++;
		List<Place> myPlaceList = null;
		
		String keywords = this.getPlaceCategory( this.currentPlaceFilter);
		String myListRequestClient = this.myRestClient.getPlaceList(keywords, this.currentLocation, this.placePageSize, this.currentPage, Integer.valueOf(this.currentRadius) );
		
		if ( myListRequestClient != null && !myListRequestClient.isEmpty() )
		{
			myPlaceList = this.parsePlaceList(myListRequestClient);
		}
		return myPlaceList;
	}
	
	@Override
	public Place getPlaceDetails(String placeId) {
		Place place = null;
		String placeStr = this.myRestClient.getPlaceDetails(placeId);
		
		if ( placeStr != null && !placeStr.isEmpty() ) {
			try {
				JSONObject json = new JSONObject(placeStr);
				if ( json != null ) {
					place = this.parsePlace(json);
				}
			} catch (JSONException e ) {
				place = null;
				Logger.Error("Exception decoding place from eventful " + e.getMessage());
			}
		}
		return place;
	}

	@Override
	public SourceType getSource() {
		return SourceType.EVENTFUL;
	}
	
	private List<Event> parseEventList(String eventList) {
		List<Event> myEventList = null;
		if ( eventList != null && !eventList.isEmpty() )
		 {
				try {
					JSONObject eventsObject = new JSONObject(eventList);
					if ( eventsObject != null && eventsObject.has("events") && !eventsObject.isNull("events") )
					{
						JSONObject events = eventsObject.getJSONObject("events");
						
						if ( events != null && events.has("event") && !events.isNull("event")) {
							JSONArray jsonEventList = null;
							
							try {
								jsonEventList = events.getJSONArray("event");
							} catch (JSONException e) {
								jsonEventList = new JSONArray();
								jsonEventList.put(events.getJSONObject("event"));
							}
							
							if ( jsonEventList != null && jsonEventList.length() > 0 )
							{
								myEventList = new LinkedList<Event>();
								
								for( int i = 0; i < jsonEventList.length(); i++ )
								{
									JSONObject iter = jsonEventList.getJSONObject(i);
									
									Event event = this.parseEvent(iter);
									if ( event != null ) {
										myEventList.add(event);
									}
								}
							}
						}
					}
				}
				catch (JSONException e)
				{
					Logger.Error("Exception decoding json object in MBVCA " );
					e.printStackTrace();
				}
		 }
		return myEventList;
	}
	
	private List<Place> parsePlaceList(String placeList) {
		List<Place> myPlaceList = null;
		if ( placeList != null && !placeList.isEmpty() )
		{
			try {
				JSONObject placesObject = new JSONObject(placeList);
				if ( placesObject != null && placesObject.has("venues") && !placesObject.isNull("venues"))
				{
					JSONObject venues = placesObject.getJSONObject("venues");
					JSONArray jsonPlaceList = null;// ? venues.getJSONArray("venue") : null;
				
					if (venues != null && venues.has("venue") && !venues.isNull("venue") ) {
						try {
							jsonPlaceList = venues.getJSONArray("venue");
						} catch (JSONException e ) {
							// try to get an object
							JSONObject aux = venues.getJSONObject("venue");
							if ( aux != null ) {
								jsonPlaceList = new JSONArray();
								jsonPlaceList.put(aux);
							}
						}
					}
					
					if ( jsonPlaceList != null && jsonPlaceList.length() > 0 )
					{
						myPlaceList = new LinkedList<Place>();
						
						for( int i = 0; i < jsonPlaceList.length(); i++ )
						{
							JSONObject iter = jsonPlaceList.getJSONObject(i);
							
							Place place = this.parsePlace(iter);
							if ( place != null ) {
								myPlaceList.add(place);
							}
						}
					}
				}
			}
			catch (JSONException e)
			{
				Logger.Error("Exception decoding json object in MBVCA " );
				e.printStackTrace();
			}
		}
		return myPlaceList;
	}
	
	private Event parseEvent( JSONObject iter ) {
		Event event = null;
		
		try {
			if ( iter != null && iter.has("id") && iter.has("title"))
			{
				event = new Event();
				event.setId(iter.getString("id"));
				event.setName(iter.getString("title"));
				
				if ( iter.has("description"))
				{
					event.setDescription(iter.getString("description"));
				}
				
				// Process the location
				if ( iter.has("latitude") && iter.has("longitude"))
				{
					Location eventLocation = new Location(iter.getString("latitude"),iter.getString("longitude") );
					
					StringBuilder myAddress = new StringBuilder(110);	
					
					if ( iter.has("venue_address") && !iter.isNull("venue_address") )
					{
						myAddress.append(iter.getString("venue_address"));
					} else if ( iter.has("address") && !iter.isNull("address"))
					{
						myAddress.append(iter.getString("address"));
					}
						
					if ( iter.has("city_name") && !iter.isNull("city_name"))
					{
						if ( myAddress.length() > 0 ) myAddress.append(",");
						myAddress.append(iter.getString("city_name"));
					}
					
					if( iter.has("region_abbr") && !iter.isNull("region_abbr"))
					{
						if ( myAddress.length() > 0 ) myAddress.append(",");
						myAddress.append(iter.getString("region_abbr"));
					}
					
					if( iter.has("postal_code") && !iter.isNull("postal_code"))
					{
						if ( myAddress.length() > 0 ) myAddress.append(",");
						myAddress.append(iter.getString("postal_code"));
					}
					
					eventLocation.setAddress(myAddress.toString());
					event.setLocation(eventLocation);										
				}//set the location
	
				if ( iter.has("start_time"))
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date;
					try {
						date = sdf.parse(iter.getString("start_time"));
						event.setTime(String.valueOf(date.getTime()/1000L));
					} catch (ParseException e) {											
						e.printStackTrace();
					}										
				}
				
				JSONObject imageObject = null;
				if ( iter.has("images") && !iter.isNull("images")) {
					imageObject = iter.getJSONObject("images");
					
					if ( imageObject.has("image") && !imageObject.isNull("image")) {
						
						try {
							JSONArray imageList = imageObject.getJSONArray("image");
							if ( imageList != null && imageList.length() > 0 ) {
								imageObject = imageList.getJSONObject(0);
							} 
						} catch (JSONException e) {
							imageObject = imageObject.getJSONObject("image");
						}
					}
				} else if ( iter.has("image") && !iter.isNull("image")) {
					imageObject = iter.getJSONObject("image");
				}
				
				if ( imageObject != null ) {
					if(imageObject.has("medium") && !imageObject.isNull("medium"))
					{
						JSONObject medium = imageObject.getJSONObject("medium");
						if (medium != null && medium.has("url") && !medium.isNull("url") ) {
							event.setImage( medium.getString("url") );
						}
					}
					else if(imageObject.has("url") && !imageObject.isNull("url")) {
						event.setImage( imageObject.getString("url"));
					}
				}
				
				event.setSource(SourceType.EVENTFUL);
			}
		} catch ( JSONException e ) {
			Logger.Warning("exception decoding json from eventful " + e.getMessage() );
		}
		return event;
	}
	
	private Place parsePlace( JSONObject iter )
	{
		Place place = null;
		try {
			if ( iter != null && iter.has("id") && iter.has("name"))
			{
				place = new Place();
				place.setId(iter.getString("id"));
				place.setSource(SourceType.EVENTFUL);
				place.setName(iter.getString("name"));	
				
				// Process the location
				if ( iter.has("latitude") && iter.has("longitude"))
				{
					Location placeLocation = new Location(iter.getString("latitude"),iter.getString("longitude") );
					
					StringBuilder myAddress = new StringBuilder(110);	
					
					if ( iter.has("venue_address") && !iter.isNull("venue_address") )
					{
						myAddress.append(iter.getString("venue_address"));
					} else if ( iter.has("address") && !iter.isNull("address"))
					{
						myAddress.append(iter.getString("address"));
					}
						
					if ( iter.has("city_name") && !iter.isNull("city_name"))
					{
						if ( myAddress.length() > 0 ) myAddress.append(",");
						myAddress.append(iter.getString("city_name"));
					}
					
					if( iter.has("region_abbr") && !iter.isNull("region_abbr"))
					{
						if ( myAddress.length() > 0 ) myAddress.append(",");
						myAddress.append(iter.getString("region_abbr"));
					}
					
					if( iter.has("postal_code") && !iter.isNull("postal_code"))
					{
						if ( myAddress.length() > 0 ) myAddress.append(",");
						myAddress.append(iter.getString("postal_code"));
					}
					
					placeLocation.setAddress(myAddress.toString());
					place.setLocation(placeLocation);										
				}//set the location

				
				if ( iter.has("description") && iter.isNull("description"))
				{
					place.setDescription(iter.getString("description"));
				}
				
				
				// set eventlist at place
				if ( iter.has("events") && !iter.isNull("events") )
				{
					JSONObject events = iter.getJSONObject("events");
					JSONArray jsonEventList = null;
					
					try {
						jsonEventList = events.getJSONArray("event");
					} catch (JSONException e) {
						jsonEventList = new JSONArray();
						jsonEventList.put( events.getJSONObject("event") );
					}
					
					List<Event> myEventList = null;
					
					if ( jsonEventList != null && jsonEventList.length() > 0 )
					{
						
						myEventList = new LinkedList<Event>();
						
						for( int i = 0; i < jsonEventList.length(); i++ )
						{
							JSONObject jsonEvent = jsonEventList.getJSONObject(i);
							
							Event event = this.parseEvent(jsonEvent);
							
							if ( event != null )
							{
								myEventList.add(event);
							}
						}// end for
					}
					
					place.setEventsAtPlace(myEventList);
				}// end if
				
			}
		} catch (JSONException e ) {
			place = null;
			Logger.Error("Exception decoding place from eventful " + e.getMessage() );
		}
		return place;
	}
	
	@Override
	protected String getEventCategory(EventCategoryFilter filter) {
		String result = null;
		if ( filter != null && filter != EventCategoryFilter.NONE ) {
			final String[] categoryList = new String[]{ "art", "business,technology", "others", "community", "music", "learning_education", "festivals_parades", "food", "music", "others", "performing_arts", "others", "outdoors_recreation", "art,performing_arts" };
			int index = filter.ordinal();
			
			if ( index < categoryList.length ) {
				result = categoryList[index];
			}
		}
		return result;
	}
	
	@Override
	protected String getPlaceCategory( PlaceCategoryFilter filter ) {
		 String result = null;
		 
		 if ( filter != null ) {
			 result = filter.toString().toLowerCase().replace('_', ' ');
		 }
		 return result;
	}
	
	protected String getDatesFromFilter(DateFilter filter) {
		String result = "All";
		
		switch ( filter ) {
		case TODAY:
			result = "Today";
			break;
		case THIS_WEEK:
			result = "This Week";
			break;
			
		case THIS_WEEKEND:
			long thisWeekend = DateUtils.getThisWeekendInMiliseconds();
			String start = DateFormat.format("yyyyMMdd", thisWeekend ).toString();	
			String end = DateFormat.format("yyyyMMdd", thisWeekend + DateUtils.ONE_DAY * 1000 ).toString();	
			result = start + "00-" + end + "23";
			break;
			
		case NEXT_WEEKEND:
			long nextWeekend = DateUtils.getNextWeekendInMiliseconds();
			String nextStart = DateFormat.format("yyyyMMdd", nextWeekend ).toString();	
			String nextEnd = DateFormat.format("yyyyMMdd", nextWeekend + DateUtils.ONE_DAY * 1000 ).toString();	
			result = nextStart + "00-" + nextEnd + "23";
			break;
			
		case NEXT_30_DAYS:
			long today = DateUtils.getTodayTimeInMiliseconds();
			String nextMonthStart = DateFormat.format("yyyyMMdd", today ).toString();	
			String nextMonthEnd = DateFormat.format("yyyyMMdd", today + DateUtils.ONE_DAY * 1000 ).toString();	
			result = nextMonthStart + "00-" + nextMonthEnd + "23";
			break;
			
		default:
				result="All";
		}
		return result;
	}
}// EventFullProvider



//http://api.eventful.com/json/venues/get?app_key=HrsPRcW3W49b6hZq&id=V0-001-000104270-1

//http://api.eventful.com/rest/venues/get?app_key=HrsPRcW3W49b6hZq&id=V0-001-000104270-1

