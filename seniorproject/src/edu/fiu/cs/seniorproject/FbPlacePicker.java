package edu.fiu.cs.seniorproject;

import java.util.ArrayList;
import java.util.List;

import com.facebook.FacebookActivity;
import com.facebook.GraphPlace;
import com.facebook.HttpMethod;
import com.facebook.LoginFragment;
import com.facebook.PickerFragment.OnDoneButtonClickedListener;
import com.facebook.PickerFragment.OnSelectionChangedListener;
import com.facebook.PlacePickerFragment;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import edu.fiu.cs.seniorproject.utils.Logger;

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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {            
            case R.id.menu_logout:
            	this.onLoadOut();
            	return true;
            case R.id.menu_settings:
                this.onSettingsClick(item);
                return true;    
        }
        return super.onOptionsItemSelected(item);
    }  
    
    private void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    } 
    
    private void onLoadOut() {
    	Session session = this.getSession();
    	if ( session != null && session.isOpened() ) {
    		FragmentManager manager = this.getSupportFragmentManager();
    		if ( manager != null ) {
  			  manager.beginTransaction().replace(R.id.picker_fragment, new LoginFragment() ).commit();
  		  }    		
    	}
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
    	Resources resources = this.getResources();
    	if ( this.selectedPlace == null ) {
    		Toast.makeText(this, resources.getString(R.string.select_place_first), Toast.LENGTH_SHORT).show();
    	} else {
    		TextView tv = (TextView)findViewById(R.id.message);
    		if ( tv != null ) {
    			if ( tv.getText().toString().equals("") ) {
    				Toast.makeText(this, resources.getString(R.string.write_message), Toast.LENGTH_SHORT).show();
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
