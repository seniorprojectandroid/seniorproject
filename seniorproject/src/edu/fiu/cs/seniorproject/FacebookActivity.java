package edu.fiu.cs.seniorproject;

import java.util.List;

import com.facebook.Session;
import com.facebook.SessionState;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FacebookActivity extends FragmentActivity {
	protected static final int REAUTH_ACTIVITY_CODE = 100;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    Session.openActiveSession(this, true, new Session.StatusCallback() {

            // callback when session changes state
            @Override
            public void call(Session session, SessionState state, Exception exception) {
            	FacebookActivity.this.onSessionStateChange(state, exception);
            }
	    });
	}
	
	protected void onSessionStateChange(SessionState state, Exception exception) {
		if ( state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
			this.onTokenUpdated();
		}
	}
	
	public Session getSession() {
		return Session.getActiveSession();
	}
	
	public void closeSession() {
		this.getSession().close();
	}
	
	public void requestPermissions(List<String> permissions) {
		Session session = this.getSession();
		if (session != null) {
	        Session.NewPermissionsRequest newPermissionsRequest = 
	            new Session.NewPermissionsRequest(this, permissions).setRequestCode(REAUTH_ACTIVITY_CODE);
	        session.requestNewPublishPermissions(newPermissionsRequest);
	    }
	}
	
	protected void onTokenUpdated() {		
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }
}
