package edu.fiu.cs.seniorproject;

import com.facebook.GraphUser;
import com.facebook.Request;
import com.facebook.FacebookActivity;
import com.facebook.Response;
import com.facebook.SessionState;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FacebookLoginActivity extends FacebookActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        this.openSession();       
        
        SessionState state = this.getSessionState();
        if ( state != null && state.isOpened() ) {
        	this.getUserInfo();
        }
    }  
  
    @Override
    protected void onSessionStateChange(SessionState state, Exception exception) {
      // user has either logged in or not ...
      if (state.isOpened()) {
    	  this.getUserInfo();
      }
    }

    private void getUserInfo() {
    	// make request to the /me API
        Request request = Request.newMeRequest(this.getSession(), new Request.GraphUserCallback() {
			
        	 // callback after Graph API response with user object
            @Override
            public void onCompleted(GraphUser user, Response response) {
              if (user != null) {
                TextView welcome = (TextView) findViewById(R.id.textView2);
                
                if ( welcome != null ) {
                	welcome.setText("Hello " + user.getName() + "!");
                }
              }
            }
		});
        Request.executeBatchAsync(request);
    }
    
    public void onLogoutButtonClick(View view) {    	
    }
}

