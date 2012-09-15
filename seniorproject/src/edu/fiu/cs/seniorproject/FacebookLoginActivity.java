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

import edu.fiu.cs.seniorproject.utils.Logger;

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
            public void onComplete(Bundle values) {
            	Logger.Info("authorize completed!!!");
            }

            @Override
            public void onFacebookError(FacebookError error) {
            	Logger.Error("Error authorizing facebook!!!");
            }

            @Override
            public void onError(DialogError e) {
            	Logger.Error("Error showing dialog!!!");
            }

            @Override
            public void onCancel() {
            	Logger.Warning("User canceled to authorization!!!");
            }
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
				Logger.Error("onMalformedURLException");
			}
			
			public void onIOException(IOException e, Object state) {
				Logger.Error("onIOException");
			}
			
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
				Logger.Error("onFileNotFoundException");
			}
			
			public void onFacebookError(FacebookError e, Object state) {
				Logger.Error("onFacebookError");
			}
			
			public void onComplete(String response, Object state) {
				Logger.Info("logout completed");
			}
		});
    	
    }
}

