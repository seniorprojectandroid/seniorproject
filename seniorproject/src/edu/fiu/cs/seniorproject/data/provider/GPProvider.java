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

import android.location.Location;

//import android.provider.ContactsContract.CommonDataKinds.Event;

import edu.fiu.cs.seniorproject.client.GPClient;
import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.utils.Logger;

public class  GPProvider extends DataProvider {
	
	private GPClient gpClient;

	public GPProvider() {
		this.gpClient = new GPClient(AppConfig.GOOGLE_PLACE_APP_ID);
	}



	public Place getPlaceDetails(String reference, String eventId) {

		JSONObject data = null;
		String result = null;
		Place place = null;
		
		result = this.getPlaceDet(reference, eventId);
		
		if(result != null && result.length() > 0)
		{
			try {
				data = new JSONObject(result);
				
				if(data != null && data.has("result"))
				{
					place = new Place();
//					place.setTime(data.getString("start_time"));
//					place.setDesscription(data.getString("summary"));
					
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

	
	public List<Place> getPlaceList(Location location, String category, String radius, String query) {

		String result = null;
		JSONObject data = null;
		JSONArray jsonArray = null;
		LinkedList<Place> placeList = null;
		
		result = getPlaces(location,category,radius,query);
		
		
		if(result !=null && result.length() > 0)
		{
			try
			{
				data = new JSONObject("result");
			
			
			if(data != null && data.has("result"))
			{
				
				jsonArray = data.getJSONArray(result);
				
				if(jsonArray.length() > 0)
				{
					JSONObject jsonOb = null;
					placeList = new LinkedList<Place>();
					
					for(int i = 0; i < jsonArray.length(); i++)
					{
						Place place = new Place();
						jsonOb = jsonArray.getJSONObject(i);
						
						place.setLocation(jsonOb.getString("formatted_address"));
						place.setName(jsonOb.getString("name"));
						place.setId(jsonOb.getString("id"));
						
						placeList.add(place);
					}
				}	
			}
			}catch(JSONException e)
			{
				Logger.Error(e.getMessage());
			}
			
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
	public List<Event> getEventList(Location location, String category,
			String radius, String query) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Event getEventDetails(String eventId) {
		// TODO Auto-generated method stub
		return null;
	}

}

