package edu.fiu.cs.seniorproject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class PersonalizationActivity extends Activity implements OnItemSelectedListener
{
	private Spinner spinner1, spinner2, spinner3;
	private Button btnSubmit, btnSkip;
	
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
	
	//add items into spinner dynamically
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
	
	 public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        // An item was selected. You can retrieve the selected item using
	        String s = parent.getItemAtPosition(pos).toString();
	        Toast.makeText(PersonalizationActivity.this, s, Toast.LENGTH_LONG).show();
	 }

	 public void onNothingSelected(AdapterView<?> parent) {
	        // Another interface callback
	 }
	
	//get the selected dropdown list value
	public void addListenerOnButton() {

//		spinner1 = (Spinner) findViewById(R.id.spinner1);
//		spinner2 = (Spinner) findViewById(R.id.spinner2);
		
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
				
				if(!(String.valueOf(spinner1.getSelectedItem()).equalsIgnoreCase("Select Your Interest")) && !(String.valueOf(spinner2.getSelectedItem()).equalsIgnoreCase("Select Radius (Miles)")) )
				{
					Intent intent = new Intent(PersonalizationActivity.this,MainActivity.class);
					//String spinnerOne = String.valueOf(spinner1.getSelectedItem());
					//int spinnerTwo = Integer.valueOf(String.valueOf(spinner2.getSelectedItem()));
					PersonalizationActivity.this.startActivity(intent);
				}
				else
				{
					Toast.makeText(PersonalizationActivity.this, "Please Select an Interest, Radius or press skip.", Toast.LENGTH_SHORT).show();
				}
			}

		});

	}

//	@Override
//	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
//			long arg3) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public void onNothingSelected(AdapterView<?> arg0) {
//		// TODO Auto-generated method stub
//		
//	}

}