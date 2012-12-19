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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.MbGuideDB;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.utils.BitmapSimpleAdapter;

public class MyPlacesActivity extends ListActivity {

	MbGuideDB mb = new MbGuideDB(MyPlacesActivity.this); 
	ArrayList<String> pList = null;
	
	private List<Hashtable<String, String>> mPlaceList = null;
	private SimpleAdapter listAdapter = null;
	
	private final OnItemClickListener mClickListener = new OnItemClickListener() 
	{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
		{
			if ( mPlaceList != null && mPlaceList.size() > position ) 
			{
				Hashtable<String, String> map = mPlaceList.get(position);
				
				if ( map != null && map.containsKey("id") && map.containsKey("source")) 
				{
					Intent intent = new Intent(MyPlacesActivity.this, PlaceDetailsActivity.class);
					
					intent.putExtra("id", map.get("id"));
					intent.putExtra("source", SourceType.valueOf(map.get("source")));
					
					MyPlacesActivity.this.startActivity(intent);
				}
			}
		}
	};
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
       
       this.showPlaceList2(getPlaceList());        
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_places, menu);
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
 
            case R.id.delete_all_from_calendar:
            	
            	if(pList != null)
            	{
	            	this.onDeleteAllEventsFromCalendarAndDBClick2(item);
	            	return true;
            	}
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    }
    
 public void showPlaceList2( List<Place> places ) {
    	
    	if ( this.mPlaceList == null ) {
	    	if ( places != null && places.size() > 0 ) {
	    		ListView lv = (ListView)findViewById(android.R.id.list);
	    		if ( lv != null ) {
	    			
	    			// create the grid item mapping
					String[] from = new String[] {"name", "address", "distance" };
					int[] to = new int[] { R.id.place_name, R.id.place_address, R.id.distance };
	
					this.mPlaceList = this.buildPlaceList(places);
					
					this.listAdapter = new BitmapSimpleAdapter(this, this.mPlaceList, R.layout.place_row, from, to);
					lv.setAdapter(this.listAdapter);
	    			lv.setVisibility(View.VISIBLE);
	    			lv.setOnItemClickListener(mClickListener);
	    		}
	    		
	    		// Hide progress bar
		    	ProgressBar pb = (ProgressBar)findViewById(android.R.id.progress);
		    	if ( pb!= null ) {
		    		pb.setVisibility(View.GONE);
		    	}
	    	}else{
	    		
	    		TextView tv = (TextView)findViewById(android.R.id.empty);
    			tv.setText("You have not added any places yet.");
    			ProgressBar pb = (ProgressBar)findViewById(android.R.id.progress);
	    		if ( pb!= null ) {
		    		pb.setVisibility(View.GONE);
		    	}
		    
	    	}
    	} else {
    		ListView lv = (ListView)findViewById(android.R.id.list);
    		if ( lv != null && lv.getAdapter() != null ) {
    			List<Hashtable<String, String>> placeList = this.buildPlaceList(places);
        		if ( placeList != null ) {
        			this.mPlaceList.addAll(placeList);
        			if ( this.listAdapter != null ) {
        				this.listAdapter.notifyDataSetChanged();
        			}
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
			map.put("id", place.getId());
			map.put("name", place.getName());
			map.put("source", place.getSource().toString());
			
			if ( place.getImage() != null ) {
				map.put("image", place.getImage());
			}
			
			Location location = place.getLocation();
			if ( location != null && currentLocation != null ) {		

				map.put("latitude", location.getLatitude());
				map.put("longitude",location.getLongitude());
				
				map.put("address", location.getAddress() != null ? location.getAddress() : "No Address");
				
				android.location.Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), Double.valueOf(location.getLatitude()), Double.valueOf(location.getLongitude()), distanceResults);
				double miles = distanceResults[0] / 1609.34;	// i mile = 1.60934km								
				map.put("distance", df.format(miles) + "mi" );
				
				placeList.add(map);
			}
		}
		return placeList;
    }

    
    public void onDeleteAllEventsFromCalendarAndDBClick2(MenuItem view) { 	  	
    	
    	
    	if(pList!=null)
    	{
	    	deleteAllPlacesFromDB();
	    	pList=null;
	    	Intent intent = new Intent(this, MyPlacesActivity.class);
	    	this.startActivity(intent);
	    	
    	}
    	else
    	{
    		
    	}
    }    
    
    
    private List<Place> getPlaceList()
    {  
    	List<Place> placeList = null;
    	try
    	{
    		mb.openDatabase();
    		placeList = mb.getPlaceList(); 
    	    mb.closeDatabase();
    	}catch(SQLException e)
    	 {
    		pList = null;
    		e.printStackTrace();
    	 }   
    	
    	return placeList;    	
    }
    
    
    private void deleteAllPlacesFromDB()
    {    	 
    	try
    	{
    		mb.openDatabase();
    		mb.deleteAllPlaces();
    	    mb.closeDatabase();
    	}catch(SQLException e)
    	 {
    		e.printStackTrace();
    	 }    	
    	
    }  	
    
}
