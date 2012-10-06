package edu.fiu.cs.seniorproject;

import android.app.Activity;
import android.os.Bundle;


import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;
import edu.fiu.cs.seniorproject.manager.DataManager.ConcurrentPlaceListLoader;
import edu.fiu.cs.seniorproject.utils.Logger;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.AsyncTask.Status;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class PlacesActivity extends Activity {

	private PlacesLoader mPlacesLoader = null;
	private List<Hashtable<String, String>> mPlaceList = null;

	private final OnItemClickListener mClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if ( mPlaceList != null && mPlaceList.size() > position ) {
				Hashtable<String, String> map = mPlaceList.get(position);
				
				if ( map != null ) {
					Intent intent = new Intent(PlacesActivity.this, PlaceDetailsActivity.class);
					if(map.get("reference") != null)
					{
						intent.putExtra("reference", map.get("reference"));
						intent.putExtra("source", SourceType.valueOf(map.get("source")));
					}
					PlacesActivity.this.startActivity(intent);
				}
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_list);
        
        mPlaceList = null;
        AppLocationManager.init(this);
        mPlacesLoader = new PlacesLoader(this);
        mPlacesLoader.execute();
    }

    @Override
    protected void onDestroy() {
    	if ( mPlacesLoader != null && mPlacesLoader.getStatus() != Status.FINISHED )
    		mPlacesLoader.cancelLoader();
    		mPlacesLoader.cancel(true);
    	mPlaceList = null;	// release memory
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.places_activity, menu);
        return true;
    }
    
    public void showPlaceList( List<Place> places ) {
    	
    	if ( this.mPlaceList == null ) {
	    	if ( places != null && places.size() > 0 ) {
	    		ListView lv = (ListView)findViewById(android.R.id.list);
	    		if ( lv != null ) {
	    			
	    			// create the grid item mapping
					String[] from = new String[] {"name", "address", "distance" };
					int[] to = new int[] { R.id.place_name, R.id.place_address, R.id.distance };
	
					this.mPlaceList = this.buildPlaceList(places);
					
					SimpleAdapter adapter = new SimpleAdapter(this, this.mPlaceList, R.layout.place_row, from, to);
					lv.setAdapter(adapter);
	    			lv.setVisibility(View.VISIBLE);
	    			lv.setOnItemClickListener(mClickListener);
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
    	} else {
    		ListView lv = (ListView)findViewById(android.R.id.list);
    		if ( lv != null && lv.getAdapter() != null ) {
    			List<Hashtable<String, String>> placeList = this.buildPlaceList(places);
        		if ( placeList != null ) {
        			this.mPlaceList.addAll(placeList);
        			((SimpleAdapter)lv.getAdapter()).notifyDataSetChanged();
        		}
    		}    		
    	}
    }
    
    private List<Hashtable<String, String>> buildPlaceList( List<Place> places ) {
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
		return placeList;
    }
    
    private class PlacesLoader extends AsyncTask<Void, List<Place>, Integer>
    {
    	private WeakReference<PlacesActivity> mActivityReference = null;
    	private ConcurrentPlaceListLoader mLoader = null;
    	
    	public PlacesLoader( PlacesActivity activity) {
    		mActivityReference = new WeakReference<PlacesActivity>(activity);
    	}
    	
    	public void cancelLoader() {
    		if ( mLoader != null ) {
    			mLoader.cancel();
    		}
    	}
		@SuppressWarnings("unchecked")
		@Override
		protected Integer doInBackground(Void... params) {
			android.location.Location currentLocation = AppLocationManager.getCurrentLocation();
			Location location = new Location( String.valueOf( currentLocation.getLatitude() ), String.valueOf(currentLocation.getLongitude()) );
			
			Integer total = 0;
			mLoader = DataManager.getSingleton().getConcurrentPlaceList(location, null, "500", null);
			
			if ( mLoader != null ) {
				List<Place> iter = null;
				while ( (iter = mLoader.getNext()) != null ) {
					total += iter.size();
					Logger.Debug("Add new set of data size = " + iter.size());
					this.publishProgress(iter);
				}
			}
			return total;
			//return DataManager.getSingleton().getPlaceList(location, null, "500", null);
		}
		
		@Override
		protected void onProgressUpdate(List<Place>... placeList) {
			if ( placeList != null && mActivityReference != null && mActivityReference.get() != null ) {
				for( int i = 0; i < placeList.length; i++ ) {
					mActivityReference.get().showPlaceList(placeList[i]);
				}
			}
		}
    	
		@Override
		protected void onPostExecute(Integer total) {
			Logger.Debug("Total records = " + total );			
		}
    }
}
