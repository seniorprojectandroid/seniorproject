package edu.fiu.cs.seniorproject;

import com.codegoogle.twitterandroid.TwitterApp;
import com.codegoogle.twitterandroid.TwitterAuthListener;

import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.utils.Logger;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class UpdateTwitterStatusActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_twitter_status);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        final TwitterApp twitter = new TwitterApp(this, AppConfig.TWITTER_CONSUMER_KEY, AppConfig.TWITTER_CONSUMER_SECRET);
        twitter.setListener(new TwitterAuthListener() {
          @Override
          public void onError(String value) {
            Toast.makeText(UpdateTwitterStatusActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
            Logger.Error("Twitter login failed");
            twitter.resetAccessToken();
          }
          @Override
          public void onComplete(String value) {
            tweetWithValidAuth(twitter);
          }
        });
        
        Button tweetButton = (Button) findViewById(R.id.postButton);
        tweetButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            twitter.resetAccessToken();
            if (twitter.hasAccessToken()) {
              tweetWithValidAuth(twitter);
            } else {
              twitter.authorize();
            }
          }
        });
        
        Intent intent = getIntent();
        if ( intent != null && intent.hasExtra("message")) {
        	EditText tweetTextView = (EditText) findViewById(R.id.message);
        	if ( tweetTextView != null ) {
        		tweetTextView.setText( intent.getStringExtra("message"));
        	}
        }
    }

    private void tweetWithValidAuth(TwitterApp twitter) {
        try {
          EditText tweetTextView = (EditText) findViewById(R.id.message);
          
          if ( tweetTextView != null ) {
	          String tweet = tweetTextView.getText().toString();
	          twitter.updateStatus(tweet);
	          Toast.makeText(this, "Posted Successfully", Toast.LENGTH_LONG).show();
          }
        } catch (Exception e) {
          if (e != null && e.getMessage() != null ) {
        	  String message = e.getMessage().toString();
        	  if ( message != null && message.contains("duplicate")) {
        		  Toast.makeText(this, "Post failed because of duplicates...", Toast.LENGTH_LONG).show();
        	  }
          }
        }
        twitter.resetAccessToken();
      }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_update_twitter_status, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
