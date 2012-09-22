package edu.fiu.cs.seniorproject.manager;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.fiu.cs.seniorproject.utils.Logger;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;

public class AppLocationManager {
	
	public interface ILocationUpdateListener {
		void onLocationUpdate(Location newLocation);
	}
	
	public interface IReverseGeoLocationListener {
		void onAdressResult(Address address);
	}
	
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	private static LocationManager mLocationManager = null;
	private final static LocationListener mGeoLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			checkLocation(location);   	
		}

		@Override
		public void onProviderDisabled(String provider) {					
		}

		@Override
		public void onProviderEnabled(String provider) {						
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}    	
	};
	
	private static Location currentLocation = null;
	private static ArrayList<ILocationUpdateListener> mListenerList = new ArrayList<ILocationUpdateListener>();
	private static boolean updatesEnabled = false;
	
	public static void init(Activity currentActivity) {
		if ( mLocationManager == null ) {
			mLocationManager = (LocationManager)currentActivity.getSystemService(Context.LOCATION_SERVICE);
			
			if ( mLocationManager != null ) {
				currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				Location networkLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				
				if ( networkLocation != null && isBetterLocation(networkLocation, currentLocation)) {
					currentLocation = networkLocation;
				}
			}
		}
	}
	
	public static void registerListener(ILocationUpdateListener listener) {
		mListenerList.add(listener);
		enableUpdades(true);
		
		if ( currentLocation != null ) {
			listener.onLocationUpdate(currentLocation);
		}
	}
	
	public static void unregisterListener(ILocationUpdateListener listener) {
		mListenerList.remove(listener);
		
		if ( mListenerList.size() == 0 ) { // nobody is listening, turn off location updates
			enableUpdades(false);
		}
	}
	
	public static void enableUpdades(boolean enable) {
		if ( enable != updatesEnabled ) {
			updatesEnabled = enable;
			
			if ( mLocationManager != null ) {
				if ( enable ) {
					
					try {
						mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
	        	            1000,          // 1-second interval.
	        	            10,             // 10 meters.
	        	            mGeoLocationListener);
					} catch (IllegalArgumentException e) {
						Logger.Warning("Unable to register for gps update.");
					}
					
					try {
						mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
		        	            5000,          // 5-second interval.
		        	            10,             // 10 meters.
		        	            mGeoLocationListener);
					} catch (IllegalArgumentException e) {
						Logger.Warning("Unable to register for network update.");
					}
				} else {
					mLocationManager.removeUpdates(mGeoLocationListener);
				}
			}
		}
	}
	
	public static Location getCurrentLocation() {
		return currentLocation;
	}
	
	public static void getCurrentAddress(Context context, IReverseGeoLocationListener listener) {
		if ( currentLocation != null && context != null && listener != null ) {
			(new AppLocationManager.ReverseGeocodingTask(context, listener)).execute(currentLocation);
		}
	}
	
	private static void dispathLocationUpdate(Location newLocation) {
		if ( updatesEnabled && mListenerList.size() > 0 ) {
			for (ILocationUpdateListener listener : mListenerList) {
				listener.onLocationUpdate(newLocation);
			}
		}
	}
	
	private static void checkLocation(Location newLocation ) {
		
		if ( newLocation != null && isBetterLocation(newLocation, currentLocation)) {
			currentLocation = newLocation;
			dispathLocationUpdate(newLocation);
		}
	}
	
	/** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	private static boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
	
	// AsyncTask encapsulating the reverse-geocoding API.  Since the geocoder API is blocked,
	 // we do not want to invoke it from the UI thread.
	 private static class ReverseGeocodingTask extends AsyncTask<Location, Void, Address>
	 {
		 Context mContext;
		 private final WeakReference<IReverseGeoLocationListener> mListener;
		 
	     public ReverseGeocodingTask(Context context, IReverseGeoLocationListener listener) {
	         super();
	         mContext = context;
	         mListener = new WeakReference<IReverseGeoLocationListener>(listener);
	     }
	
	     @Override
	     protected Address doInBackground(Location... params) {
	         Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
	
	         Location loc = params[0];
	         List<Address> addresses = null;
	         try {
	             // Call the synchronous getFromLocation() method by passing in the lat/long values.
	             addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
	         } catch (IOException e) {
	            Logger.Error("Exception getting address " + e.getMessage() );
	         }
	         
	         Address address = null;
	         if (addresses != null && addresses.size() > 0) {
	             address = addresses.get(0);
	             // Format the first line of address (if available), city, and country name.
//	             addressText = String.format("%s, %s, %s",
//	                     address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
//	                     address.getLocality(),
//	                     address.getCountryName());             
	         }
	         return address;
	     }
	     
	     protected void onPostExecute(Address address) {
	    	 if ( mListener != null ) {
		    	IReverseGeoLocationListener listener = mListener.get();
				if ( address != null && listener != null) {
					listener.onAdressResult(address);
				}
	    	 }
	     }
	 }
}
