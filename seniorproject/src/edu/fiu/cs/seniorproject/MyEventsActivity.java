package edu.fiu.cs.seniorproject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.MbGuideDB;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.utils.BitmapSimpleAdapter;

public class MyEventsActivity extends ListActivity {
	
	MbGuideDB mb = new MbGuideDB(this); 
	ArrayList<String> eList = null;
	private List<Hashtable<String, String>> mEventList = null;
	private SimpleAdapter listAdapter = null;
	
	private final OnItemClickListener mClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			if ( mEventList != null && mEventList.size() > position ) {
				Hashtable<String, String> map = mEventList.get(position);
				
				if ( map != null ) {
					Intent intent = new Intent(MyEventsActivity.this, EventDetailsActivity.class);
					intent.putExtra("event_id", map.get("event_id"));
					intent.putExtra("source", SourceType.valueOf(map.get("source")));
					MyEventsActivity.this.startActivity(intent);
				}
			}
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        getActionBar().setDisplayHomeAsUpEnabled(true);     

        this.showEventList(this.getEventList());
        
    }

    
    private void showEventList( List<Event> eventList ) {
    
	    	if (this.mEventList == null ) {
	    		
	    		if (  eventList != null && eventList.size() > 0) {
	    			
		    		ListView lv = (ListView)findViewById(android.R.id.list);
		    		if ( lv != null ) {
		    			
		    			// create the grid item mapping
		    			String[] from = new String[] {"name", "place", "time", "distance" };
						int[] to = new int[] { R.id.event_name, R.id.event_place, R.id.event_time, R.id.event_distance };
	
						this.mEventList = this.buildEventMap(eventList);						
						this.listAdapter = new BitmapSimpleAdapter(this, this.mEventList, R.layout.event_row, from, to);
						lv.setAdapter(this.listAdapter);
		    			lv.setVisibility(View.VISIBLE);
		    			lv.setOnItemClickListener(mClickListener);
		    			
				    
		    		}	
		    		ProgressBar pb = (ProgressBar)findViewById(android.R.id.progress);
		    		if ( pb!= null ) {
			    		pb.setVisibility(View.GONE);
			    	}
		    	
		    		
	    		}
	    		else{
	    			
	    			TextView tv = (TextView)findViewById(android.R.id.empty);
	    			tv.setText("You have not added any events yet.");
	    			ProgressBar pb = (ProgressBar)findViewById(android.R.id.progress);
		    		if ( pb!= null ) {
			    		pb.setVisibility(View.GONE);
			    	}
	    		}
	    	} else {
	    		ListView lv = (ListView)findViewById(android.R.id.list);
	    		if ( lv != null && lv.getAdapter() != null ) {
	    			List<Hashtable<String, String>> eventMap = this.buildEventMap(eventList);
	        		if ( eventMap != null ) {
	        			this.mEventList.addAll(eventMap);
	        			this.listAdapter.notifyDataSetChanged();
	        		}
	    		}    		
	    	}
    	
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
			entry.put("id", event.getId());
			entry.put("event_id", event.getId());
			entry.put("source", event.getSource().toString() );
			entry.put("name", event.getName() );			
			entry.put("time", DateFormat.format("EEEE, MMMM dd, h:mmaa", Long.valueOf( event.getTime() ) * 1000 ).toString() );
			
			String image = event.getImage();
			if ( image != null && !image.isEmpty()) {
				entry.put("image", event.getImage());
			}
			
			Location location = event.getLocation();
			
			if ( location != null ) {
				//entry.put("place", location.getAddress() );
				entry.put("latitude", location.getLatitude());
				entry.put("longitude", location.getLongitude());
				
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

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_events, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.menu_settings:
                this.onSettingsClick(item);
                return true;
 
            case R.id.delete_from_calendar:
            	this.onDeleteAllEventsFromCalendarAndDBClick2(item);
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    }

    
    public void onDeleteAllEventsFromCalendarAndDBClick2(MenuItem view) { 	  	
    	
    	// Syncronize this to delete the event from both, DB and Calendar
    	//deleteAllEventsFromCalendarAndDB();
    }
    
    
//    
//    private void getEventNames()
//    {
//    	 
//    	try
//    	{
//    		mb.openDatabase();
//    		eList = mb.listEventNames(); 
//    	    mb.closeDatabase();
//    	}catch(SQLException e)
//    	 {
//    		e.printStackTrace();
//    	 }
//    	
//    	
//    }
//    
    
    private List<Event> getEventList()
    {
    	List<Event> eventList = null;
    	try
    	{
    		mb.openDatabase();
    		eventList = mb.getEventList(); 
    	    mb.closeDatabase();
    	}catch(SQLException e)
    	 {
    		e.printStackTrace();
    	 }
    	return eventList;
    	
    }
    
    
    
    
}
