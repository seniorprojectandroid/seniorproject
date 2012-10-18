package edu.fiu.cs.seniorproject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Hashtable;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;


import edu.fiu.cs.seniorproject.data.Event;

import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;

public class PlaceDetailsActivity extends MapActivity {
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        AppLocationManager.init(this);
        
        Intent intent = getIntent();
        
        if ( intent != null && intent.hasExtra("id") && intent.hasExtra("source")) {
        	String placeId = intent.getStringExtra("id");
        	SourceType source =  (SourceType)intent.getSerializableExtra("source");
        	
        	(new PlaceDownloader(this)).execute(new PlaceSearchData(placeId, source));
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

	//settings click
    public void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    } 
    
	private void showPlace(Place place) {
		
		if ( place != null ) {
			//create place name.
			TextView name = (TextView)findViewById(R.id.place_name);
			if ( name != null ) {
				name.setText(place.getName());
			}
			
			//create PLACE description.
			TextView description = (TextView)findViewById(R.id.place_description);
			if ( description != null ) {
				description.setText(place.getDescription());
			}
			
			 
			if ( place.getImage() != null && !place.getImage().isEmpty() ) {
				//place image
				ImageView image = (ImageView)findViewById(R.id.place_image);
				if ( image != null ) {
					DataManager.getSingleton().downloadBitmap(place.getImage(), image);
				}
			}
			
			//place location 
			Location location = place.getLocation();
			if ( location != null ) {
				TextView placeLocation = (TextView)findViewById(R.id.place_location);
				if ( placeLocation != null ) {
					placeLocation.setText(location.getAddress());
				}
				
				//place distance.
				TextView distance = (TextView)findViewById(R.id.place_distance);
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
		    			
		    			List<Overlay> mapOverlaysList = map.getOverlays();
						Drawable drawable = this.getResources().getDrawable(R.drawable.red_pointer_icon);
						
						//creating an ItemizedOverlayActivity object so we can have multiple overlays
						//added to a list to show them in a map
					    ItemizedOverlayActivity itemizedoverlay = new ItemizedOverlayActivity(drawable, this);
					    
		    			GeoPoint geoPoint =  new GeoPoint( (int)(Double.valueOf( location.getLatitude() ) * 1E6),
		    			  (int)(Double.valueOf( location.getLongitude() ) * 1E6 ));
		    			
		    			// Creates an overlay item with a geopoint to show in the map
		    			 OverlayItem overlayitem = new OverlayItem(geoPoint, "Place", place.getName());
		    			
	    			    itemizedoverlay.addOverlay(overlayitem);
	    			    mapOverlaysList.add(itemizedoverlay);	    			    
		    			
		    			mc.setCenter(geoPoint);
		    			mc.setZoom(17);
		    		}
				}
				
				
				if(place.getEventsAtPlace() != null)
				{
					List<Event> events = place.getEventsAtPlace();	
					showEventList(events);
				
				}
			}
		}	
		
		
	}// end showPlace
	
	 private void showEventList( List<Event> eventList ) {
	    	
	    		
	    		if ( eventList != null && eventList.size() > 0 ) {
	    			
	    			LinearLayout ll = (LinearLayout)findViewById(android.R.id.list);
	    			
	    			if(ll!= null)
	    			{
	    				for(int i = 0; i < eventList.size(); i ++)
	    				{
	    					Event event = eventList.get(i);
	    					TextView tv = new TextView(this);
	    					tv.setText(event.getName());
	    					ll.addView(tv);
	    				}
	    			}
//		    		ListView lv = (ListView)findViewById(android.R.id.list);
//		    		if ( lv != null ) {
//		    			
//		    			// create the grid item mapping
//		    			String[] from = new String[] {"name", "place", "time", "distance" };
//						int[] to = new int[] { R.id.event_name, R.id.event_place, R.id.event_time, R.id.event_distance };
//
//						this.mEventList = this.buildEventMap(eventList);
//						
//						SimpleAdapter adapter = new SimpleAdapter(this, this.mEventList, R.layout.event_row, from, to);
//						lv.setAdapter(adapter);
//		    			lv.setVisibility(View.VISIBLE);
//		    		//	lv.setOnItemClickListener(mClickListener);
//		    		}
		    	} else {
		    		TextView tv = (TextView)findViewById(android.R.id.empty);
		    		if ( tv != null ) {
		    			tv.setVisibility(View.VISIBLE);
		    		}
		    	} 		
	    		
	    	
	}// end showEventList
	 
	 
	 
	private class PlaceSearchData {
		
		public String id;
		public SourceType source;
		
		public PlaceSearchData(String id, SourceType sourceType) {
			
			this.id = id;
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

			return DataManager.getSingleton().getPlaceDetails(params[0].id, params[0].source);
		}
		
		@Override
		protected void onPostExecute(Place result) {
			if ( mActivityReference != null && mActivityReference.get() != null ) {
				mActivityReference.get().showPlace(result);
			}
	    }
	}
}// end PlaceDetailActivity
