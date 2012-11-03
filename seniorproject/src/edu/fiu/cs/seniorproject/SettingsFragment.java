package edu.fiu.cs.seniorproject;

//import android.app.Fragment;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener  {
	
	public static final String KEY_DISTANCE_RADIUS 			= "pref_distanceradius";
	public static final String KEY_DEFAULT_EVENT_CATEGORY 	= "pref_eventscategories";
	public static final String KEY_DEFAULT_PLACE_CATEGORY 	= "pref_placescategories";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }

	@Override
	public void onResume() {
	    super.onResume();
	    Map<String, ?> prefMap  = getPreferenceScreen().getSharedPreferences().getAll();
	    for (String key : prefMap.keySet() ) {
	    	Preference pref = findPreference(key);
			if ( pref != null ) {
				pref.setSummary( prefMap.get(key).toString() );
			}
		}
	    getPreferenceScreen().getSharedPreferences()
	            .registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences()
	            .unregisterOnSharedPreferenceChangeListener(this);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		Preference pref = findPreference(key);
		if ( pref != null ) {
			pref.setSummary( sharedPreferences.getString(key, ""));
		}
	}
}
