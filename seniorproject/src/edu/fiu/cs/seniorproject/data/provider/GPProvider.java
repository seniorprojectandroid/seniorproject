package edu.fiu.cs.seniorproject.data.provider;

import java.io.IOException;
//import java.lang.reflect.Array;
import java.net.MalformedURLException;
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

	public GPProvider() {
		this.gpClient = new GPClient(AppConfig.GOOGLE_PLACE_APP_ID);
	}

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
				jsonArray = data.getJSONArray("results");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
					} else {
						Logger.Error("empty place");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			Logger.Error("");
		}

		
		return eventList;
	}
	
	@Override
	public Place getPlaceDetails(String placeId, String reference) {

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
			Logger.Error("getPlaceDetails: reference not being passed in:"+ reference +"placeID: " + placeId );
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

		String radiusInMeters = String.valueOf( Double.valueOf(radius) * 1609.34 );
		result = getPlaces(location, getPlaceCategory(category), radiusInMeters, query);

		if (result != null && result.length() > 0) 
		{
			try {
				data = new JSONObject(result);
				jsonArray = data.getJSONArray("results");

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
	
						if (eachPlace != null) 
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
							
							if(eachPlace.has("reference") && eachPlace.getString("reference") != null)
							{
								place.setReference(eachPlace.getString("reference"));
							}
							
//							String id = eachPlace.getString("id");
//							
//							if(id != null)
//							{
//								place.setId(id);
//							}
						
							place.setLocation(loc);
							place.setSource(SourceType.GOOGLE_PLACE);
							placeList.add(place);
							
						} else {
							Logger.Error("");
						}
	
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
			Logger.Error("");
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

	@Override
	public boolean supportEvents() {
		return true;
	}

	@Override
	public SourceType getSource() {
		return SourceType.GOOGLE_PLACE;
	}

}
