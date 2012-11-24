package edu.fiu.cs.seniorproject;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.fiu.cs.seniorproject.data.DateFilter;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.EventCategoryFilter;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.MbGuideDB;
import edu.fiu.cs.seniorproject.manager.AppLocationManager;
import edu.fiu.cs.seniorproject.manager.DataManager;
import edu.fiu.cs.seniorproject.manager.DataManager.ConcurrentEventListLoader;
import edu.fiu.cs.seniorproject.utils.Logger;

public class MainActivity extends Activity {

	MbGuideDB db;
	private ArrayList<Integer> hotdList =  new ArrayList<Integer>();
	private ArrayList<Integer> restdList =  new ArrayList<Integer>();	
	private ArrayList<Bitmap> hotBmList =  new ArrayList<Bitmap>();
	private ArrayList<Bitmap> restBmList =  new ArrayList<Bitmap>();
	HorizontalScrollView hv;
	HorizontalScrollView hv2;
	EventsActivity mAct;
	List <Event>mEventList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        addBitmaps();
       
        hv = (HorizontalScrollView)findViewById(R.id.horizontalScrollView1);
        hv2 = (HorizontalScrollView)findViewById(R.id.horizontalScrollView2);
        printPlaces();
        printEvents();

        db = new MbGuideDB(this);
        try
        {
        	db.openDatabase();
        	
        	if(!db.isUserPrefSet())
        	{
        		  Intent intent = new Intent(this, PersonalizationActivity.class);
        	      startActivity(intent);
        	}
        	db.closeDatabase();
        }
        catch(SQLException s)
        {
        	s.printStackTrace();
        	
        }
      
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                this.onSettingsClick(item);
                return true;            
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void onFacebookLoginButtonClick(View view) {
    	Intent intent = new Intent(this, FacebookLoginActivity.class);
    	this.startActivity(intent);
    }
    
    public void onShowGeoLocationClick(View view) {
    	Intent intent = new Intent(this, GeoLocationActivity.class);
    	this.startActivity(intent);
    }
    
    
    
    public void  onShowEventsClick(View view) {
    	Intent intent = new Intent(this, EventsActivity.class);
    	this.startActivity(intent);
    }
    
    public void  onPlacesButtonClick(View view) {
    	Intent intent = new Intent(this, PlacesActivity.class);
    	this.startActivity(intent);
    }
    
    public void onShowPlacesClick(View view) {
    	Intent intent = new Intent(this, PlacesActivity.class);
    	this.startActivity(intent);
    }
    
    public void onShowToursClick(View view) {
    	Intent intent = new Intent(this, TourActivity.class);
    	this.startActivity(intent);
    }
    
    public void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    }   
    
    public void onShowMyPlacesClick(View view) {
    	Intent intent = new Intent(this, MyPlacesActivity.class);
    	this.startActivity(intent);
    }
    public void onShowMyEventsClick (View view) {

    	Intent intent = new Intent(this,MyEventsActivity.class); 
    	//Intent intent = new Intent(this,PersonalizationActivity.class); 
    	this.startActivity(intent);

    }
    public void onItemClicked (View view) {
    	//Intent intent = new Intent(this,MyEventsActivity.class); 
    	Intent intent = new Intent(this,PersonalizationActivity.class); 
    	this.startActivity(intent);	
    }
    
    private void printPlaces()
    {
    	 LayoutInflater inflater = (LayoutInflater)this.getLayoutInflater();
    	 LinearLayout ly = (LinearLayout)findViewById(R.id.child_linear_layout);
    	 int size  = getHotelBitmapList().size();
    	 
	    for(int i=0; i<size; i++)
	    {
	    	View v = (View)inflater.inflate(R.layout.image_box_linear, null);
	    	ImageView iv =(ImageView) v.findViewById(R.id.image);
	    	TextView mtv = (TextView) v.findViewById(R.id.text);       	
	    	
	    	if(iv!=null){
	    		if(hotBmList!= null)
	    			iv.setImageBitmap(getHotelBitmapList().get(i));
	    		mtv.setText("Hello "+i);
	    	}   
	    	ly.addView(v);
	    }
    }
    
    private void printEvents()
    {
    	 LayoutInflater inflater = (LayoutInflater)this.getLayoutInflater();
    	 LinearLayout ly = (LinearLayout)findViewById(R.id.child_linear_layout2);
    	 int size  = getHotelBitmapList().size();
    	 
    	 mAct = new EventsActivity();
        // mEventList = EventsActivity.mPendingEventList;
       
    	 
	    for(int i=0; i<size; i++)
	    {
	    	View v = (View)inflater.inflate(R.layout.image_box_linear, null);
	    	ImageView iv =(ImageView) v.findViewById(R.id.image);
	    	TextView mtv = (TextView) v.findViewById(R.id.text);       	
	    	
	    	if(iv!=null){
	    		if(restBmList!= null)
	    			iv.setImageBitmap(getRestaurantBitmapList().get(i));
	    		  if(mEventList !=null)
	    	      {
	    	         mtv.setText(mEventList.get(i).getName());
	    	      }
	    		  else
	    			  mtv.setText("Hello "+i);
	    	}   
	    	ly.addView(v);
	    }
    }
    
    
    public void addBitmaps()
	{
		getHotelImages();
		getRestaurantImages();
		//getRestaurantImages();
		for(int i=0; i<9; i++)
		{
			Bitmap bm = (Bitmap)BitmapFactory.decodeResource(this.getResources(), hotdList.get(i));
			hotBmList.add(bm);	   
			
			Bitmap bm2 = (Bitmap)BitmapFactory.decodeResource(this.getResources(), restdList.get(i));
			restBmList.add(bm2);	
		
		}
		
	}
	private void getHotelImages()
	{	
			hotdList.add(R.drawable.hot1);		
			hotdList.add(R.drawable.hot2);
			hotdList.add(R.drawable.hot3);
			hotdList.add(R.drawable.hot4);
			hotdList.add(R.drawable.hot5);
			hotdList.add(R.drawable.hot6);
			hotdList.add(R.drawable.hot7);
			hotdList.add(R.drawable.hot8);
			hotdList.add(R.drawable.hot9);
			hotdList.add(R.drawable.hot10);
			hotdList.add(R.drawable.hot11);
			hotdList.add(R.drawable.hot12);				
	}
	private void getRestaurantImages()
	{
		restdList.add(R.drawable.rest1);		
		restdList.add(R.drawable.rest2);
		restdList.add(R.drawable.rest3);
		restdList.add(R.drawable.rest4);
		restdList.add(R.drawable.rest5);
		restdList.add(R.drawable.rest6);
		restdList.add(R.drawable.rest7);
		restdList.add(R.drawable.rest8);
		restdList.add(R.drawable.rest9);
		//restdList.add(R.drawable.rest10);
		//restdList.add(R.drawable.rest11);
		//restdList.add(R.drawable.rest12);			
	}
	
	public ArrayList<Bitmap> getHotelBitmapList()
	{
		return hotBmList;
	}
	
	public ArrayList<Bitmap> getRestaurantBitmapList()
	{
		return restBmList;
	}
	
	
}

	
    
    

