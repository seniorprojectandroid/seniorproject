package edu.fiu.cs.seniorproject;

import edu.fiu.cs.seniorproject.utils.Logger;
import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class MBVCABackUp extends BackupAgentHelper 
{
	
	// The names of the SharedPreferences groups that the application maintains.  These
    // are the same strings that are passed to getSharedPreferences(String, int).
	public static final String KEY_DISTANCE_RADIUS 			= "pref_distanceradius";
	public static final String KEY_DEFAULT_EVENT_CATEGORY 	= "pref_eventscategories";
	public static final String KEY_DEFAULT_PLACE_CATEGORY 	= "pref_placescategories";
	public static final String KEY_MIAMI_BEACH				= "pref_MBVCA";
	public static final String KEY_GOOGLE_PLACE				= "pref_GOOGLE_PLACE";
	public static final String KEY_EVENTFUL					= "pref_EVENTFUL";

    // A key to uniquely identify the set of backup data
    static final String PREF_BACKUP_KEY = "sharedPref";

    // Allocate a helper and add it to the backup agent
    public void onCreate() {

        Logger.Error("It executed backup before.");
        
        SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this, KEY_DISTANCE_RADIUS, 
        																			   KEY_DEFAULT_EVENT_CATEGORY,
        																			   KEY_DEFAULT_PLACE_CATEGORY,
        																			   KEY_MIAMI_BEACH,
        																			   KEY_GOOGLE_PLACE,
        																			   KEY_EVENTFUL);
        
        addHelper(PREF_BACKUP_KEY, helper);
        
        Logger.Error("It executed backup after.");
    }

}
