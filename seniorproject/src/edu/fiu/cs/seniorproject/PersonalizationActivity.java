package edu.fiu.cs.seniorproject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class PersonalizationActivity extends Activity implements OnItemSelectedListener
{
	private Spinner spinner1, spinner2, spinner3;
	private Button btnSubmit, btnSkip;	
	
	String[] eventsValues;
	String[] placesValues;
	String[] radiusValues;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personalization);

		addItemsOnSpinner1();
		addItemsOnSpinner2();
		addItemsOnSpinner3();
		addListenerOnButton();		
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
	
	private void setEventsPref(int pos)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = pref.edit();		
		Resources res = getResources();  
		editor.putString(SettingsFragment.KEY_DEFAULT_EVENT_CATEGORY, res.getStringArray(R.array.EventsValues)[pos] );
		editor.commit();

	}
	
	private void setPlacesPref(int pos)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor eventsPrefEditor = pref.edit();		
		Resources res = getResources(); 
		eventsPrefEditor.putString(SettingsFragment.KEY_DEFAULT_PLACE_CATEGORY,res.getStringArray(R.array.PlacesValues)[pos] );
		eventsPrefEditor.commit();		
	}
	
	private void setRadiusPref(int pos)
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Editor editor = pref.edit();		
		Resources res = getResources();  
		editor.putString(SettingsFragment.KEY_DISTANCE_RADIUS, res.getStringArray(R.array.radiusvalues)[pos]);
		editor.commit();
	}

	 public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	    
	 }

	 public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	 }
	
	public void addListenerOnButton() {
		
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSkip = (Button) findViewById(R.id.btnSkip);		
		btnSkip.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PersonalizationActivity.this, MainActivity.class);
				PersonalizationActivity.this.startActivity(intent);	
			}
			
		});

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
					PersonalizationActivity.this.startActivity(intent);
			}

		});

	}



}