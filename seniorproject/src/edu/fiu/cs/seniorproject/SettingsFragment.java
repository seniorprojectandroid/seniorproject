package edu.fiu.cs.seniorproject;

//import android.app.Fragment;
import java.util.Map;

import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.manager.DataManager;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener  {
	
	public static final String KEY_DISTANCE_RADIUS 			= "pref_distanceradius";
	public static final String KEY_DEFAULT_EVENT_CATEGORY 	= "pref_eventscategories";
	public static final String KEY_DEFAULT_PLACE_CATEGORY 	= "pref_placescategories";
	public static final String KEY_MIAMI_BEACH				= "pref_MBVCA";
	public static final String KEY_GOOGLE_PLACE				= "pref_GOOGLE_PLACE";
	public static final String KEY_EVENTFUL					= "pref_EVENTFUL";
	
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
				String summary = prefMap.get(key).toString();
				if ( summary.equals("true")) {
					summary = "Enabled";
				} else if ( summary.equals("false")) {
					summary = "Disabled";
				}
				pref.setSummary( summary );
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
		if ( key.equals(KEY_DEFAULT_EVENT_CATEGORY) || key.equals(KEY_DEFAULT_PLACE_CATEGORY) || key.equals(KEY_DISTANCE_RADIUS)) {
			Preference pref = findPreference(key);
			if ( pref != null ) {
				pref.setSummary( sharedPreferences.getString(key, ""));
			}
		} else if ( key.equals(KEY_MIAMI_BEACH) || key.equals(KEY_GOOGLE_PLACE) || key.equals(KEY_EVENTFUL)) {
			String sourceStr = key.substring(5);
			SourceType source = SourceType.valueOf(sourceStr);
			if ( source != null ) {
				DataManager.getSingleton().enableProvider(source, sharedPreferences.getBoolean(key, true));
			}
		}
	}
}
