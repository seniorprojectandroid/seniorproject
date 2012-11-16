package edu.fiu.cs.seniorproject;

import java.util.ArrayList;
import java.util.List;

import com.facebook.FacebookActivity;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;

import edu.fiu.cs.seniorproject.utils.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class FbPublishFeedActivity extends FacebookActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_publish_feed);
        
        if ( this.getSession() != null ) {
        	this.closeSession();
        }
        
        Intent intent = this.getIntent();
        if ( intent != null ) {
        	TextView tv = (TextView)findViewById(R.id.title);
        	
        	if ( tv != null ) {
	        	if ( intent.hasExtra("title")) {
	        		tv.setText(intent.getStringExtra("title") );
	        	} else {
	        		tv.setText("Join me!!!");
	        	}
        	}
        	
        	tv = (TextView)findViewById(R.id.message);
        	if ( tv != null ) {
        		if ( intent.hasExtra("message")) {
	        		tv.setText(intent.getStringExtra("message") );
	        	} else {
	        		tv.setText("Would you like to join me at Miami Beach!!!!");
	        	}
        	}
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
      } else {
    	  Logger.Debug("session is closed!!!");
      }
    }
    
    public void onPostButtonClick(View v) {
    	Session session = this.getSession();
    	
    	if ( session != null && session.isOpened() ) {
    		this.publishFeed();
    	} else {
    		List<String> permissions = new ArrayList<String>(1);
            permissions.add("publish_stream");            
            this.openSessionForPublish(getResources().getString(R.string.fb_app_id), permissions );  
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_fb_publish_feed, menu);
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
    
    private void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    } 
    
    private void publishFeed() {
		Bundle params = new Bundle();
    	
		params.putString("name", "Miami Beach Guide");
		params.putString("link", "https://developers.facebook.com/android");
    	TextView tv = (TextView)findViewById(R.id.title);
    	if ( tv != null ) {
    		params.putString("caption", tv.getText().toString());
    	}
    	tv = (TextView)findViewById(R.id.message);
    	if ( tv != null ) {
    		params.putString("description", tv.getText().toString());
    	}
    	
    	Request request = new Request(getSession(), "me/feed", params, HttpMethod.POST, new Request.Callback() {
			@Override
			public void onCompleted(Response response) {
				Logger.Debug("Response completed " + response.getError() );	
				FbPublishFeedActivity.this.finish();
			}
		});
		Request.executeBatchAsync(request);
    }
}
