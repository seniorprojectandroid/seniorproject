package edu.fiu.cs.seniorproject.manager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.utils.Logger;

public class FacebookManager {
	
	public interface IRequestResult {
		void onComplete( boolean success );
	}
	
	private Facebook mFacebook;
	
	public void login(Activity activity, final IRequestResult listener ) {
		Facebook fb = getFacebookClient();
		fb.authorize(activity, new DialogListener() {

			@Override
			public void onComplete(Bundle values) {
				listener.onComplete(true);
			}

			@Override
			public void onFacebookError(FacebookError e) {
				listener.onComplete(false);
			}

			@Override
			public void onError(DialogError e) {
				listener.onComplete(false);
			}

			@Override
			public void onCancel() {
				listener.onComplete(false);
			}			
		} );
	}
	
	public void logout(Context context) {
		try {
			Facebook fb = getFacebookClient();
			fb.logout(context);
		} catch ( Exception e ) {
			Logger.Warning("Exception logging out facebook!!");
		}
	}
	
	public List<Place> getPlacesAtLocation( Location location, int distance ) {
		
		LinkedList<Place> result = null;
		try {
			Bundle params = new Bundle();
			params.putString("center", location.getLatitude() + "," + location.getLongitude() );
			params.putString("type", "place");
			params.putInt("distance", distance);
			
			String response = getFacebookClient().request("search", params);
			
			if ( response != null && !response.isEmpty() ) {
				try {
					JSONObject json = new JSONObject(response);
					
					if ( json != null && json.has("data") ) {
						JSONArray placeList = json.getJSONArray("data");
						if ( placeList != null && placeList.length() > 0 ) {
							
							result = new LinkedList<Place>();
							for( int i = 0; i < placeList.length(); i++ ) {
								JSONObject placeIter = placeList.getJSONObject(i);
								if ( placeIter != null && placeIter.has("id") && placeIter.has("name")) {
									Place place = new Place();
									place.setId(placeIter.getString("id"));
									place.setName(placeIter.getString("name"));
									result.add(place);
								}
							}
						}
					}
					
				} catch (JSONException e) {
					Logger.Warning("Exception decoding json on get places!!!");
				}
				
			}
		} catch (MalformedURLException e) {
			Logger.Warning("malformed exception searching for places");
		} catch (IOException e) {
			Logger.Warning("io exception searching for places");
		}
		
		return result;
	}
	
	private Facebook getFacebookClient() {
		if ( mFacebook == null ) {
			mFacebook = new Facebook( AppConfig.FB_APP_ID );
		}
		return mFacebook;
	}
	
}