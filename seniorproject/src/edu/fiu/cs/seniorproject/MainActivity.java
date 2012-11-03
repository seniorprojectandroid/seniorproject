package edu.fiu.cs.seniorproject;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                this.onSettingsClick(item);
                return true;            
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onFacebookLoginButtonClick(View view) {
    	Intent intent = new Intent(this, FacebookLoginActivity.class);
    	this.startActivity(intent);
    }
    
    public void onShowGeoLocationClick(View view) {
    	Intent intent = new Intent(this, GeoLocationActivity.class);
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
    
    public void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    }   
    
    public void onShowMyPlacesClick(View view) {
    	Intent intent = new Intent(this, MyPlacesActivity.class);
    	this.startActivity(intent);
    }
    public void onShowMyEventsClick (View view) {
    	Intent intent = new Intent(this, MyEventsActivity.class); 
    	this.startActivity(intent);
    }
}
