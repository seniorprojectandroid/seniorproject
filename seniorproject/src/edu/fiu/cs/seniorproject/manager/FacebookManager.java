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
import android.content.Intent;
import android.os.Bundle;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.utils.Logger;

public class FacebookManager {
	
	public interface IRequestResult {
		void onComplete( boolean success );
	}
	
	private Facebook mFacebook;
	
	public void login(Activity activity, final IRequestResult listener )
	{
		login(activity, new String[] {}, listener);		
	}
	
	@SuppressWarnings("deprecation")
	public void login(Activity activity, String[] permissions, final IRequestResult listener )
	{
		Facebook fb = getFacebookClient();
		fb.authorize(activity, permissions, new DialogListener()
		{

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
	
	@SuppressWarnings("deprecation")
	public void logout(Context context) {
		try {
			Facebook fb = getFacebookClient();
			fb.logout(context);
		} catch ( Exception e ) {
			Logger.Warning("Exception logging out facebook!!");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void authorizeCallback(int requestCode, int resultCode, Intent data)
	{
		Facebook fb = getFacebookClient();
		fb.authorizeCallback(requestCode, resultCode, data);		
		
	}
	
	
	public void PostFeed( String message, String placeId ) {
	
		Bundle params = new Bundle();
		params.putString("message", message);
		params.putString("place", placeId);
		
		try {
			@SuppressWarnings("deprecation")
			String response = getFacebookClient().request( "me/feed",params, "POST");	
			Logger.Debug("response = " + response);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public List<Place> getPlacesAtLocation( Location location, int distance ) {
		
		LinkedList<Place> result = null;
		try {
			Bundle params = new Bundle();
			params.putString("center", location.getLatitude() + "," + location.getLongitude() );
			params.putString("type", "place");
			params.putInt("distance", distance);
			
			@SuppressWarnings("deprecation")
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
									place.setSource(SourceType.FACEBOOK);
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
	
	public Facebook getFacebookClient() {
		if ( mFacebook == null ) {
			mFacebook = new Facebook( AppConfig.FB_APP_ID );
		}
		return mFacebook;
	}
	
	@SuppressWarnings("deprecation")
	public String request(String path)throws MalformedURLException, IOException 
	{
		return this.getFacebookClient().request(path);
	}
	
}
