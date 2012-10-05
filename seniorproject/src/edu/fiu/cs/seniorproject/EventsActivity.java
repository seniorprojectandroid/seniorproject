package edu.fiu.cs.seniorproject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;
import edu.fiu.cs.seniorproject.manager.DataManager.ConcurrentEventListLoader;
import edu.fiu.cs.seniorproject.utils.Logger;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;

public class EventsActivity extends Activity {

	private EventLoader mEventLoader = null;
	private List<Hashtable<String, String>> mEventList = null;
	
	private final OnItemClickListener mClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if ( mEventList != null && mEventList.size() > position ) {
				Hashtable<String, String> map = mEventList.get(position);
				
				if ( map != null ) {
					Intent intent = new Intent(EventsActivity.this, EventDetailsActivity.class);
					intent.putExtra("event_id", map.get("event_id"));
					intent.putExtra("source", SourceType.valueOf(map.get("source")));
					EventsActivity.this.startActivity(intent);
				}
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        AppLocationManager.init(this);
        
        mEventLoader = new EventLoader(this);
        mEventLoader.execute();
    }

    @Override
    protected void onDestroy() {
    	if ( mEventLoader != null && mEventLoader.getStatus() != Status.FINISHED ) {
    		mEventLoader.cancelLoader();
    		mEventLoader.cancel(true);
    	}
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items_list, menu);
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

    private List<Hashtable<String, String>> buildEventMap(List<Event> eventList ) {
    	List<Hashtable<String, String>> fillMaps = new ArrayList<Hashtable<String, String>>(eventList.size());
		float[] distanceResults = new float[1];
		android.location.Location currentLocation = AppLocationManager.getCurrentLocation();
		DecimalFormat df = new DecimalFormat("#.#");
		
		for(int i = 0; i<eventList.size(); i++)
		{
			Event event = eventList.get(i);
			Hashtable<String, String> entry = new Hashtable<String, String>();
			entry.put("event_id", event.getId());
			entry.put("source", event.getSource().toString() );
			entry.put("name", event.getName() );						
			entry.put("time", DateFormat.format("EEEE, MMMM dd, h:mmaa", Long.valueOf( event.getTime() ) * 1000 ).toString() );
			
			Location location = event.getLocation();
			
			if ( location != null ) {
				entry.put("place", location.getAddress() );
				if ( currentLocation != null ) {
					android.location.Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()), distanceResults);
					double miles = distanceResults[0] / 1609.34;	// i mile = 1.60934km								
					entry.put("distance", df.format(miles) + "mi" );
				} else {
					entry.put("distance", "0mi");
				}
			}
			fillMaps.add(entry);
		}
		return fillMaps;
    }
    
    private void showEventList( List<Event> eventList ) {
    	if (this.mEventList == null ) {
    		
    		if ( eventList != null && eventList.size() > 0 ) {
	    		ListView lv = (ListView)findViewById(android.R.id.list);
	    		if ( lv != null ) {
	    			
	    			// create the grid item mapping
	    			String[] from = new String[] {"name", "place", "time", "distance" };
					int[] to = new int[] { R.id.event_name, R.id.event_place, R.id.event_time, R.id.event_distance };

					this.mEventList = this.buildEventMap(eventList);
					
					SimpleAdapter adapter = new SimpleAdapter(this, this.mEventList, R.layout.event_row, from, to);
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
    			List<Hashtable<String, String>> eventMap = this.buildEventMap(eventList);
        		if ( eventMap != null ) {
        			this.mEventList.addAll(eventMap);
        			((SimpleAdapter)lv.getAdapter()).notifyDataSetChanged();
        		}
    		}    		
    	}
    }
    
    private class EventLoader extends AsyncTask<Void, List<Event>, Integer> {

    	private final WeakReference<EventsActivity> mActivityReference;
    	private ConcurrentEventListLoader mLoader = null;
    	
    	public EventLoader(EventsActivity activity) {
    		mActivityReference = new WeakReference<EventsActivity>(activity);
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
			this.mLoader = DataManager.getSingleton().getConcurrentEventList(location, null, "2", null);
			
			if ( this.mLoader != null ) {
				List<Event> iter = null;
				while ( (iter = this.mLoader.getNext()) != null ) {
					total += iter.size();
					Logger.Debug("Add new set of data size = " + iter.size());
					this.publishProgress(iter);
				}
			}
			return total;
		}
    	
		@Override
		protected void onProgressUpdate(List<Event>... eventList) {
			if ( mActivityReference != null ) {
				EventsActivity activity = this.mActivityReference.get();
				if ( activity != null ) {
					for ( int i = 0; i < eventList.length; i++ ) {
						activity.showEventList(eventList[i]);
					}
				}
			}
		}
		
		@Override
		protected void onPostExecute(Integer total) {
			Logger.Debug("Total events = " + total );
		}
    }
}
