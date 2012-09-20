package edu.fiu.cs.seniorproject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import edu.fiu.cs.seniorproject.utils.Logger;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Address;
//import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class GeoLocationActivity extends MapActivity {

	private LocationManager mLocationManager = null;
	private GeoLocationListener mListener = null;
	private CustomLocationOverlay mMyLocationOverlay = null;
	private edu.fiu.cs.seniorproject.data.Location currentLocation = new edu.fiu.cs.seniorproject.data.Location();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_location);
        
        MapView mapView = (MapView) findViewById(R.id.geolocation_mapview);
        mapView.setBuiltInZoomControls(true);
        
        //this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //this.mListener = new GeoLocationListener();
        mMyLocationOverlay = new CustomLocationOverlay(this, mapView);
        mapView.getOverlays().add(mMyLocationOverlay);
        mapView.postInvalidate();
    }
    
    public void onCheckinClick(View view) {
    	Intent intent = new Intent(this, CheckinActivity.class);
    	intent.putExtra("latitude", currentLocation.getLongitude());
    	intent.putExtra("longitude", currentLocation.getLongitude());
    	this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_geo_location, menu);
        return true;
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mMyLocationOverlay.enableMyLocation();
    	Logger.Info("My Location listener set!!!");
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	mMyLocationOverlay.disableMyLocation();
    	Logger.Info("My Location listener removed!!!");
    }
    
    @Override
    public void onStart() {
    	super.onStart();    	
    	 
         if ( this.mLocationManager != null ) {        	 
        	 Logger.Info("Set listener!!!");
        	 
        	 this.mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
        	            1000,          // 10-second interval.
        	            5,             // 10 meters.
        	            mListener);
        	 this.mLocationManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER,
     	            1000,          // 10-second interval.
     	            5,             // 10 meters.
     	            mListener);
        	 
 	        final boolean gpsEnabled = this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
 	
 	        if (!gpsEnabled) {
 	            // Build an alert dialog here that requests that the user enable
 	            // the location services, then when the user clicks the "OK" button,
 	            // call enableLocationSettings()
 	        	Logger.Warning("GPS is disabled!!!");
 	        	//enableLocationSettings();
 	        } else {
 	        	Logger.Info("GPS is enabled. waiting for location..");
 	        }
         }
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        
        if ( this.mLocationManager != null ) {    
	        Logger.Info("Remove listener!!!");
	        mLocationManager.removeUpdates(mListener);
        }
    }
    
//    private void enableLocationSettings() {
//        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(settingsIntent);
//    }
    
    protected void updateLocationInfo(String latitudeInfo, String longitudeInfo) {
    	TextView latitude = (TextView)findViewById(R.id.latitude_value);
    	if ( latitude != null) {
    		latitude.setText( latitudeInfo );
    	}
    	
    	TextView longitude = (TextView)findViewById(R.id.longitude_value);
    	if ( longitude != null) {
    		longitude.setText( longitudeInfo );
    	}	
    	currentLocation.setLatitude(latitudeInfo);
    	currentLocation.setLongitude(longitudeInfo);
    }
    
    private class CustomLocationOverlay extends MyLocationOverlay {

		public CustomLocationOverlay(Context arg0, MapView arg1) {
			super(arg0, arg1);			
		}
    	
		@Override
		public void onLocationChanged(Location location ) {
			super.onLocationChanged(location);
			updateLocationInfo(String.valueOf( location.getLatitude() ), String.valueOf(location.getLongitude()) );
			
			MapView mapView = (MapView) findViewById(R.id.geolocation_mapview);
	    	if ( mapView != null ) {
	    		MapController mc = mapView.getController();
	    		if ( mc != null ) {
	    			int currentZoom = mapView.getZoomLevel();
	    			for( int i = currentZoom + 1; i <= 17; i++ ) {	    				
	    				mc.zoomIn();
	    			}
	    			//mc.setZoom(17);
	    			mc.animateTo(new GeoPoint( (int)(location.getLatitude() * 1E6), (int)(location.getLongitude() * 1E6)));
	    			mapView.invalidate();
	    		}
	    	}
	    	(new ReverseGeocodingTask(GeoLocationActivity.this)).execute(new Location[] {location});
		}
    }
    
	 // AsyncTask encapsulating the reverse-geocoding API.  Since the geocoder API is blocked,
	 // we do not want to invoke it from the UI thread.
	 private class ReverseGeocodingTask extends AsyncTask<Location, Void, String>
	 {
		 Context mContext;
			 private final WeakReference<TextView> mAddressTextField;
			 
		     public ReverseGeocodingTask(Context context) {
		         super();
		         mContext = context;
		         mAddressTextField = new WeakReference<TextView>( (TextView)findViewById(R.id.adress_value) );
		     }
		
		     @Override
		     protected String doInBackground(Location... params) {
		         Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		
		         Location loc = params[0];
		         List<Address> addresses = null;
		         try {
		             // Call the synchronous getFromLocation() method by passing in the lat/long values.
		             addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
		         } catch (IOException e) {
		            Logger.Error("Exception getting address " + e.getMessage() );
		         }
		         
		         String addressText = "Loading..";
		         if (addresses != null && addresses.size() > 0) {
		             Address address = addresses.get(0);
		             // Format the first line of address (if available), city, and country name.
		             addressText = String.format("%s, %s, %s",
		                     address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
		                     address.getLocality(),
		                     address.getCountryName());             
		         }
		         return addressText;
		     }
		     
		     protected void onPostExecute(String addressText) {
		    	 if ( mAddressTextField != null ) {
			    	TextView address = mAddressTextField.get();
					if ( address != null) {
						address.setText( addressText );
					}
		    	 }
		     }
	 }
 
    private class GeoLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			
			updateLocationInfo(String.valueOf( location.getLatitude() ), String.valueOf(location.getLongitude()) );
	    	
	    	MapView mapView = (MapView) findViewById(R.id.geolocation_mapview);
	    	if ( mapView != null ) {
	    		MapController mc = mapView.getController();
	    		if ( mc != null ) {
	    			mc.animateTo(new GeoPoint( (int)(location.getLatitude() * 1E6), (int)(location.getLongitude() * 1E6)));
	    			mc.setZoom(17);	    	    			
	    			mapView.invalidate();
	    		}
	    	}
		}

		@Override
		public void onProviderDisabled(String provider) {
			if ( provider != null && provider.equals(LocationManager.GPS_PROVIDER)) {
				updateLocationInfo("Disabled", "Disabled");
			}			
		}

		@Override
		public void onProviderEnabled(String provider) {
			if ( provider != null && provider.equals(LocationManager.GPS_PROVIDER)) {
				updateLocationInfo("Loading..", "Loading.." );		
			}			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}    	
    }
    
}
