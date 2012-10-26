package edu.fiu.cs.seniorproject;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MyPlacesActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_places, menu);
        return true;
    }
}
