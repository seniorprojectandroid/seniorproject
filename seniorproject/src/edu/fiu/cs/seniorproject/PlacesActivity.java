package edu.fiu.cs.seniorproject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PlacesActivity extends Activity {

	private PlacesLoader mPlacesLoader = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_list);
        
        AppLocationManager.init(this);
        mPlacesLoader = new PlacesLoader(this);
        mPlacesLoader.execute();
    }

    @Override
    protected void onDestroy() {
    	if ( mPlacesLoader != null && mPlacesLoader.getStatus() != Status.FINISHED )
    		mPlacesLoader.cancel(true);
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.places_activity, menu);
        return true;
    }
    
    private void showPlaceList( List<Place> places ) {
    	if ( places != null && places.size() > 0 ) {
    		ListView lv = (ListView)findViewById(android.R.id.list);
    		if ( lv != null ) {
    			
    			// create the grid item mapping
				String[] from = new String[] {"name", "address", "distance" };
				int[] to = new int[] { R.id.place_name, R.id.place_address, R.id.distance };

				List<Hashtable<String, String>> placeList = new ArrayList<Hashtable<String,String>>(places.size());
				
				float[] distanceResults = new float[1];
				android.location.Location currentLocation = AppLocationManager.getCurrentLocation();
				DecimalFormat df = new DecimalFormat("#.#");
				
				for( int i = 0; i < places.size(); i++ ) {
					Hashtable<String, String> map = new Hashtable<String, String>();
					
					Place place = places.get(i);
					map.put("name", place.getName());
					
					Location location = place.getLocation();
					if ( location != null && currentLocation != null ) {
						map.put("address", location.getAddress() != null ? location.getAddress() : "No Address");
						
						android.location.Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()), distanceResults);
						double miles = distanceResults[0] / 1609.34;	// i mile = 1.60934km								
						map.put("distance", df.format(miles) + "mi" );
					}
					placeList.add(map);
				}
				
				SimpleAdapter adapter = new SimpleAdapter(this, placeList, R.layout.place_row, from, to);
				lv.setAdapter(adapter);
    			lv.setVisibility(View.VISIBLE);
    		}
    	} else {
    		TextView tv = (TextView)findViewById(android.R.id.empty);
    		if ( tv != null ) {
    			tv.setVisibility(View.VISIBLE);
    		}
    	}
    	
    	// Hide progress bar
    	ProgressBar pb = (ProgressBar)findViewById(android.R.id.progress);
    	if ( pb!= null ) {
    		pb.setVisibility(View.GONE);
    	}
    }
    
    private class PlacesLoader extends AsyncTask<Void, Void, List<Place>>
    {
    	private WeakReference<PlacesActivity> mActivityReference = null;
    	
    	public PlacesLoader( PlacesActivity activity) {
    		mActivityReference = new WeakReference<PlacesActivity>(activity);
    	}
    	
		@Override
		protected List<Place> doInBackground(Void... params) {
			android.location.Location currentLocation = AppLocationManager.getCurrentLocation();
			Location location = new Location( String.valueOf( currentLocation.getLatitude() ), String.valueOf(currentLocation.getLongitude()) );
			
			return DataManager.getSingleton().getPlaceList(location, null, "500", null);
		}
    	
		@Override
		protected void onPostExecute(List<Place> placeList) {
			if ( placeList != null && mActivityReference != null && mActivityReference.get() != null ) {
				mActivityReference.get().showPlaceList(placeList);
			}
		}
    }
}
