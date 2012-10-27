package edu.fiu.cs.seniorproject;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class PersonalizationActivity extends Activity 
{
	private Spinner spinner1, spinner2;
	private Button btnSubmit, btnSkip;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personalization);

		addItemsOnSpinner1();
		addItemsOnSpinner2();
		addListenerOnButton();
	}

	public void addItemsOnSpinner1() {

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		List<String> list1 = new ArrayList<String>();
		list1.add("Select Your Interest");
		list1.add("Restaurants");
		list1.add("Bars");
		list1.add("Beaches");
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list1);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(dataAdapter1);
	}
	
	//add items into spinner dynamically
	public void addItemsOnSpinner2() {

		spinner2 = (Spinner) findViewById(R.id.spinner2);
		List<String> list = new ArrayList<String>();
		list.add("Select Radius (Miles)");
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		list.add("10");
		list.add("20");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(dataAdapter);
	}
	
	//get the selected dropdown list value
	public void addListenerOnButton() {

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);
		
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

}