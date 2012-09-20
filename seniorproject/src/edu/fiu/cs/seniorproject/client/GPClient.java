package edu.fiu.cs.seniorproject.client;

import java.io.IOException;
import java.net.MalformedURLException;

import edu.fiu.cs.seniorproject.utils.Logger;

import android.os.Bundle;

public class GPClient extends RestClient{

	public GPClient(String appId)
	{
		super(appId);
	}
	
	/**
	 * 
	 * @param reference
	 * @param eventId
	 * @return String
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public String getEvent(String reference, String eventId) throws MalformedURLException, IOException
	{
		/*
		 * 	https://maps.googleapis.com/maps/api/place/event/details/format?sensor=true_or_false
  		 *	&key=api_key
  		 *	&reference=CnRkAAAAGnBVNFDeQoOQHzgdOpOqJNV7K9-etc
  		 *	&event_id=weafgerg1234235
		 */
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
	public String getPlaces(String longitude, String latitude, String radius, String types, String name) throws MalformedURLException, IOException
	{
	   /*
	    * https://maps.googleapis.com/maps/api/place/search/json?
		* location=-33.8670522,151.1957362&radius=500&types=food&name=harbour&
		* sensor=false&key=AddYourOwnKeyHere
		*/


		Bundle params = this.getBundle();
		
		if(longitude != null)
			params.putString("longitude", longitude);
		if(latitude != null)
			params.putString("latitude", latitude);
		if(radius != null)
			params.putString("radius",radius);
		if(types != null)
			params.putString("types",types);
		if(name != null)
			params.putString("name",name);
		
		params.putString("sensor","false");
		
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
	
	public Bundle getBundle()
	{
		Bundle bundle = new Bundle();
		bundle.putString("key", this.appId);
		return bundle;
	}
	
}
