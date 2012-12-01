package edu.fiu.cs.seniorproject;

import java.util.Hashtable;
import java.util.List;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class EventsMapViewActivity extends MapActivity {
	
	
	
		
	public static List<Hashtable<String,String>> locationsList = null;	
	public static String actvtitle = null;
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_events_mapview);
	    MapView mapView = (MapView) findViewById(R.id.events_mapview); 
	    mapView.setBuiltInZoomControls(true); 
	    this.setTitle(actvtitle);
	    
	    
	    // Part to instatiate and use Overlays
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.red_pointer_icon);
	    ItemizedOverlayActivity itemizedoverlay = new ItemizedOverlayActivity(drawable, this);
	    
	    
	    //Here is where the logic for getting the data of location for all events
	    
	       
		    
		    for (Hashtable<String, String> iter : locationsList) {
		    	
				int latitude = (int)(Double.valueOf(iter.get("latitude"))* 1E6);
				int longitude = (int)(Double.valueOf(iter.get("longitude"))* 1E6);
				String eventName = iter.get("name");
			
				
				
				GeoPoint geoPoint = new GeoPoint(latitude, longitude);
				
				
				OverlayItem overlayitem = new OverlayItem(geoPoint, "This is ", eventName);
			
				itemizedoverlay.addOverlay(overlayitem);
				
				
				
			}  
		    
        
	    
	    
	    mapOverlays.add(itemizedoverlay);
	    
	}
	
	@Override
	public void onDestroy() {
		locationsList = null;	// free memory
		super.onDestroy();
	}
	

}
