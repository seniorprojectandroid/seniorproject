package edu.fiu.cs.seniorproject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);      
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
    
    public void onShowGeoLocationClick(View view) {
    	Intent intent = new Intent(this, GeoLocationActivity.class);
    	this.startActivity(intent);
    }
    
    public void onEventMapButtonClick(View view) {
    	Intent intent = new Intent(this, EventsMapViewActivity.class);
    	this.startActivity(intent);
    }
    
    
    public void  onShowEventsClick(View view) {
    	Intent intent = new Intent(this, EventsActivity.class);
    	this.startActivity(intent);
    }
    
    public void  onPlacesButtonClick(View view) {
    	Intent intent = new Intent(this, PlacesActivity.class);
    	this.startActivity(intent);
    }
    
    public void onShowPlacesClick(View view) {
    	Intent intent = new Intent(this, PlacesActivity.class);
    	this.startActivity(intent);
    }
    
    public void onShowToursClick(View view) {
    	Intent intent = new Intent(this, TourActivity.class);
    	this.startActivity(intent);
    }
    
    public void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    }    
}
