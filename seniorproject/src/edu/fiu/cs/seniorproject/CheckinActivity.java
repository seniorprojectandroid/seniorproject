package edu.fiu.cs.seniorproject;

import java.util.List;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.manager.FacebookManager;
import edu.fiu.cs.seniorproject.manager.FacebookManager.IRequestResult;
import edu.fiu.cs.seniorproject.utils.Logger;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CheckinActivity extends Activity {

	private FacebookManager fManager = null;
	private List<Place> mPlaceList = null;
	private ProgressDialog mProgress = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
       
        
        Intent intent = getIntent();
        final String latitude = intent != null ? intent.getStringExtra("latitude") : null;
        final String longitude = intent != null ? intent.getStringExtra("longitude") : null;
        
        Logger.Debug("latitude = " + latitude + " longitude = " + longitude );   
        
        fManager = new FacebookManager();
        fManager.login(this, new String[] {"publish_stream"}, new IRequestResult() {
			
			@Override
			public void onComplete(boolean success) {
				if(success)
				{
					if ( latitude != null && longitude != null )
					{
						mProgress = ProgressDialog.show(CheckinActivity.this, "", "Loading. Please wait...", true);
						Location myLocation = new Location();
						myLocation.setLatitude(latitude);// ("25.7593282");
						myLocation.setLongitude(longitude);// ("-80.371674");
						new PlaceDownloader().execute(myLocation);
					}
				}
				else
				{
					Logger.Warning("Error Login facbook");
				}
				
			}
        });
        
    }// end onCreate // 25.7593282,-80.371674

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_checkin, menu);
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
    
    public void onSettingsClick(MenuItem view) {
    	Intent intent = new Intent(this, SettingsActivity.class);
    	this.startActivity(intent);
    } 
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        fManager.authorizeCallback(requestCode, resultCode, data);
    }
    
    private class FeedPublisher extends AsyncTask<Integer, Void, Void>
    {
    	@Override
	   	 protected Void doInBackground(Integer... params)
	   	 {
    		int myIndex = params[0];
    		Place myPlace = mPlaceList.get(myIndex);
    		fManager.PostFeed("Testing checkin!!!", myPlace.getId());
			return null;	
	   	 }
    	
    	 @Override
    	 protected void onPostExecute(Void result)
    	 { 
    		 Toast.makeText(CheckinActivity.this, "Checkin Posted..", Toast.LENGTH_SHORT ).show();
    	 }
    }// end FeedPublisher
    
    private class PlaceDownloader extends AsyncTask<Location, Void, List<Place>>
    {
    	 @Override
    	 protected List<Place> doInBackground(Location... params)
    	 {
    		 Location mylocation = params[0];
    		 return fManager.getPlacesAtLocation(mylocation, 150);   		 
    	 }
    	 
    	 @Override
    	 protected void onPostExecute(List<Place> myPlaceList)
    	 {
    		 if ( mProgress != null )
    		 {
    			 mProgress.dismiss();
    		 }
			if(myPlaceList != null && myPlaceList.size() > 0) 
			{
				mPlaceList = myPlaceList;
				String[] placesOnList = new String[myPlaceList.size()];
				for(int i = 0; i<myPlaceList.size(); i++)
				{
					placesOnList[i]=myPlaceList.get(i).getName();
				}
				
				ListView myListView = (ListView) findViewById(R.id.place_list);
				
				myListView.setOnItemClickListener(
						new OnItemClickListener()
						{
							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3)
							{
								// TODO Auto-generated method stub
								Logger.Debug("click on position " + arg2);
								new FeedPublisher().execute(arg2);
							}
						}
				);
				
		        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(CheckinActivity.this, R.layout.checkin_row,R.id.checkin_place_name,placesOnList);
		        myListView.setAdapter(myAdapter);
			}// end if
			
    	 }// end onPostExecute
    	 
    }// end PlaceDownloader
}
