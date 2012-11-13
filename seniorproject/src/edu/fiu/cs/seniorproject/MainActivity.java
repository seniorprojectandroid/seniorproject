package edu.fiu.cs.seniorproject;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.fiu.cs.seniorproject.data.MbGuideDB;

public class MainActivity extends Activity {

	MbGuideDB db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
        
        // scroll view
     
        
        
        TextView tv = new TextView(this);
        tv.setText("hello");
        
        
        TextView tv2 = new TextView(this);
        tv2.setText("hello2");
        TextView tv3 = new TextView(this);
        tv3.setText("hello3");
        TextView tv4 = new TextView(this);
        tv4.setText("hello4");
        TextView tv5 = new TextView(this);
        tv5.setText("hello5");
        
        
        
        HorizontalScrollView hv = (HorizontalScrollView)findViewById(R.id.horizontalScrollView1);
        ArrayList<TextView> l = new ArrayList<TextView>();
        
        
        LinearLayout ly = (LinearLayout)findViewById(R.id.child_linear_layout);
        
        LayoutInflater inflater = (LayoutInflater)this.getLayoutInflater();
        ImageView image = (ImageView)findViewById(R.id.image);
        this.getResources().getDrawable(R.drawable.facebook_dark_96);
        // image.setImageBitmap( this.getResources().getDrawable(R.drawable.facebook_dark_96));
        
        Bitmap bm = (Bitmap)BitmapFactory.decodeResource(this.getResources(), R.drawable.facebook_dark_96); 
      //  image.setImageBitmap(bm);
       //  image.setImageDrawable(this.getResources().getDrawable(R.drawable.facebook_dark_96));
         
         
       // image.setImageResource(R.drawable.facebook_dark_96);
        
        for(int i=0; i<20; i++)
        {      		
        	View v = (View)inflater.inflate(R.layout.image_box_linear, null);
        	ImageView iv =(ImageView) v.findViewById(R.id.image);
        	TextView mtv = (TextView) v.findViewById(R.id.text);
            if(iv!=null)
            {
            	iv.setImageBitmap(bm);
            	mtv.setText("Hello "+i);
            }    
            
       // View v2 = (View)inflater.inflate(R.layout.image_box_linear, null);
        	//ly.addView(iv);
            ly.addView(v);
        }


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
    	//Intent intent = new Intent(this,MyEventsActivity.class); 
    	Intent intent = new Intent(this,MyEventsActivity.class); 
    	this.startActivity(intent);	
    }
    public void onItemClicked (View view) {
    	//Intent intent = new Intent(this,MyEventsActivity.class); 
    	Intent intent = new Intent(this,PersonalizationActivity.class); 
    	this.startActivity(intent);	
    }
    
}
