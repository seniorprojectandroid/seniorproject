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
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.utils.Logger;

public class  GPProvider extends DataProvider {
	
	private GPClient gpClient;

	public GPProvider() {
		this.gpClient = new GPClient(AppConfig.GOOGLE_PLACE_APP_ID);
	}

	public Place getPlaceDetails(String reference, String eventId) {

		JSONObject data = null;
		JSONObject results;
		JSONObject resultLocation;
		String result = null;
		Place place = null;
		Location locat;
		
		result = this.getPlaceDet(reference, eventId);
		
		if(result != null && result.length() > 0)
		{
			try {
					data = new JSONObject(result);
					
				if(data != null && data.has("result"))
				{
					place = new Place();
					results = data.getJSONObject("result");
					
					
					//results.getString("formatted_address");
					//results.getString("formatted_phone_number");
					
					//name
					place.setName(results.getString("name"));
					
					//rating
					//results.getString("rating");
					
					resultLocation = results.getJSONObject("geometry").getJSONObject("location");

					locat = new Location();
					
					locat.setLongitude(resultLocation.getString("lng"));
					locat.setLatitude(resultLocation.getString("lat"));
					
					place.setLocation(locat);
					
					//website
					place.setWebsite(results.getString("website"));
					
				}
				else
				{
					Logger.Error("parseEvent: invalid data.");
				}
				
			} catch (JSONException e) {
				Logger.Error("JSONException in parseEvent");
			}	
		}
		else
		{
			Logger.Error("parseEvent: invalid result.");
		}
		return place;

	}

	
	@Override
	public List<Place> getPlaceList(Location location, String category, String radius, String query) {

		String result = null;
		JSONObject data = null;
		JSONArray jsonArray = null;
		LinkedList<Place> placeList = new LinkedList<Place>();
		JSONObject eachPlace = null;
		Place place = null;
		Location loc = null;
		
		result = getPlaces(location,category,radius,query);
		
	
		if(result !=null && result.length() > 0)
		{
			try 
			{
				data = new JSONObject(result);	
				jsonArray = data.getJSONArray("result");
					
			} catch (JSONException e) 
			{
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			
			for(int i = 0; i < jsonArray.length(); i++)
			{
				place = new Place();
				loc = new Location();
				
				try {
					eachPlace = jsonArray.getJSONObject(i);
					
					if(eachPlace != null)
					{
						loc.setLatitude(eachPlace.getJSONObject("geometry").getJSONObject("location").getString("lat"));
						loc.setLongitude(eachPlace.getJSONObject("geometry").getJSONObject("location").getString("lng"));
						place.setName(eachPlace.getString("name"));
						place.setReference(eachPlace.getString("reference"));
						place.setLocation(loc);
						place.setId(eachPlace.getString("id"));
						
						placeList.add(place);
					}
					else
					{
						Logger.Error("");
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
					
		}
		else
		{
			Logger.Error("");
		}

		return placeList;
	}



	private String getPlaces(Location location, String category, String radius, String query) {
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
	
	private String getPlaceDet(String reference, String eventId)
	{
		String result = null;
		
		try {
			result = this.gpClient.getPlaceDetails(reference, eventId);
		} catch (MalformedURLException e) {
			Logger.Error("MalformedURLException in parseEvent");
		} catch (IOException e) {
			Logger.Error("IOException in parseEvent");
		}
		
		return result;
	}

	@Override
	public Event getEventDetails(String eventId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Event> getEventList(Location location, String category,
			String radius, String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportEvents() {
		return false;
	}
	
	@Override
	public SourceType getSource() {
		return SourceType.GOOGLE_PLACE;
	}

}
