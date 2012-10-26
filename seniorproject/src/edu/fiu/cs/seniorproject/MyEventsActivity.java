package edu.fiu.cs.seniorproject;

import java.util.ArrayList;



import android.app.ListActivity;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import edu.fiu.cs.seniorproject.data.MbGuideDB;

public class MyEventsActivity extends ListActivity {
	
	MbGuideDB mb = new MbGuideDB(this); 
	ArrayList<String> eList = null;
	ArrayList<String> eList2 = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_my_events);
        getEventNames();
        eList2 = new ArrayList<String>();
        eList2.add("Joe");
        eList2.add("Joe2");
        if(eList != null)
        {
//	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
//	                android.R.layout.simple_list_item_1, eList2  );
	        setListAdapter(new ArrayAdapter<String>(this ,
					android.R.layout.simple_dropdown_item_1line, eList));
//	        ListView lv = (ListView)findViewById(R.layout.activity_my_events);
//	        lv.setAdapter(adapter);
        }
        
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_my_events, menu);
//        return true;
//    }
//    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_events, menu);
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
    
    public void onAddEventToCalendarClick2(MenuItem view) {
    	//addEventToCalendarAndDB();
    }
    
    public void onDeleteEventFromCalendarAndDBClick2(MenuItem view) { 	  	
    	
    	//deleteEventToCalendarAndDB();
    }
    
    
    
    private void getEventNames()
    {
    	 
    	try
    	{
    		mb.openDatabase();
    		eList = mb.listEventNames(); 
    	    mb.closeDatabase();
    	}catch(SQLException e)
    	 {
    		e.printStackTrace();
    	 }
    	
    	
    }
    
    
    
}
