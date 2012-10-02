package edu.fiu.cs.seniorproject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class PlaceDetailsActivity extends MapActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        AppLocationManager.init(this);
        
        Intent intent = getIntent();
        if ( intent.hasExtra("event_id") && intent.hasExtra("source")) {
        	String eventId = intent.getStringExtra("event_id");
        	SourceType source = (SourceType)intent.getSerializableExtra("source");        	
        	(new PlaceDownloader(this)).execute(new PlaceSearchData(eventId, source));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_place_details, menu);
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

	private void showPlace(Place place) {
		
		if ( place != null ) {
			TextView name = (TextView)findViewById(R.id.place_name);
			if ( name != null ) {
				name.setText(place.getName());
			}
			
			//create PLACE description.
			TextView description = (TextView)findViewById(R.id.event_description);
			if ( description != null ) {
				description.setText(place.getDescription());
			}
			
			 
			if ( place.getImage() != null && !place.getImage().isEmpty() ) {
				ImageView image = (ImageView)findViewById(R.id.image);
				if ( image != null ) {
					DataManager.getSingleton().downloadBitmap(place.getImage(), image);
				}
			}
			
			//place location 
			Location location = place.getLocation();
			if ( location != null ) {
				TextView placeLocation = (TextView)findViewById(R.id.event_place);
				if ( placeLocation != null ) {
					placeLocation.setText(location.getAddress());
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
				
				MapView map = (MapView)findViewById(R.id.mapview);
				if ( map != null ) {
					MapController mc = map.getController();
		    		if ( mc != null ) {
		    			mc.setCenter(new GeoPoint( (int)(Double.valueOf( location.getLatitude() ) * 1E6),(int)(Double.valueOf( location.getLongitude() ) * 1E6 )));
		    			mc.setZoom(17);
		    		}
				}
			}
		}
	}

	private class PlaceSearchData {
		
		public String placeId;
		public SourceType source;
		
		public PlaceSearchData(String id, SourceType sourceType) {
			this.placeId = id;
			this.source = sourceType;
		}
	}
	
	private class PlaceDownloader extends AsyncTask<PlaceSearchData, Void, Place>
	{
		private WeakReference<PlaceDetailsActivity> mActivityReference;
		
		public PlaceDownloader( PlaceDetailsActivity activity ) {
			mActivityReference = new WeakReference<PlaceDetailsActivity>(activity);
		}
		
		@Override
		protected Place doInBackground(PlaceSearchData... params) {
			return DataManager.getSingleton().getPlaceDetails(params[0].placeId,null, params[0].source);
		}
		
		@Override
		protected void onPostExecute(Place result) {
			if ( mActivityReference != null && mActivityReference.get() != null ) {
				mActivityReference.get().showPlace(result);
			}
	    }
	}
}