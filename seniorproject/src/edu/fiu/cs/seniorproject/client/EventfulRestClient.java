package edu.fiu.cs.seniorproject.client;

import java.io.IOException;
import java.net.MalformedURLException;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.utils.Logger;

import android.os.Bundle;

public class EventfulRestClient extends RestClient {
	
	private static final String BASE_URL = "http://api.eventful.com/json/";
	
	public EventfulRestClient(String appID) {
		super(appID);
	}
	
	public String getEventDetails(String eventId) {
		Bundle params = this.getBundle();
		params.putString("id", eventId);
		String response = null;
		
		try {
			response = this.openUrl(BASE_URL + "events/get", GET, params);
		} catch (MalformedURLException e) {
			Logger.Error("EventfulRestClient", "MalformedURLException getting event details!!!");
		} catch (IOException e) {
			Logger.Error("EventfulRestClient", "IOException getting event details!!!");
		}
		return response;
	}
	
	public String getEventList( String keywords, Location location, String date, String category, int within ) {
		Bundle params = this.getBundle();
		
		if ( keywords != null ) {
			params.putString("keywords", keywords);
		}
		
		if ( date != null ) {
			params.putString("date", date);
		}
		
		if ( category != null ) {
			params.putString("category",category);
		}
		
		if ( within > 0 ) {
			params.putInt("within", within);
		}
		
		if ( location != null ) {
			if ( location.getLatitude() != null && location.getLongitude() != null ) {
				params.putString("where", location.getLatitude() + "," + location.getLongitude());
			} else if ( location.getAddress() != null ) {
				params.putString("where", location.getAddress());
			}
		}
		
		String response = null;
		
		try {
			response = this.openUrl(BASE_URL + "events/search", GET, params);
		} catch (MalformedURLException e) {
			Logger.Error("EventfulRestClient", "MalformedURLException getting event list!!!");
		} catch (IOException e) {
			Logger.Error("EventfulRestClient", "IOException getting event list!!!");
		}
		return response;
	}
	
	public String getPlaceDetails(String placeId) {
		Bundle params = this.getBundle();
		params.putString("id", placeId);
		String response = null;
		
		try {
			response = this.openUrl(BASE_URL + "venues/get", GET, params);
		} catch (MalformedURLException e) {
			Logger.Error("EventfulRestClient", "MalformedURLException getting place details!!!");
		} catch (IOException e) {
			Logger.Error("EventfulRestClient", "IOException getting place details!!!");
		}
		return response;
	}
	
	public String getPlaceList( String keywords, Location location, int pageSize, int pageNumber, int within ) {
		Bundle params = this.getBundle();
		
		if ( keywords != null ) {
			params.putString("keywords", keywords);
		}
		
		if ( pageSize > 0 ) {
			params.putInt("page_size", pageSize);
		}
		
		if ( pageNumber > 0 ) {
			params.putInt( "page_number", pageNumber);
		}
		
		if ( within > 0 ) {
			params.putInt("within", within);
		}
		
		if ( location != null ) {
			if ( location.getLatitude() != null && location.getLongitude() != null ) {
				params.putString("location", location.getLatitude() + "," + location.getLongitude());
			} else if ( location.getAddress() != null ) {
				params.putString("location", location.getAddress());
			}
		}
		
		String response = null;
		
		try {
			response = this.openUrl(BASE_URL + "venues/search", GET, params);
		} catch (MalformedURLException e) {
			Logger.Error("EventfulRestClient", "MalformedURLException getting place list!!!");
		} catch (IOException e) {
			Logger.Error("EventfulRestClient", "IOException getting place list!!!");
		}
		return response;
	}
	
	private Bundle getBundle() {
		Bundle bundle = new Bundle();
		bundle.putString("app_key", this.appId);	// add always the app id
		return bundle;
	}
	
}
