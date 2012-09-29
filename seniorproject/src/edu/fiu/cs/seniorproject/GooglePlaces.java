package edu.fiu.cs.seniorproject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.provider.GPProvider;
import edu.fiu.cs.seniorproject.manager.DataManager;

import java.util.List;

public class GooglePlaces extends Activity {

	ArrayList<String> list = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_places);
        list.add("Hello");
        list.add("Hello1");
        list.add("Hello2");
        list.add("Hello3");
       
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.google_list,R.id.google_place_item_one,list);
        
        ListView lv = (ListView)findViewById(R.id.google_places_list);
        
        if(lv != null)
        {
        	lv.setAdapter(adapter);
        }
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_google_places, menu);
        return true;
    }
   
}

//private class PlaceLoader extends AsyncTask<Void,Void,List<Place>>{
//	
//	private final WeakReference<ProgressBar> mProgressBar = new WeakReference<ProgressBar>((ProgressBar)findViewById(android.R.id.progress));
//	private final WeakReference<ListView> mListView = new WeakReference<ListView>((ListView)findViewById(android.R.id.list));
//	private final WeakReference<TextView> mTextView = new WeakReference<TextView>((TextView)findViewById(android.R.id.empty));
//	
//	protected List<Place> doInBackground(Void... params){
//		return 
//	}
//}