package edu.fiu.cs.seniorproject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
//import edu.fiu.cs.seniorproject.EventsActivity.EventLoader;
//import edu.fiu.cs.seniorproject.GooglePlaces.PlaceLoader;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.provider.GPProvider;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;
import java.util.List;


//public class EventsActivity extends Activity {
//
//	private EventLoader mEventLoader = null;
//	private List<Event> mEventlist = null;
//	
//	private final OnItemClickListener mClickListener = new OnItemClickListener() {
//		@Override
//		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//			if ( mEventlist != null && mEventlist.size() > position ) {
//				Event targetEvent = mEventlist.get(position);
//				Intent intent = new Intent(EventsActivity.this, EventDetailsActivity.class);
//				intent.putExtra("event_id", targetEvent.getId());
//				intent.putExtra("source", targetEvent.getSource());
//				EventsActivity.this.startActivity(intent);
//			}
//		}
//	};
//	
//
//    @Override
//    protected void onDestroy() {
//    	if ( mEventLoader != null && mEventLoader.getStatus() != Status.FINISHED )
//    	mEventLoader.cancel(true);
//    	super.onDestroy();
//    }
//    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.items_list, menu);
//        return true;
//    }
//
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

public class GooglePlaces extends Activity {

//	private PlaceLoader gPlaceLoader = null;
//	private List<Place> gPlacelist = null;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_places);
//       
////      @Override
////      public void onCreate(Bundle savedInstanceState) {
////          super.onCreate(savedInstanceState);
////          setContentView(R.layout.items_list);
////          getActionBar().setDisplayHomeAsUpEnabled(true);
////          AppLocationManager.init(this);
////          
////          mEventLoader = new EventLoader();
////          mEventLoader.execute();
////      }
//        
//        ListView lv = (ListView)findViewById(R.id.google_places_list);
//        
//        if(lv != null)
//        {
//        	lv.setAdapter(adapter);
//        }
//        
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_google_places, menu);
//        return true;
//    }
//   
//
//    private class PlaceLoader extends AsyncTask<Void, Void, List<Place>> {
//
//    	
//    	private final WeakReference<ProgressBar> mProgressBar = new WeakReference<ProgressBar>((ProgressBar)findViewById(android.R.id.progress));
//    	private final WeakReference<ListView> mListView = new WeakReference<ListView>((ListView)findViewById(android.R.id.list));
//    	private final WeakReference<TextView> mTextView = new WeakReference<TextView>((TextView)findViewById(android.R.id.empty));
//		
//    	@Override
//		protected List<Place> doInBackground(Void... params) {
//			return DataManager.getSingleton().getPlaceList(null, null, null, null);
//		}
//    	
//		@Override
//		protected void onPostExecute(List<Place> placeList) {
//			if ( placeList != null && placeList.size() > 0 ) {
//				if ( mListView != null && mListView.get() != null ) {
//					
//					gPlacelist = placeList;	// store the event list
//					
//					// create the grid item mapping
//					String[] from = new String[] {"name", "place", "distance" };
//					int[] to = new int[] { R.id.event_name, R.id.event_place, R.id.event_distance };
//
//					List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
//					float[] distanceResults = new float[1];
//					android.location.Location currentLocation = AppLocationManager.getCurrentLocation();
//					DecimalFormat df = new DecimalFormat("#.#");
//					
//					for(int i = 0; i<placeList.size(); i++)
//					{
//						Place place = placeList.get(i);
//						HashMap<String, String> entry = new HashMap<String, String>();
//						entry.put("name", place.getName() );						
//						//entry.put("time", DateFormat.format("EEEE, MMMM dd, h:mmaa", Long.valueOf( place.getTime() ) * 1000 ).toString() );
//						
//						Location location = place.getLocation();
//						
//						if ( location != null ) {
//							entry.put("place", location.getAddress() );
//							if ( currentLocation != null ) {
//								android.location.Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()), distanceResults);
//								double miles = distanceResults[0] / 1609.34;	// i mile = 1.60934km								
//								entry.put("distance", df.format(miles) + "mi" );
//							} else {
//								entry.put("distance", "0mi");
//							}
//						}
//						fillMaps.add(entry);
//					}
//					SimpleAdapter adapter = new SimpleAdapter(GooglePlaces.this, fillMaps, R.layout.event_row, from, to);
//					mListView.get().setAdapter(adapter);
//					mListView.get().setVisibility(View.VISIBLE);
//					mListView.get().setOnItemClickListener(mClickListener);
//				}
//			} else {
//				if ( mTextView != null && mTextView.get() != null ) {
//					mTextView.get().setText("No Events..");
//					mTextView.get().setVisibility(View.VISIBLE);
//				}
//			}
//			
//			if ( mProgressBar != null && mProgressBar.get() != null ) {
//				mProgressBar.get().setVisibility(View.GONE);	// hide progress bar
//			}
//		}
//    }
//	
	
}