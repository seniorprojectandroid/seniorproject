package edu.fiu.cs.seniorproject.client;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.utils.DateUtils;
import edu.fiu.cs.seniorproject.utils.Logger;

import android.os.Bundle;

public class MBVCAClient extends RestClient{

	private static final String BASE_API = "http://www.miamibeachapi.com/api/index.php/";

	public MBVCAClient(String appId)
	{
		super(appId);
	}

	public String getEventList(Location location, String category,String radiusStr, long startTime, long endTime) {
		String response = null;

		try {
			Bundle params = getBundle();
			JSONObject query = new JSONObject();
			query.put("calendar_id", 1);
			if ( startTime == 0 ) {
				startTime = DateUtils.getTodayTimeInMiliseconds();
				endTime = startTime + DateUtils.ONE_DAY;
			}
			JSONObject timeFilter = new JSONObject();
			timeFilter.put("$gt", startTime);
			if ( endTime != 0 ) {
				timeFilter.put("$lt", endTime);
			}
			query.put("start_time", timeFilter);

			if ( category != null && !category.isEmpty() ) {
				query.put("datatable_category_id", Integer.valueOf( category ) );
			} else {
				JSONObject existObject = new JSONObject();
				existObject.put("$exists", true);
				query.put("datatable_category_id", existObject );
			}
			
			if ( location != null ) {
				float radius = Float.valueOf(radiusStr);
				if ( radius > 0 ) {
					this.addLocationFilter(query, location, radius);
				}
			}
			
			String queryStr = query.toString();

			Logger.Debug("Query string in MBVCA = " + query);
			params.putString("qry", queryStr);
			params.putString("srt", "{\"start_time\":1}" );


			try {
				return openUrl( BASE_API + "search/solodev_view", GET, params );
			} catch (MalformedURLException e) {
				Logger.Warning("Malformed exception getting MBVCA events " + e.getMessage() );
			} catch (IOException e) {
				Logger.Warning("IO exception getting MBVCA events " + e.getMessage() );
			}
		} catch (JSONException e) {
			response = null;
			Logger.Error("Exeption encoding url params" + e.getMessage());
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
				return openUrl( BASE_API + "search/solodev_view", GET, params );
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

	public String getPlaceList(Location location, String category,String radius) {
		String result = null;

		try {
			Bundle params = getBundle();
			JSONObject query = new JSONObject();

			if ( category != null && !category.isEmpty() ) {
				query.put("datatable_category_id", Integer.valueOf( category ) );
			} else {
				return null;	// need a category to query
			}
			String queryStr = query.toString();

			Logger.Debug("Query string in MBVCA = " + query);
			params.putString("qry", queryStr);
			//params.putString("srt", "{\"lat\":1}" );

			try {
				return openUrl( BASE_API + "search/solodev_view", GET, params );
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

	public String getPlaceDetails(String placeId, String reference) {
		String result = null;

		try {
			Bundle params = getBundle();
			JSONObject query = new JSONObject();

			if ( placeId != null && !placeId.isEmpty() ) {
				query.put("datatable_entry_id", Integer.valueOf(placeId));
			} else if ( reference != null && !reference.isEmpty() ) {
				query.put("last_name", reference);
			} else {
				Logger.Warning("Both params are null or empty. Unable to make request!!!");
				return null;
			}

			params.putString("qry", query.toString() );

			try {
				return openUrl( BASE_API + "search/solodev_view", GET, params );
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
				return openUrl( BASE_API + "search/solodev_view", GET, params );
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
		bundle.putString("token", AppConfig.MBVCA_TOKEN);
		bundle.putString("token_secret", AppConfig.MBVCA_TOKEN_SECRET);
		return bundle;
	}

}