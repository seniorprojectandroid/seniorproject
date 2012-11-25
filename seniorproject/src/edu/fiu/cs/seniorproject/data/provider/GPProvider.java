package edu.fiu.cs.seniorproject.data.provider;

import java.io.IOException;
//import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.LinkedList;
//import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.provider.ContactsContract.CommonDataKinds.Event;

import edu.fiu.cs.seniorproject.client.GPClient;
import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.DateFilter;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.EventCategoryFilter;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.PlaceCategoryFilter;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.utils.Logger;

public class GPProvider extends DataProvider {

	private GPClient gpClient;

	private String nextPageToken = null;
	
	Hashtable<String,String> categories = null;
	public GPProvider() {
		this.gpClient = new GPClient(AppConfig.GOOGLE_PLACE_APP_ID);
		this.categories =  new Hashtable<String,String>();
	}

	private String getCategory(String category)
	{
		if(!this.getCategories().containsKey(category))
			Logger.Error("this category: " + category + " does not exist.");
		
		
		return this.getCategories().get(category);
	}
	
	private Hashtable<String,String> getCategories()
	{
		this.categories.put(PlaceCategoryFilter.RESTAURANT_BARS.toString(), "restaurant|bar");
		this.categories.put(PlaceCategoryFilter.HOTEL.toString(), "lodging");
		this.categories.put(PlaceCategoryFilter.BAKERY.toString(), "bakery");
		this.categories.put(PlaceCategoryFilter.DENTISTS.toString(), "dentist");
		this.categories.put(PlaceCategoryFilter.FOOD_SALES.toString(), "food");
		this.categories.put(PlaceCategoryFilter.GALLERY_ART.toString(), "art_gallery");
		this.categories.put(PlaceCategoryFilter.LIQUOR_SALES.toString(), "liquor_store");
		this.categories.put(PlaceCategoryFilter.LOCKSMITH.toString(), "locksmith");
		this.categories.put(PlaceCategoryFilter.PARKING_GARAGE.toString(), "parking");
		this.categories.put(PlaceCategoryFilter.PARKING_LOT.toString(), "parking");
		this.categories.put(PlaceCategoryFilter.PARKING_LOT_PROVISIONAL.toString(), "parking");
		this.categories.put(PlaceCategoryFilter.PARKING_LOT_SELF_PARKING.toString(), "parking");
		this.categories.put(PlaceCategoryFilter.PARKING_LOT_TEMPORARY.toString(), "parking");
		this.categories.put(PlaceCategoryFilter.PARKING_LOT_UNDERUTILIZED.toString(), "parking");
		this.categories.put(PlaceCategoryFilter.PARKING_LOT_VALET.toString(), "parking");
		this.categories.put(PlaceCategoryFilter.PHARMACY.toString(), "pharmacy");
		
		return this.categories;
	}
	
	//accounting
	//airport
	//amusement_park
	//aquarium
	//art_gallery
	//atm
	//bakery
	//bank
	//bar
	//beauty_salon
	//bicycle_store
	//book_store
	//bowling_alley
	//bus_station
	//cafe
	//campground
	//car_dealer
	//car_rental
	//car_repair
	//car_wash
	//casino
	//cemetery
	//church
	//city_hall
	//clothing_store
	//convenience_store
	//courthouse
	//dentist
	//department_store
	//doctor
	//electrician
	//electronics_store
	//embassy
	//establishment
	//finance
	//fire_station
	//florist
	//food
	//funeral_home
	//furniture_store
	//gas_station
	//general_contractor
	//grocery_or_supermarket
	//gym
	//hair_care
	//hardware_store
	//health
	//hindu_temple
	//home_goods_store
	//hospital
	//insurance_agency
	//jewelry_store
	//laundry
	//lawyer
	//library
	//liquor_store
	//local_government_office
	//locksmith
	//lodging
	//meal_delivery
	//meal_takeaway
	//mosque
	//movie_rental
	//movie_theater
	//moving_company
	//museum
	//night_club
	//painter
	//park
	//parking
	//pet_store
	//pharmacy
	//physiotherapist
	//place_of_worship
	//plumber
	//police
	//post_office
	//real_estate_agency
	//restaurant
	//roofing_contractor
	//rv_park
	//school
	//shoe_store
	//shopping_mall
	//spa
	//stadium
	//storage
	//store
	//subway_station
	//synagogue
	//taxi_stand
	//train_station
	//travel_agency
	//university
	//veterinary_care
	//zoo
	@Override
	public Event getEventDetails(String eventId, String reference) {
		JSONObject data = null;
		JSONObject results;
		String result = null;
		Event event = null;

		result = this.getEventDet(eventId, reference);

		if (result != null && result.length() > 0) {
			try {
				data = new JSONObject(result);

				if (data != null && data.has("result")) 
				{
					event = new Event();
					results = data.getJSONObject("result");
					
					if(results != null)
					{
						String startTime = results.getString("start_time");
						
						if(startTime != null)
						{
							event.setTime(startTime);
						}
						
						String summary = results.getString("summary");
						
						if(summary != null)
						{
							event.setDescription(summary);
						}
						event.setSource(this.getSource());
					}
				} else {
					Logger.Error("parseEvent: invalid data.");
				}

			} catch (JSONException e) {
				Logger.Error("JSONException in parseEvent");
			}
		} else {
			Logger.Error("parseEvent: invalid result.");
		}
		
		event.setSource(SourceType.GOOGLE_PLACE);
		
		return event;
	}


