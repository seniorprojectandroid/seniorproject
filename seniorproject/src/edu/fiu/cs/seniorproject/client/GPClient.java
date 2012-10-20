package edu.fiu.cs.seniorproject.client;

import java.io.IOException;
import java.net.MalformedURLException;

import edu.fiu.cs.seniorproject.utils.Logger;

import edu.fiu.cs.seniorproject.data.Location;
import android.os.Bundle;

public class GPClient extends RestClient{

	public GPClient(String appId)
	{
		super(appId);
	}
	
	/**
	 * @param reference
	 * @param eventId
	 * @return String
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String getPlaceDetails(String reference) throws MalformedURLException, IOException
	{
		Bundle params;
			
		params = this.getBundle();
		
		if(reference != null)
			params.putString("reference", reference);

		String result = null;
		String url = "https://maps.googleapis.com/maps/api/place/details/json";
		String method = "GET";

		try
		{
			result = this.openUrl(url, method, params);
		
		}catch(MalformedURLException e)
		{
			Logger.Error("GPClient getPlace MalformedURLException");
		}catch(IOException e)
		{
			Logger.Error("GPClient getPlace IOException");
		}
		
		return result;
	}
		
	/**
	 * 
	 * @param longitude
	 * @param latitude
	 * @param radius
	 * @param types
	 * @param name
	 * @return String
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String getPlaces(Location location, String category, String radius, String query) throws MalformedURLException, IOException
	{
	   /*
	    * https://maps.googleapis.com/maps/api/place/search/json?
		* location=-33.8670522,151.1957362&radius=500&types=food&name=harbour&
		* sensor=false&key=AddYourOwnKeyHere
		*/

		Bundle params = this.getBundle();
		
		if(location != null)
			params.putString("location", location.getLatitude() + "," + location.getLongitude());
		if(radius != null)
			params.putString("radius", radius);
		
		if (query!=null) {
			params.putString("keyword", query);
		}
		
		String result = null;
		String url = "https://maps.googleapis.com/maps/api/place/search/json";
		String method = "GET";
		
		try
		{
			result = this.openUrl(url, method, params);
		
		}catch(MalformedURLException e)
		{
			Logger.Error("GPClient getPlaces MalformedURLException");
		}catch(IOException e)
		{
			Logger.Error("GPClient getPlaces IOException");
		}
		
		return result;
	}
	
	public String getEventDet(String eventId, String reference)
	{
		Bundle params = this.getBundle();
		
		if(reference != null)
			params.putString("reference", reference);
		if(eventId != null)
			params.putString("event_id", eventId);
		
		String result = null;
		String url = "https://maps.googleapis.com/maps/api/place/event/details/json";
		String method = "GET";
		
		try
		{
			result = this.openUrl(url, method, params);
		
		}catch(MalformedURLException e)
		{
			Logger.Error("GPClient getEvents MalformedURLException");
		}catch(IOException e)
		{
			Logger.Error("GPClient getEvents IOException");
		}
		
		return result;
	}
	
	public String getNextPlaces(String nextPageToken)
	{
		String result = null;
		
		Bundle params = this.getBundle();
	
		if(nextPageToken != null)
			params.putString("pagetoken", nextPageToken);
		
		String url = "https:maps.googleapis.com/maps/api/place/search/json?";
		String method = "GET";
		
		try
		{
			result = this.openUrl(url, method, params);
		}catch(IOException e)
		{
			Logger.Error("GPClient get next page IOException.");
		}
		
		return result;
	}
	
	public Bundle getBundle()
	{
		Bundle bundle = new Bundle();
		bundle.putString("key", this.appId);
		bundle.putString("sensor", "false");
		return bundle;
	}
	
}