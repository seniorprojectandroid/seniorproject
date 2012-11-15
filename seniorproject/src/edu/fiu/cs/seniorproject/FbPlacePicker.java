package edu.fiu.cs.seniorproject;

import java.util.ArrayList;
import java.util.List;

import com.facebook.FacebookActivity;
import com.facebook.PickerFragment.OnDoneButtonClickedListener;
import com.facebook.PickerFragment.OnSelectionChangedListener;
import com.facebook.GraphPlace;
import com.facebook.HttpMethod;
import com.facebook.PlacePickerFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.SessionState;

import edu.fiu.cs.seniorproject.utils.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class FbPlacePicker extends FacebookActivity {

	private PlacePickerFragment placePickerFragment = null;
	private GraphPlace selectedPlace = null;
	
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
		  
		  this.placePickerFragment.setOnSelectionChangedListener(new OnSelectionChangedListener() {
				@Override
				public void onSelectionChanged() {
					updateSelectedPlace();
				}
			});
		  
		  this.placePickerFragment.setOnDoneButtonClickedListener(new OnDoneButtonClickedListener() {
			@Override
			public void onDoneButtonClicked() {
				checkIn();
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
    
    private void updateSelectedPlace() {
    	this.selectedPlace = this.placePickerFragment.getSelection();
    	
    	if ( this.selectedPlace != null ) {
    		TextView tv = (TextView)findViewById(R.id.location);
    		if ( tv != null ) {
    			tv.setText(this.selectedPlace.getName());
    		}
    	}
    }
    
    private void checkIn() {
    	if ( this.selectedPlace == null ) {
    		Toast.makeText(this, "Select a place first", Toast.LENGTH_SHORT).show();
    	} else {
    		TextView tv = (TextView)findViewById(R.id.message);
    		if ( tv != null ) {
    			if ( tv.getText().toString().equals("") ) {
    				Toast.makeText(this, "Write a message!!", Toast.LENGTH_SHORT).show();
    			} else {
    				
    				Bundle params = new Bundle();
    				params.putString("message", tv.getText().toString() );
    				params.putString("place", this.selectedPlace.getId() );
    				
    				Request request = new Request(getSession(), "me/feed", params, HttpMethod.POST, new Request.Callback() {
						
						@Override
						public void onCompleted(Response response) {
							Logger.Debug("Response completed " + response.getError() );	
							FbPlacePicker.this.finish();
						}
					});
    				Request.executeBatchAsync(request);
    			}
    		}
    	}
    }
}
