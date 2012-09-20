package edu.fiu.cs.seniorproject;

import java.util.List;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.manager.FacebookManager;
import edu.fiu.cs.seniorproject.manager.FacebookManager.IRequestResult;
import edu.fiu.cs.seniorproject.utils.Logger;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CheckinActivity extends Activity {

	private FacebookManager fManager = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin);
        
//        String[] myList = new String[]{"Rey", "Gato", "Julio", "Robeto" , "Quien falta aqui."};
//        ListView myListView = (ListView) findViewById(R.id.place_list);
//        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.checkin_row,R.id.checkin_place_name,myList);
//        myListView.setAdapter(myAdapter);
        
        fManager = new FacebookManager();
        fManager.login(this, new IRequestResult() {
			
			@Override
			public void onComplete(boolean success) {
				if(success)
				{
					Location myLocation = new Location();
					myLocation.setLatitude("25.7593282");
					myLocation.setLongitude("-80.371674");
					new PlaceDownloader().execute(myLocation);
				}
				else
				{
					Logger.Warning("Error Login facbook");
				}
				
			}
        });
        
    }// 25.7593282,-80.371674

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_checkin, menu);
        return true;
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fManager.authorizeCallback(requestCode, resultCode, data);
    }
    
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
			if(myPlaceList != null && myPlaceList.size() > 0) 
			{
				String[] placesOnList = new String[myPlaceList.size()];
				for(int i = 0; i<myPlaceList.size(); i++)
				{
					placesOnList[i]=myPlaceList.get(i).getName();
					
				}
				
				ListView myListView = (ListView) findViewById(R.id.place_list);
		        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(CheckinActivity.this, R.layout.checkin_row,R.id.checkin_place_name,placesOnList);
		        myListView.setAdapter(myAdapter);
			}
    	 }
    }
}
