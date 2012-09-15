package edu.fiu.cs.seniorproject;

import edu.fiu.cs.seniorproject.utils.Logger;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

	private LocationManager mLocationManager = null;
	private final LocationListener mListener = new LocationListener() {

	    @Override
	    public void onLocationChanged(Location location) {
	        // A new location update is received.  Do something useful with it.  In this case,
	        // we're sending the update to a handler which then updates the UI with the new
	        // location.
//	        Message.obtain(mHandler,
//	                UPDATE_LATLNG,
//	                location.getLatitude() + ", " +
//	                location.getLongitude()).sendToTarget();
//
//	        }
	    	TextView latitude = (TextView)findViewById(R.id.latitude);
	    	if ( latitude != null) {
	    		latitude.setText( "Lat-" + location.getLatitude());
	    	}
	    	
	    	TextView longitude = (TextView)findViewById(R.id.longitude);
	    	if ( longitude != null) {
	    		longitude.setText( "Lon-" + location.getLongitude());
	    	}
	}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.mLocationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        if ( this.mLocationManager != null ) {
	        final boolean gpsEnabled = this.mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	
	        if (!gpsEnabled) {
	            // Build an alert dialog here that requests that the user enable
	            // the location services, then when the user clicks the "OK" button,
	            // call enableLocationSettings()
	        	Logger.Warning("GPS is disabled!!!");
	        	this.mLocationManager = null;
	        	enableLocationSettings();
	        }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void onLoginButtonClick(View view) {
    	Intent intent = new Intent(this, FacebookLoginActivity.class);
    	this.startActivity(intent);
    }
    
    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	
    	Logger.Info("Set listener!!!");
    	if ( this.mLocationManager != null ) {
    		this.mLocationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
            10000,          // 10-second interval.
            10,             // 10 meters.
            mListener);
    	}
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        Logger.Info("Remove listener!!!");
        mLocationManager.removeUpdates(mListener);
    }
}