	@Override
	public List<Event> getEventList(Location location, EventCategoryFilter category,
			String radius, String query, DateFilter date) {
		
		String result = null;
		JSONObject data = null;
		JSONArray jsonArray = null;
		LinkedList<Event> eventList = new LinkedList<Event>();
		JSONObject eachPlace = null;
		Event event = null;
		Location loc = null;

		String radiusInMeters = String.valueOf( Double.valueOf(radius) * 1609.34 );
		result = getPlaces(location, null, radiusInMeters, query);

		if (result != null && result.length() > 0) {
			try {
				data = new JSONObject(result);
				
				if ( data != null && data.has("results") && !data.isNull("results")) {
					jsonArray = data.getJSONArray("results");
				}

			} catch (JSONException e) {
				Logger.Error("Exception decoding json on Gp get event list ");
			}

			if ( jsonArray != null && jsonArray.length() > 0 ) {
				for (int i = 0; i < jsonArray.length(); i++) {
					event = new Event();
					loc = new Location();
					JSONObject singleEvent;
	
					try {
						eachPlace = jsonArray.getJSONObject(i);
						
						if (eachPlace != null && eachPlace.has("events"))
						{
							singleEvent = eachPlace.getJSONArray("events").getJSONObject(0);
							
							if(singleEvent != null)
							{
								String eventId = singleEvent.getString("event_id"); 
								
								if(eventId != null)
								{
									event.setId(eventId);
								}
								
								String summary = singleEvent.getString("summary");
								
								if(summary != null)
								{
									event.setDescription(summary);
								}
								//singleEvent.getString("url");
								
								String name = eachPlace.getString("name");
								
								if(name != null)
								{
									event.setName(name);
								}
								
								String icon = eachPlace.getString("icon");
								
								if(icon != null )
								{
									event.setImage(icon);
								}
								
								JSONObject geometry = eachPlace.getJSONObject("geometry");
								
								if(geometry != null && geometry.has("location"))
								{
									JSONObject ltn = geometry.getJSONObject("location");
									
									if(ltn != null && ltn.has("lat") && ltn.has("lng"))
									{
										loc.setLatitude(ltn.getString("lat"));
										loc.setLongitude(ltn.getString("lng"));
									}
								}
								
								String formattedAddress = eachPlace.getString("formatted_address");
								
								if(formattedAddress != null)
								{
									loc.setAddress(formattedAddress);
								}
								
								event.setLocation(loc);
								//event.setTime(time)
								event.setSource(this.getSource());
								
								event.setSource(SourceType.GOOGLE_PLACE);
								
								eventList.add(event);
							}
						}
	
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Logger.Error("");
		}

		
		return eventList;
	}
	
	@Override
	public Place getPlaceDetails(String reference) {

		JSONObject data = null;
		JSONObject results;
		JSONObject resultLocation;
		String result = null;
		Place place = null;
		Location locat = new Location();;

		if(reference != null)
		{
			result = this.getPlaceDet(reference);
		}
		else
		{
			Logger.Error("getPlaceDetails: reference not being passed in:"+ reference );
		}

		if (result != null && result.length() > 0) {
			try {
				data = new JSONObject(result);

				if (data != null && data.has("result")) {
					place = new Place();
					place.setSource(SourceType.GOOGLE_PLACE);
					results = data.getJSONObject("result");
					
					if(results != null)
					{
						if(results.has("formatted_address"))
						{
							String address = results.getString("formatted_address");
						
							if(address != null)
							{
								locat.setAddress(address);
							}
						}
						
						if(results.has("icon") && results.getString("icon") != null)
						{
							place.setImage(results.getString("icon"));
						}
						
						//results.getString("formatted_phone_number");
	
						if(results.has("name"))
						{
							String name = results.getString("name");
							
							if(name != null)
							{
								place.setName(name);
							}
						}
						// rating
						// results.getString("rating");
	
						if(results.has("geometry"))
						{
							JSONObject geometry = results.getJSONObject("geometry");
							
							if(geometry != null && geometry.has("location"))
							{
								resultLocation = geometry.getJSONObject("location");
								
								if(resultLocation != null && resultLocation.has("lng") && resultLocation.has("lat"))
								{
									String longitude = resultLocation.getString("lng");
									
									if(longitude != null)
									{
										locat.setLongitude(longitude);
									}
									
									String latitude = resultLocation.getString("lat");
									
									if(latitude != null)
									{
										locat.setLatitude(latitude);
									}
									
									place.setLocation(locat);
								}
							}
						}
						
						if(results.has("website"))
						{
							String web = results.getString("website");
							
							if(web != null)
							{
								place.setWebsite(web);
							}
						}
						
						if (results.has("events") && !results.isNull("events"))
						{
							
							JSONArray jsonEventList = results.getJSONArray("events");
							
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
						}
							
					}
				} else {
					Logger.Error("parsePlaceDetails: invalid data.");
				}

			} catch (JSONException e) {
				Logger.Error("JSONException in parsePlaceDet");
			}
		} else {
			Logger.Error("parsePlaceDet: invalid result.");
		}
		
		place.setSource(SourceType.GOOGLE_PLACE);
		
		return place;

	}
	
	
	private Event parseEvent( JSONObject iter ) {
		Event event = null;
		
		try {
			if ( iter != null && iter.has("event_id") && iter.has("summary"))
			{
				event = new Event();
				event.setId(iter.getString("event_id"));
				event.setName(iter.getString("summary"));
				
				if ( iter.has("url")&&!iter.getString("url").isEmpty() && !iter.getString("url").equals("null"))
				{
					event.setUrl(iter.getString("url"));
				}
				
				
				event.setSource(SourceType.GOOGLE_PLACE);
			}
		} catch ( JSONException e ) {
			Logger.Warning("exception decoding json from eventful " + e.getMessage() );
		}
		return event;
	}
	

	@Override
	public List<Place> getPlaceList(Location location, PlaceCategoryFilter category,
			String radius, String query) {

		String result = null;
		JSONObject data = null;
		JSONArray jsonArray = null;
		LinkedList<Place> placeList = new LinkedList<Place>();
		JSONObject eachPlace = null;
		Place place = null;
		Location loc = null;

		String categ = this.getCategory(category.toString());
		
		String radiusInMeters = String.valueOf( Double.valueOf(radius) * 1609.34 );
		result = getPlaces(location, categ, radiusInMeters, query);

		if (result != null && !result.isEmpty() ) 
		{
			try {
				data = new JSONObject(result);
				
				if(data.has("results"))
				{
					jsonArray = data.getJSONArray("results");
					
					//Logger.Error("JSON ARRAY: "+jsonArray.toString()+ " category: "+category.toString());
					
					if(data.has("next_page_token"))
					{
						this.setNextToken(data.getString("next_page_token"));
					}else
					{
						this.setNextToken(null);
						Logger.Error("Page token empty ");
					}
				}
				else
				{
					Logger.Error("Results are empty ");
				}
			

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(jsonArray != null && jsonArray.length() > 0)
			{	
				for (int i = 0; i < jsonArray.length(); i++) 
				{
					place = new Place();
					loc = new Location();
	
					try {
						eachPlace = jsonArray.getJSONObject(i);
	
						if (eachPlace != null && eachPlace.has("reference") ) 
						{
							
							if(eachPlace.has("vicinity") && eachPlace.getString("vicinity") != null)
							{
								loc.setAddress(eachPlace.getString("vicinity"));	
							}
							

							JSONObject geometry = eachPlace.getJSONObject("geometry");
							
							if(geometry != null && geometry.has("location"))
							{
								JSONObject ltn = geometry.getJSONObject("location");
								
								if(ltn != null && ltn.has("lat") && ltn.has("lng"))
								{
									String latitude = ltn.getString("lat");
									
									if(latitude != null)
									{
										loc.setLatitude(latitude);
									}
									
									String longitude = ltn.getString("lng");
									
									if(longitude != null)
									{
										loc.setLongitude(longitude);
									}
								}
								
							}
							
							if(eachPlace.has("name") && eachPlace.getString("name") != null)
							{
								place.setName(eachPlace.getString("name"));
							}
							
							if( eachPlace.getString("reference") != null)
							{
								place.setId(eachPlace.getString("reference"));
							} 
							if(eachPlace.has("icon") && eachPlace.getString("icon") != null)
							{
								place.setImage(eachPlace.getString("icon"));
							}
							
							place.setLocation(loc);
							place.setSource(SourceType.GOOGLE_PLACE);
							placeList.add(place);
							
						} else {
							Logger.Error("");
						}
	
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Logger.Error("Results empty");
		}

		return placeList;
	}

	private String getPlaces(Location location, String category, String radius,
			String query) {
		String result = null;

		try {
			result = this.gpClient.getPlaces(location, category, radius, query);
		} catch (MalformedURLException e) {
			Logger.Error("MalformedURLException in parseEvent");
		} catch (IOException e) {
			Logger.Error("IOException in parseEvent");
		}

		return result;

	}

	private String getPlaceDet(String reference) {
		String result = null;

		try {
			result = this.gpClient.getPlaceDetails(reference);
		} catch (MalformedURLException e) {
			Logger.Error("MalformedURLException in parseEvent");
		} catch (IOException e) {
			Logger.Error("IOException in parseEvent");
		}

		return result;
	}
	
	private String getEventDet(String eventId , String reference)
	{
		String result = null;
		
		result = this.gpClient.getEventDet(eventId, reference);

		return result;
	}
	
	private String getNextPlaces(String nextPageToken)
	{
		String results = null;
		
		results = this.gpClient.getNextPlaces(nextPageToken);
		
		return results;
	}

	@Override
	public boolean supportEvents() {
		return true;
	}

	@Override
	public SourceType getSource() {
		return SourceType.GOOGLE_PLACE;
	}	 
	 
	 protected String getPlaceCategory( PlaceCategoryFilter filter )
	 {
		 return filter != null ? this.getCategory(filter.toString()) : null;
	 }
	 
	 protected String getEventCategory( EventCategoryFilter filter ) {
		 return filter != null ? String.valueOf(filter.Value()) : null;
	}
	
	public String getNextToken()
	{
		return this.nextPageToken;
	}
	
	public void setNextToken(String nextPageToken)
	{
		this.nextPageToken = nextPageToken;
	}
	
	 public List<Event> getNextEventPage() {
		 return null;
	 }
	 
	 public List<Place> getNextPlacePage() 
	 {
		 	LinkedList<Place> placeList = new LinkedList<Place>();
		 	
		 	String result = null;
			JSONObject data = null;
			JSONArray jsonArray = null;
			JSONObject eachPlace = null;
			Place place = null;
			Location loc = null;
			
		 	if(this.getNextToken() != null)
		 		result = this.getNextPlaces(this.getNextToken());

			if (result != null && result.length() > 0) 
			{
				try {
					data = new JSONObject(result);
					jsonArray = data.getJSONArray("results");
					
					if(data.has("next_page_token"))
					{
						this.setNextToken(data.getString("next_page_token"));
					}
					else
					{
						this.setNextToken(null);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(jsonArray != null && jsonArray.length() > 0)
				{	
					for (int i = 0; i < jsonArray.length(); i++) 
					{
						place = new Place();
						loc = new Location();
		
						try {
							eachPlace = jsonArray.getJSONObject(i);
		
							if (eachPlace != null && eachPlace.has("reference") ) 
							{
								
								if(eachPlace.has("vicinity") && eachPlace.getString("vicinity") != null)
								{
									loc.setAddress(eachPlace.getString("vicinity"));	
								}
								

								JSONObject geometry = eachPlace.getJSONObject("geometry");
								
								if(geometry != null && geometry.has("location"))
								{
									JSONObject ltn = geometry.getJSONObject("location");
									
									if(ltn != null && ltn.has("lat") && ltn.has("lng"))
									{
										String latitude = ltn.getString("lat");
										
										if(latitude != null)
										{
											loc.setLatitude(latitude);
										}
										
										String longitude = ltn.getString("lng");
										
										if(longitude != null)
										{
											loc.setLongitude(longitude);
										}
									}
									
								}
								
								if(eachPlace.has("name") && eachPlace.getString("name") != null)
								{
									place.setName(eachPlace.getString("name"));
								}
								
								if( eachPlace.getString("reference") != null)
								{
									place.setId(eachPlace.getString("reference"));
								}
								
//								String id = eachPlace.getString("id");
//								
//								if(id != null)
//								{
//									place.setId(id);
//								}
							
								place.setLocation(loc);
								place.setSource(SourceType.GOOGLE_PLACE);
								placeList.add(place);
								
							} else {
								Logger.Error("");
							}
		
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				Logger.Error("");
			}

			return placeList;
		 
	 }

}
