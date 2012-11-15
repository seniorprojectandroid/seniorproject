package edu.fiu.cs.seniorproject;

import com.facebook.PlacePickerFragment;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.AppLocationManager.ILocationUpdateListener;
import edu.fiu.cs.seniorproject.manager.AppLocationManager.IReverseGeoLocationListener;
import edu.fiu.cs.seniorproject.utils.Logger;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GeoLocationActivity extends MapActivity {

	private MyLocationOverlay mMyLocationOverlay = null;
	private edu.fiu.cs.seniorproject.data.Location currentLocation = new edu.fiu.cs.seniorproject.data.Location();
	
	private final ILocationUpdateListener mGeoLocationListener = new ILocationUpdateListener() {
		@Override
		public void onLocationUpdate(Location newLocation) {
			updateLocationInfo(String.valueOf( newLocation.getLatitude() ), String.valueOf(newLocation.getLongitude()) );
	    	
	    	MapView mapView = (MapView) findViewById(R.id.geolocation_mapview);
	    	if ( mapView != null ) {
	    		MapController mc = mapView.getController();
	    		if ( mc != null ) {
	    			mc.animateTo(new GeoPoint( (int)(newLocation.getLatitude() * 1E6), (int)(newLocation.getLongitude() * 1E6)));
	    			mc.setZoom(17);	    	    			
	    			mapView.invalidate();
	    		}
	    	}
	    	
	    	AppLocationManager.getCurrentAddress(getApplicationContext(), mReverseGeoLocationListener);
		}
	};
	
	private final IReverseGeoLocationListener mReverseGeoLocationListener = new IReverseGeoLocationListener() {
		@Override
		public void onAdressResult(Address address) {
			TextView addressView = (TextView)findViewById(R.id.adress_value);
			if ( address != null && addressView != null ) {
	            String addressText = String.format("%s, %s, %s",
	                     address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
	                     address.getLocality(),
	                     address.getCountryName());             
	            addressView.setText(addressText);
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_location);
        
        AppLocationManager.init(this);
        MapView mapView = (MapView) findViewById(R.id.geolocation_mapview);
        mapView.setBuiltInZoomControls(true);
        
        mMyLocationOverlay = new MyLocationOverlay(this, mapView);
        mapView.getOverlays().add(mMyLocationOverlay);
        mapView.postInvalidate();
    }
    
    public void onCheckinClick(View view) {
    	
    	if ( currentLocation.getLatitude() != null && currentLocation.getLongitude() != null ) {
	    	
    		Intent intent = new Intent(this, FbPlacePicker.class);
	    	intent.putExtra("latitude", currentLocation.getLatitude());
	    	intent.putExtra("longitude", currentLocation.getLongitude());
	    	intent.putExtra(PlacePickerFragment.RADIUS_IN_METERS_BUNDLE_KEY, 100);
	    	intent.putExtra(PlacePickerFragment.SHOW_SEARCH_BOX_BUNDLE_KEY, false);
	    	
	    	android.location.Location location = new android.location.Location(LocationManager.GPS_PROVIDER);
	    	location.setLatitude(Double.valueOf(AppLocationManager.MIAMI_BEACH_LATITUDE));
			location.setLongitude(Double.valueOf(AppLocationManager.MIAMI_BEACH_LONGITUDE));
			
			intent.putExtra(PlacePickerFragment.LOCATION_BUNDLE_KEY, location);
			
	    	this.startActivity(intent);
    	} else {
    		Toast.makeText(this, "Waiting for location..", Toast.LENGTH_SHORT ).show();
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_geo_location, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                this.onSettingsClick(item);
                return true;            
        }
        return super.onOptionsItemSelected(item);
    }  
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mMyLocationOverlay.enableMyLocation();
    	AppLocationManager.registerListener(mGeoLocationListener);
    	Logger.Info("My Location listener set!!!");
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	mMyLocationOverlay.disableMyLocation();
    	AppLocationManager.unregisterListener(mGeoLocationListener);
    	Logger.Info("My Location listener removed!!!");
    }

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
    
    public void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    } 
}
