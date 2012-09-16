package edu.fiu.cs.seniorproject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

import edu.fiu.cs.seniorproject.utils.Logger;

import android.content.Context;
//import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
//import android.provider.Settings;
import android.view.Menu;
import android.widget.TextView;

public class GeoLocationActivity extends MapActivity {

	private LocationManager mLocationManager = null;
	private GeoLocationListener mListener = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_location);
        
        MapView mapView = (MapView) findViewById(R.id.geolocation_mapview);
        mapView.setBuiltInZoomControls(true);
        
        this.mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.mListener = new GeoLocationListener();
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
        Logger.Info("Remove listener!!!");
        mLocationManager.removeUpdates(mListener);
    }
    
//    private void enableLocationSettings() {
//        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(settingsIntent);
//    }
    
    private class GeoLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			TextView latitude = (TextView)findViewById(R.id.latitude_value);
	    	if ( latitude != null) {
	    		latitude.setText( String.valueOf( location.getLatitude() ) );
	    	}
	    	
	    	TextView longitude = (TextView)findViewById(R.id.longitude_value);
	    	if ( longitude != null) {
	    		longitude.setText( String.valueOf(location.getLongitude()));
	    	}
	    	
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
				TextView latitude = (TextView)findViewById(R.id.latitude_value);
		    	if ( latitude != null) {
		    		latitude.setText( "Disabled" );
		    	}
		    	
		    	TextView longitude = (TextView)findViewById(R.id.longitude_value);
		    	if ( longitude != null) {
		    		longitude.setText( "Disabled" );
		    	}		
			}			
		}

		@Override
		public void onProviderEnabled(String provider) {
			if ( provider != null && provider.equals(LocationManager.GPS_PROVIDER)) {
				TextView latitude = (TextView)findViewById(R.id.latitude_value);
		    	if ( latitude != null) {
		    		latitude.setText( "Loading.." );
		    	}
		    	
		    	TextView longitude = (TextView)findViewById(R.id.longitude_value);
		    	if ( longitude != null) {
		    		longitude.setText( "Loading.." );
		    	}		
			}			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}    	
    }
    
}
