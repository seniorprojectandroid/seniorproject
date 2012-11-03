package edu.fiu.cs.seniorproject;

import edu.fiu.cs.seniorproject.data.EventCategoryFilter;
import edu.fiu.cs.seniorproject.data.PlaceCategoryFilter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;


public class SettingsActivity extends PreferenceActivity  {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();        

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

    public static int getDefaultSearchRadius(Context context) {
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
    	return pref != null ? Integer.valueOf( pref.getString(SettingsFragment.KEY_DISTANCE_RADIUS, "1")) : 0;
    }
    
    public static String getDefaultPlaceCategory(Context context) {
    	String defaultCategory = PlaceCategoryFilter.RESTAURANT_BARS.toString();
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
    	return pref != null ? pref.getString(SettingsFragment.KEY_DEFAULT_PLACE_CATEGORY, defaultCategory) : defaultCategory;
    }
    
    public static String getDefaultEventsCategory(Context context) {
    	String defaultCategory = EventCategoryFilter.Music.toString();
    	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
    	return pref != null ? pref.getString(SettingsFragment.KEY_DEFAULT_EVENT_CATEGORY, defaultCategory) : defaultCategory;
    }    
}

