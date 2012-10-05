package edu.fiu.cs.seniorproject;

import java.text.DecimalFormat;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;

public class EventDetailsActivity extends MapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        AppLocationManager.init(this);
        
        Intent intent = getIntent();
        if ( intent.hasExtra("event_id") && intent.hasExtra("source")) {
        	String eventId = intent.getStringExtra("event_id");
        	SourceType source = (SourceType)intent.getSerializableExtra("source");
        	
        	showEvent( DataManager.getSingleton().getEventDetails(eventId, source) );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_event_details, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	private void showEvent(Event event) {
		
		if ( event != null ) {
			TextView name = (TextView)findViewById(R.id.event_name);
			if ( name != null ) {
				name.setText(event.getName());
			}
			
			TextView description = (TextView)findViewById(R.id.event_description);
			if ( description != null ) {
				description.setText(event.getDescription());
			}
			
			TextView time = (TextView)findViewById(R.id.event_time);
			if ( time != null ) {
				time.setText(DateFormat.format("EEEE, MMMM dd, h:mmaa", Long.valueOf( event.getTime() ) * 1000 ).toString());
			}
			 
			if ( event.getImage() != null && !event.getImage().isEmpty() ) {
				ImageView image = (ImageView)findViewById(R.id.image);
				if ( image != null ) {
					DataManager.getSingleton().downloadBitmap(event.getImage(), image);
				}
			}
			
			Location location = event.getLocation();
			if ( location != null ) {
				TextView place = (TextView)findViewById(R.id.event_place);
				if ( place != null ) {
					place.setText(location.getAddress());
				}
				
				TextView distance = (TextView)findViewById(R.id.event_distance);
				if ( distance != null ) {
					float[] distanceResults = new float[1];
					android.location.Location currentLocation = AppLocationManager.getCurrentLocation();
					DecimalFormat df = new DecimalFormat("#.#");
					
					if ( currentLocation != null ) {
						android.location.Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()), distanceResults);
						double miles = distanceResults[0] / 1609.34;	// i mile = 1.60934km								
						distance.setText( df.format(miles) + "mi" );
					} else {
						distance.setText("--");
					}
				}
				
				
				
				
			 
			    
			    
			   
			    
			    
			    // Add the overlay to the itemized overlay and 
			    // Add the Overlay to the overlay list
			    
			    
				
				
				MapView map = (MapView)findViewById(R.id.mapview);
				List<Overlay> mapOverlays = map.getOverlays();
				
				Drawable drawable = this.getResources().getDrawable(R.drawable.facebook_icon_small);
			    ItemizedOverlayActivity itemizedoverlay = new ItemizedOverlayActivity(drawable, this);
				
			    
			    
			   
			    
				if ( map != null ) {
					MapController mc = map.getController();
		    		if ( mc != null ) {
		    			
		    			GeoPoint geoPoint =  new GeoPoint( (int)(Double.valueOf( location.getLatitude() ) * 1E6),
		    			  (int)(Double.valueOf( location.getLongitude() ) * 1E6 ));
		    			 OverlayItem overlayitem = new OverlayItem(geoPoint, "Hi, I am in !", event.getName());
		    			 
		    			// Add the overlay to the itemized overlay and 
		    			    // Add the Overlay to the overlay list
		    			    itemizedoverlay.addOverlay(overlayitem);

		    			    mapOverlays.add(itemizedoverlay);
		    			    
		    			    itemizedoverlay.addOverlay(overlayitem);
		    				
		    			    mapOverlays.add(itemizedoverlay);
		    			
		    			mc.setCenter(geoPoint);
		    			mc.setZoom(17);
		    			
		    			    
		    			   
		    			    
		    			    
		    			    
		    			
		    			
		    			//mc.setCenter(new GeoPoint( (int)(Double.valueOf( location.getLatitude() ) * 1E6),(int)(Double.valueOf( location.getLongitude() ) * 1E6 )));
		    			
		    		}
				}
			}
		}
	}
}
