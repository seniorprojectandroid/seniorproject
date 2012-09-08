package edu.fiu.cs.seniorproject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.android.*;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.*;

public class FacebookLoginActivity extends Activity {

    private Facebook facebook = null;
    private AsyncFacebookRunner mAsyncRunner = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        facebook = new Facebook("378790605525916");
        mAsyncRunner = new AsyncFacebookRunner(facebook);
        
        facebook.authorize(this, new DialogListener() {
            @Override
            public void onComplete(Bundle values) {}

            @Override
            public void onFacebookError(FacebookError error) {}

            @Override
            public void onError(DialogError e) {}

            @Override
            public void onCancel() {}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
    public void onLogoutButtonClick(View view) {
    	
    	  mAsyncRunner.logout(view.getContext(), new RequestListener() {
			
			public void onMalformedURLException(MalformedURLException e, Object state) {
			}
			
			public void onIOException(IOException e, Object state) {
			}
			
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
			}
			
			public void onFacebookError(FacebookError e, Object state) {
			}
			
			public void onComplete(String response, Object state) {
			}
		});
    	
    }
}

