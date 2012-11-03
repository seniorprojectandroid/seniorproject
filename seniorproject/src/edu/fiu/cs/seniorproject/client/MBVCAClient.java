package edu.fiu.cs.seniorproject.client;

import edu.fiu.cs.seniorproject.config.AppConfig;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.json.JSONException;
import org.json.JSONObject;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.utils.Logger;
import android.os.Bundle;
import android.text.format.DateFormat;

public class MBVCAClient extends RestClient{

	private static final String BASE_API = "http://www.miamibeachapi.com/api/api.php/";

	private final String OAUTH_CONSUMER_KEY  = "anonymous";
	private final String OAUTH_CONSUMER_SECRET  = "anonymous";
	
	private final OAuthConsumer mConsumer = new DefaultOAuthConsumer(OAUTH_CONSUMER_KEY, OAUTH_CONSUMER_SECRET);
	
	public MBVCAClient(String appId)
	{
		super(appId);
		mConsumer.setTokenWithSecret(AppConfig.MBVCA_TOKEN, AppConfig.MBVCA_TOKEN_SECRET);
	}
	
	public String getEventList(Location location, String category,String radiusStr, long startTime, long endTime, String search) {
		return this.getEventList(location, category, radiusStr, startTime, endTime, search, 1, 25 );
	}

	public String getEventList(Location location, String category,String radiusStr, long startTime, long endTime, String search, int page, int rows ) {
		String response = null;

		Bundle params = getBundle();
		
		if ( search != null && !search.isEmpty() ) {
			params.putString("keyword", search);
		}
		
		if ( location != null ) {
			params.putString("lat", location.getLatitude() );
			params.putString("lng", location.getLongitude());
		}
		
		if ( startTime != 0 && endTime != 0 ) {
			String startStr = DateFormat.format("yyyyMMdd", startTime * 1000 ).toString();
			String endStr = DateFormat.format("yyyyMMdd", endTime * 1000 ).toString();
			params.putString("date_filter", startStr + "-" + endStr );
		}
		
		if ( radiusStr != null && !radiusStr.isEmpty() ) {
			params.putString("radius", radiusStr);
		}
		
		if ( category != null && !category.isEmpty() ) {
			params.putString("category_filter", category);
		}
		
		params.putString("srt", "{\"name\":1}" );
		params.putString("page", String.valueOf(page));
		params.putString("rows", String.valueOf(rows));
		
		try {
			response = openUrl( "http://www.miamibeachapi.com/rest/a.pi/events/search", GET, params );
		} catch (MalformedURLException e) {
			Logger.Warning("Malformed exception getting MBVCA events " + e.getMessage() );
		} catch (IOException e) {
			Logger.Warning("IO exception getting MBVCA events " + e.getMessage() );
		}

		return response;
	}

	public String getEventDetails(String event_id) {
		String response = null;
		try {
			Bundle params = getBundle();
			JSONObject query = new JSONObject();
			query.put("calendar_entry_id", Integer.valueOf(event_id));
			params.putString("qry", query.toString() );

			try {
				response = openUrl( BASE_API + "search/solodev_view", GET, params );
			} catch (MalformedURLException e) {
				Logger.Warning("Malformed exception getting MBVCA event detail " + e.getMessage() );
			} catch (IOException e) {
				Logger.Warning("IO exception getting MBVCA event detail " + e.getMessage() );
			}
		} catch ( JSONException e ) {
			Logger.Error("Error encoding json in MBVCA getEventDetails" + e.getMessage() );
		}
		return response;
	}

	public String getPlaceList(Location location, String category,String radiusStr, String search) {
		String result = null;

		try {
			Bundle params = getBundle();
			JSONObject query = new JSONObject();

			if ( category != null && !category.isEmpty() ) {
				query.put("datatable_category_id", Integer.valueOf( category ) );
			} else {
				Logger.Warning("Calling get events from MBVCA with empty category!!!");
				return null;	// need a category to query
			}
			
			if ( location != null ) {
				float radius = Float.valueOf(radiusStr);
				if ( radius > 0 ) {
					this.addLocationFilter(query, location, radius);
				}
			}
			
			if ( search != null ) {
				query.put("name", "/" + search + "/i" );
			}
			
			String queryStr = query.toString();

			Logger.Debug("Query string in MBVCA = " + query);
			params.putString("qry", queryStr);
			//params.putString("srt", "{\"lat\":1}" );

			try {
				result = openUrl( BASE_API + "search/solodev_view", GET, params );
			} catch (MalformedURLException e) {
				Logger.Warning("Malformed exception getting MBVCA places " + e.getMessage() );
			} catch (IOException e) {
				Logger.Warning("IO exception getting MBVCA places " + e.getMessage() );
			}
		} catch (JSONException e) {
			result = null;
			Logger.Error("Exeption encoding url params" + e.getMessage() );
		}

		return result;
	}

