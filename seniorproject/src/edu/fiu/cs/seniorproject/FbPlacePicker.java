package edu.fiu.cs.seniorproject;

import java.util.ArrayList;
import java.util.List;

import com.facebook.FacebookActivity;
import com.facebook.PickerFragment.OnDoneButtonClickedListener;
import com.facebook.PlacePickerFragment;
import com.facebook.SessionState;

import edu.fiu.cs.seniorproject.utils.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;

public class FbPlacePicker extends FacebookActivity {

	private PlacePickerFragment placePickerFragment = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_place_picker);    
        
        if ( this.getSession() != null ) {
        	this.closeSession();
        }
        
        Intent intent = this.getIntent();
  	  
	  if ( intent != null ) {
		  FragmentManager manager = this.getSupportFragmentManager();
		  if ( savedInstanceState == null ) {
			  this.placePickerFragment = new PlacePickerFragment(intent.getExtras());
		  } else {
			  this.placePickerFragment = (PlacePickerFragment)manager.findFragmentById(R.id.picker_fragment);
		  }
		  
		  if ( manager != null ) {
			  manager.beginTransaction().replace(R.id.picker_fragment, this.placePickerFragment).commit();
		  }		
		  
		  this.placePickerFragment.setOnDoneButtonClickedListener(new OnDoneButtonClickedListener() {
			
			@Override
			public void onDoneButtonClicked() {
				// TODO Auto-generated method stub
				Logger.Debug("On done");
			}
		});
	  }	  
	  
        List<String> permissions = new ArrayList<String>(1);
        permissions.add("publish_stream");
        
        this.openSessionForPublish(getResources().getString(R.string.fb_app_id), permissions );        
    }

    @Override
    protected void onSessionStateChange(SessionState state, Exception exception) {
      // user has either logged in or not ...
      if (state.isOpened()) {
    	  Logger.Debug("session is opened!!!");
    	  this.loadPlaces();
      } else {
    	  Logger.Debug("session is closed. exiting!!!");
    	  //this.finish();
      }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_fb_place_picker, menu);
        return true;
    }
    
    private void loadPlaces() {
    	if ( this.getSession() != null && this.getSession().isOpened() && this.placePickerFragment.getSession() != null ) {
    		this.placePickerFragment.loadData(true);
    	}
    }
}
