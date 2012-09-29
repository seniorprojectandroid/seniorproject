package edu.fiu.cs.seniorproject;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;

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
	private List<Event> mEventlist = null;
	
	private final OnItemClickListener mClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if ( mEventlist != null && mEventlist.size() > position ) {
				Event targetEvent = mEventlist.get(position);
				Intent intent = new Intent(EventsActivity.this, EventDetailsActivity.class);
				intent.putExtra("event_id", targetEvent.getId());
				intent.putExtra("source", targetEvent.getSource());
				EventsActivity.this.startActivity(intent);
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        AppLocationManager.init(this);
        
        mEventLoader = new EventLoader();
        mEventLoader.execute();
    }

    @Override
    protected void onDestroy() {
    	if ( mEventLoader != null && mEventLoader.getStatus() != Status.FINISHED )
    	mEventLoader.cancel(true);
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

    private class EventLoader extends AsyncTask<Void, Void, List<Event>> {

    	private final WeakReference<ProgressBar> mProgressBar = new WeakReference<ProgressBar>( (ProgressBar)findViewById(android.R.id.progress));
    	private final WeakReference<ListView> mListView = new WeakReference<ListView>((ListView)findViewById(android.R.id.list) );
    	private final WeakReference<TextView> mTextView = new WeakReference<TextView>((TextView)findViewById(android.R.id.empty));
		
    	@Override
		protected List<Event> doInBackground(Void... params) {
			return DataManager.getSingleton().getEventList(null, null, null, null);
		}
    	
		@Override
		protected void onPostExecute(List<Event> eventList) {
			if ( eventList != null && eventList.size() > 0 ) {
				if ( mListView != null && mListView.get() != null ) {
					
					mEventlist = eventList;	// store the event list
					
					// create the grid item mapping
					String[] from = new String[] {"name", "place", "time", "distance" };
					int[] to = new int[] { R.id.event_name, R.id.event_place, R.id.event_time, R.id.event_distance };

					List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
					float[] distanceResults = new float[1];
					android.location.Location currentLocation = AppLocationManager.getCurrentLocation();
					DecimalFormat df = new DecimalFormat("#.#");
					
					for(int i = 0; i<eventList.size(); i++)
					{
						Event event = eventList.get(i);
						HashMap<String, String> entry = new HashMap<String, String>();
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
					SimpleAdapter adapter = new SimpleAdapter(EventsActivity.this, fillMaps, R.layout.event_row, from, to);
					mListView.get().setAdapter(adapter);
					mListView.get().setVisibility(View.VISIBLE);
					mListView.get().setOnItemClickListener(mClickListener);
				}
			} else {
				if ( mTextView != null && mTextView.get() != null ) {
					mTextView.get().setText("No Events..");
					mTextView.get().setVisibility(View.VISIBLE);
				}
			}
			
			if ( mProgressBar != null && mProgressBar.get() != null ) {
				mProgressBar.get().setVisibility(View.GONE);	// hide progress bar
			}
		}
    }
}