	public String getPlaceDetails(String placeId) {
		String result = null;

		try {
			Bundle params = getBundle();
			JSONObject query = new JSONObject();

			if ( placeId != null && !placeId.isEmpty() ) {
				query.put("datatable_entry_id", Integer.valueOf(placeId));
			} else {
				Logger.Warning("Both params are null or empty. Unable to make request!!!");
				return null;
			}

			params.putString("qry", query.toString() );

			try {
				result = openUrl( BASE_API + "search/solodev_view", GET, params );
			} catch (MalformedURLException e) {
				Logger.Warning("Malformed exception getting MBVCA place detail " + e.getMessage() );
			} catch (IOException e) {
				Logger.Warning("IO exception getting MBVCA place detail " + e.getMessage() );
			}
		} catch ( JSONException e ) {
			Logger.Error("Error encoding json in MBVCA getPlaceDetails" + e.getMessage() );
		}

		return result;
	}

	public String getEventsAtPlace( String placeName ) {
		String result = null;

		try {
			Bundle params = getBundle();
			JSONObject query = new JSONObject();
			query.put("calendar_id", 1);	// have to be events

			if ( placeName != null && !placeName.isEmpty() ) {
				query.put("venue", placeName);
			} else {
				Logger.Warning("Need a venue id to make request!!!");
				return null;
			}
			params.putString("qry", query.toString() );

			try {
				result = openUrl( BASE_API + "search/solodev_view", GET, params );
			} catch (MalformedURLException e) {
				Logger.Warning("Malformed exception getting MBVCA events at place" + e.getMessage() );
			} catch (IOException e) {
				Logger.Warning("IO exception getting MBVCA events at place " + e.getMessage() );
			}
		} catch ( JSONException e ) {
			Logger.Error("Error encoding json in MBVCA getEventsAtPlace" + e.getMessage() );
		}
		return result;
	}

	private void addLocationFilter(JSONObject query, Location location, float radius ) {
		// At 25 degree north
		// Length Of A Degree Of Latitude In Meters => 110772.87 meters => 68.8311 miles
		// Length Of A Degree Of Longitude In Meters => 100950.06 meters => 62.7275 miles
		// offset = ( 1 / distance ) * radius
		if ( location != null && radius > 0 ) {
			try {
				
				double range = radius / 68.8311;
				double center = Double.valueOf(location.getLatitude());
			
				JSONObject latitudeFilter = new JSONObject();
				latitudeFilter.put("$gt", center - range);
				latitudeFilter.put("$lt", center + range);
				
				range = radius / 62.7275;
				center = Double.valueOf(location.getLongitude());
				
				JSONObject longitudeFilter = new JSONObject();
				longitudeFilter.put("$gt", center - range);
				longitudeFilter.put("$lt", center + range);
				
				query.put("lat", latitudeFilter);
				query.put("lng", longitudeFilter);
				
			} catch (JSONException e) {
				Logger.Warning("Exception encoding lat and lng filters " + e.getMessage() );
			}
		}
	}
	
	public Bundle getBundle()
	{
		Bundle bundle = new Bundle();
		//bundle.putString("token", AppConfig.MBVCA_TOKEN);
		//bundle.putString("token_secret", AppConfig.MBVCA_TOKEN_SECRET);
		return bundle;
	}

	@Override
	protected void signRequest(HttpURLConnection conn) {
		if ( conn != null ) {
			try {
				mConsumer.sign(conn);
			} catch (OAuthMessageSignerException e) {
				Logger.Error("OAuthMessageSignerException signing request in MBVCA " + e.getMessage() );
			} catch (OAuthExpectationFailedException e) {
				Logger.Error("OAuthExpectationFailedException signing request in MBVCA " + e.getMessage() );
			} catch (OAuthCommunicationException e) {
				Logger.Error("OAuthCommunicationException signing request in MBVCA " + e.getMessage() );
			}
		}
	}
}
