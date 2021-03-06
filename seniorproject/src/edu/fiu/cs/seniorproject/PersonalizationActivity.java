package edu.fiu.cs.seniorproject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import edu.fiu.cs.seniorproject.data.MbGuideDB;


public class PersonalizationActivity extends Activity implements OnItemSelectedListener
{
	private Spinner spinner1, spinner2, spinner3, spinner4;
	private Button btnSubmit, btnSkip;	
	private String eCategory, pCategory, radius; //, time;
	private MbGuideDB db = new MbGuideDB(this);

	private OnClickListener skipListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int eventPos = spinner1.getSelectedItemPosition();
			//int timePos = spinner4.getSelectedItemPosition();
			int placePos = spinner2.getSelectedItemPosition();
			int radiusPos = spinner3.getSelectedItemPosition();
			
			setEventsPref(eventPos);
		//	setTimePref(timePos);
			setPlacesPref(placePos);
			setRadiusPref(radiusPos);
			setFlag();
			Intent skipIntent = new Intent(PersonalizationActivity.this, MainActivity.class);
			startActivity(skipIntent);
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personalization);

		addItemsOnSpinner1();
		addItemsOnSpinner2();
		addItemsOnSpinner3();
		addItemsOnSpinner4();
		addListenerOnButton();	
		
		btnSkip = (Button) findViewById(R.id.btnSkip);	
		btnSkip.setOnClickListener(skipListener);
	}

	public void addItemsOnSpinner1() {

		spinner1 = (Spinner) findViewById(R.id.events_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.eventscategories, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner1.setAdapter(adapter);
		spinner1.setOnItemSelectedListener(this);		
	}	

	public void addItemsOnSpinner2() {
		
		spinner2 = (Spinner) findViewById(R.id.places_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.placescategories, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner2.setAdapter(adapter);
		spinner2.setOnItemSelectedListener(this);
	}
	
	public void addItemsOnSpinner3() {
		spinner3 = (Spinner) findViewById(R.id.distanceradious_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.distanceradius, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner3.setAdapter(adapter);	
		spinner3.setOnItemSelectedListener(this);
		
	}
	
	public void addItemsOnSpinner4() {

		spinner4 = (Spinner) findViewById(R.id.time_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.time_filters, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner4.setAdapter(adapter);
		spinner4.setOnItemSelectedListener(this);		
	}
	
	private void setEventsPref(int pos)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = pref.edit();		
		Resources res = getResources(); 
		eCategory =  res.getStringArray(R.array.EventsValues)[pos];
		editor.putString(SettingsFragment.KEY_DEFAULT_EVENT_CATEGORY,eCategory );
		editor.commit();

	}
	
	private void setPlacesPref(int pos)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor eventsPrefEditor = pref.edit();		
		Resources res = getResources(); 
		pCategory = res.getStringArray(R.array.PlacesValues)[pos];
		eventsPrefEditor.putString(SettingsFragment.KEY_DEFAULT_PLACE_CATEGORY, pCategory );
		eventsPrefEditor.commit();		
	}
	
	private void setRadiusPref(int pos)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = pref.edit();		
		Resources res = getResources();  
		radius = res.getStringArray(R.array.radiusvalues)[pos];
		editor.putString(SettingsFragment.KEY_DISTANCE_RADIUS, radius );
		editor.commit();
	}
	
//	private void setTimePref(int pos)
//	{
//		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//		Editor editor = pref.edit();		
//		Resources res = getResources();  
//		time = res.getStringArray(R.array.time_filters)[pos];
//		editor.putString(SettingsFragment.TIME, time );
//		editor.commit();
//	}

	 public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    
	 }

	 public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	 }
	
	public void addListenerOnButton() {
		
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		
		
		
//		btnSkip.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				
//				Intent intent = new Intent(PersonalizationActivity.this, MainActivity.class);
//				PersonalizationActivity.this.startActivity(intent);	
//			}
//			
//		});

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {		

					Intent intent = new Intent(PersonalizationActivity.this,MainActivity.class);		
					int eventPos = spinner1.getSelectedItemPosition();
					int placePos = spinner2.getSelectedItemPosition();
					int radiusPos = spinner3.getSelectedItemPosition();
					setEventsPref(eventPos);
					setPlacesPref(placePos);
					setRadiusPref(radiusPos);
					
					setFlag();
					PersonalizationActivity.this.startActivity(intent);
			}

		});

	}
	
	private void setFlag()
	{
		try{						
			db.openDatabase();				
			int res = db.setUserPrefInactiveFlag();						
			Log.i("User_Prerences_Table", "# of rows affected = " + res);						
							db.createUserPrefRecord(eCategory, pCategory, radius);						
			db.closeDatabase();
			
		}catch(SQLException e){						
			e.printStackTrace();
		}					
		
	}



}