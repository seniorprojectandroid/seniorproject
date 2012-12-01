package edu.fiu.cs.seniorproject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;
import edu.fiu.cs.seniorproject.utils.Logger;
import edu.fiu.cs.seniorproject.utils.XMLParser;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class TourActivityDetails extends Activity {
	
	private List<Place> tours = null;		
	private XMLParser parser = null;	
	private XmlResourceParser stringXmlContent = null;	
	private String tourName = null;
	private PlaceListPagerAdapter mAdapter;
	private ViewPager mPager;
	
	
	private List<Hashtable<String, String>> mPlaceList = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_deatils_pager);      
        
        AppLocationManager.init(this);
        
        Intent intent = getIntent();
        
        if ( intent != null && intent.hasExtra("tour")) {
        	tourName = intent.getStringExtra("tour");         	
        	tours = new ArrayList<Place>();  
            parser = new XMLParser();
       		stringXmlContent = parser.getXMLFromSRC(this,R.xml.tours);
       		tours = parser.getTourByName(stringXmlContent, tourName);   
       		
       		this.mPlaceList = this.buildPlaceMap(tours);
       		
       		this.mAdapter = new PlaceListPagerAdapter(this.getFragmentManager(), this.tours);
       		
       		this.mPager = (ViewPager) findViewById(R.id.pager);
       		if ( this.mPager != null ) {
       			this.mPager.setAdapter(this.mAdapter);
       		}
       		
       		Logger.Debug("list size" + tours.size());    	
        } 
        
    }// end onCreate  
    
    public void testActivity(List<Place> list)
    {
    	this.mAdapter = new PlaceListPagerAdapter(this.getFragmentManager(), list);
   		
   		this.mPager = (ViewPager) findViewById(R.id.pager);
   		if ( this.mPager != null ) {
   			this.mPager.setAdapter(this.mAdapter);
   		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tour_activity_details, menu);
        return true;
    }
    
    /*
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}*/
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		 tours = null;		
		 parser = null;
		 stringXmlContent = null;
	}
	
	
	
	 @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Logger.Debug("click on menu item = " + item.getItemId());
        switch (item.getItemId()) {
            
            case R.id.map_menuitem:
            	this.onEventsMapClick(item);
            	return true;
            case R.id.menu_settings:
                this.onSettingsClick(item);
                return true;    
        }
        return super.onOptionsItemSelected(item);
    }  
	 
	 public void onSettingsClick(MenuItem view) {
	    	Intent intent = new Intent(this, SettingsActivity.class);
	    	this.startActivity(intent);
	 } 
	 
	 public void onEventsMapClick( MenuItem menuItem)
    {
		 
    	if( tourName != null)
    		this.showEventsInMapView();
    	else
    	{
    		Dialog d= new Dialog(this);
        	d.setTitle(" No tour to show in the map!");       
        	d.show();
    	}
    }
	 
 // Method to show all events in a MapView 
    // It populates the static field locationsList
    public void showEventsInMapView(){    	
    	EventsMapViewActivity.locationsList = mPlaceList;
    	EventsMapViewActivity.actvtitle = this.tourName;
    	Intent intent = new Intent(this, EventsMapViewActivity.class);
    	TourActivityDetails.this.startActivity(intent);
    }
    
    private List<Hashtable<String, String>> buildPlaceMap(List<Place> placeList ) {
    	List<Hashtable<String, String>> fillMaps = new ArrayList<Hashtable<String, String>>(placeList.size());
				
		for(int i = 0; i<placeList.size(); i++)
		{
			Place place = placeList.get(i);
			Hashtable<String, String> entry = new Hashtable<String, String>();
			entry.put("name", place.getName() );
			
			Location location = place.getLocation();
			
			if ( location != null ) {				
				entry.put("latitude", location.getLatitude());
				entry.put("longitude", location.getLongitude());
			}
			fillMaps.add(entry);
		}
		return fillMaps;
    }// end buildPlaceMap
    
    
	
	// this is for the fragment
	public class PlaceListPagerAdapter extends FragmentStatePagerAdapter {
		private List<Place> mPlaceList;
		
		public PlaceListPagerAdapter(FragmentManager fm, List<Place> list) {
		    super(fm);
		    this.mPlaceList = list;
		}
		
		@Override
		public android.app.Fragment getItem(int i) {
			android.app.Fragment fragment = new PlaceDetailsFragment();
		    Place place = mPlaceList.get(i);
		    
		    Bundle args = new Bundle();
		    args.putString("image", place.getImage());
		    if(place.getImageBase64() != null)
		    {
		    	args.putString("imageBase64", place.getImageBase64());
		    }
		    args.putString("name", place.getName());
		    args.putString("telephone", place.getTelephone());
		    args.putString("description", place.getDescription());		    
		    args.putString("category", place.getCategory());
		    args.putString("latitude", place.getLocation().getLatitude());
		    args.putString("longitude", place.getLocation().getLongitude());		    
		    args.putString("address", place.getLocation() != null ? place.getLocation().getAddress() : "No address");
		    
		    fragment.setArguments(args);
		    return fragment;
		}
		
		@Override
		public int getCount() {
		    return mPlaceList.size();
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
		    //return "Place " + (position + 1) + " " + this.mPlaceList.get(position).getName();
			return  this.mPlaceList.get(position).getName();
		}
	}// end PlaceListPagerAdapter
	
	public static class PlaceDetailsFragment extends android.app.Fragment {
	    public static final String ARG_OBJECT = "object";

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        // The last two arguments ensure LayoutParams are inflated
	        // properly.
	       final View rootView = inflater.inflate(R.layout.activity_tour_activity_details, container, false);
	        
	        Bundle args = getArguments();
	        
	        ImageView image = (ImageView)rootView.findViewById(R.id.place_image);
			if (image != null ) {
				
				if (args.containsKey("imageBase64"))
				{
					Logger.Debug("imageBase64 = " + args.getString("imageBase64"));
					byte[] decodeString = Base64.decode(args.getString("imageBase64"), Base64.DEFAULT);
					Bitmap decodebyte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
					image.setImageBitmap(decodebyte);
				}
				else if (args.containsKey("image"))
				{
					DataManager.getSingleton().downloadBitmap(args.getString("image"),image);	
				}							
			}
	        
	        TextView name = (TextView)rootView.findViewById(R.id.tour_name);
			if ( name != null && args.containsKey("name") ) {
				name.setText( args.getString("name"));
			}
			
			TextView telephone = (TextView)rootView.findViewById(R.id.tour_telephone);
			if ( telephone != null && args.containsKey("telephone") ) {
				telephone.setText(args.getString("telephone"));
			}
			
			TextView description = (TextView)rootView.findViewById(R.id.tour_description);
			if ( description != null && args.containsKey("description") ) {
				description.setText(args.getString("description"));
			}
			
			//create place name.
			TextView category = (TextView)rootView.findViewById(R.id.tour_category);
			if ( category != null && args.containsKey("category")) {
				category.setText(args.getString("category"));
			}
			
			//create PLACE description.
			TextView address = (TextView)rootView.findViewById(R.id.tour_address);
			if ( address != null && args.containsKey("address")) {
				address.setText(args.getString("address"));
			}
			
			if(args.containsKey("latitude") && args.containsKey("longitude"))
			{
						
				image = (ImageView)rootView.findViewById(R.id.map_image);
				if (image != null ) {
					
					String latitude = args.getString("latitude");
					
					String longitude = args.getString("longitude");
					
					String url = "http://maps.googleapis.com/maps/api/staticmap?center=" + latitude + "," + longitude + "&zoom=18&size=400x400&sensor=true&" +
							"markers=color:blue|label:S|" + latitude + "," + longitude ;
					
					DataManager.getSingleton().downloadBitmap(url,image);
				}
			
			}// end if
			
	        return rootView;
	    } 
	    
	    
	}// end PlaceDetailsFragment


}// end TourActivityDetails










//private static Bitmap getBitmap(String bitmapUrl) {
//    try {
//      URL url = new URL(bitmapUrl);
//      return BitmapFactory.decodeStream(url.openConnection().getInputStream());
//    }
//    catch(Exception ex) {return null;}
//}
