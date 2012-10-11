package edu.fiu.cs.seniorproject;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class EventsMapViewActivity extends MapActivity {
	
	
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
	    
	    
	    // Part to instatiate and use Overlays
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.main_icon);
	    ItemizedOverlayActivity itemizedoverlay = new ItemizedOverlayActivity(drawable, this);
	    
	    
	    // Create a geoPoint and add it to the Overlay 
	    GeoPoint point = new GeoPoint(19240000,-99120000);
	    OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
	    
	    // Create a geoPoint and add it to the Overlay 
	    GeoPoint point2 = new GeoPoint(35410000, 139460000);
	    OverlayItem overlayitem2 = new OverlayItem(point2, "Sekai, konichiwa!", "I'm in Japan!");
	    
	    
	    // Add the overlay to the itemized overlay and 
	    // Add the Overlay to the overlay list
	    itemizedoverlay.addOverlay(overlayitem);
	    itemizedoverlay.addOverlay(overlayitem2);
	    mapOverlays.add(itemizedoverlay);
	    
	}
	

}
