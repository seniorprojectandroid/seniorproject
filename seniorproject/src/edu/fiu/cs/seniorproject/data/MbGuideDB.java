package edu.fiu.cs.seniorproject.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MbGuideDB {

	// Database Name and Version
	private static final String DATABASE_NAME = "MB_Guide_DB";
	private static final int DATABASE_VERSION = 6;

	// Database Tables
	private static final String EVENT_TABLE_NAME = "Event";
	// private static final String TEST_TABLE_NAME = "Test";
	private static final String PLACE_TABLE_NAME = "Place";
	// private static final String CATEGORY_TABLE_NAME = "Categoty";
	// private static final String LOCATION_TABLE_NAME = "Location";
	 private static final String USER_PREFERENCES_TABLE_NAME = "User_Preferences";

	// Event table
	public static final String EVENT_ID = "event_id";
	public static final String EVENT_NAME = "event_name";
	public static final String EVENT_CALENDAR_ID = "event_calendar_id";
	public static final String EVENT_LOCATION = "event_location";
	public static final String EVENT_CATEGORY_ID = "event_category_id";
	public static final String EVENT_CATEGORY_DESCRIPTION = "event_category_description";
	public static final String EVENT_TIME_STARTS = "event_time_start";
	public static final String EVENT_TIME_ENDS = "event_time_end";
	public static final String EVENT_IS_DELETED_FLAG = "event_is_deleted_flag";
	public static final String EVENT_IS_EXPIRED_FLAG = "event_is_expired_flag";
	public static final String EVENT_PLACE_ID = "event_place_id";

	// Place table
	public static final String PLACE_ID = "place_id";
	public static final String PLACE_NAME = "place_name";
	public static final String PLACE_ADDRESS = "place_address";
	public static final String PLACE_LATITUDE = "place_latitude";
	public static final String PLACE_LONGITUDE = "place_longitude";
	
	public static final String PLACE_DESCRIPTION = "place_description";
	public static final String PLACE_WEBSITE = "place_website";
	public static final String PLACE_SOURCE_TYPE = "place_source_type";
	
	public static final String PLACE_CATEGORY_ID = "place_category_id";
	
	public static final String PLACE_CATEGORY_DESCRIPTION = "place_category_description";
	public static final String PLACE_TIME_OPENS = "place_time_opens";
	public static final String PLACE_TIME_CLOSES = "event_time_closes";
	public static final String PLACE_IS_DELETED_FLAG = "place_is_deleted_flag";
	public static final String PLACE_EVENT_ID = "place_event_id";
	public static final String PLACE_HAS_EVENT_FLAG = "event_place_id";

	// Category table
	public static final String CATEGORY_ID = "category_id";
	public static final String CATEGORY_DESCRIPTION = "category_description";
	public static final String CATEGORY_IS_EVENT_FLAG = "category_is_event_flag";
	public static final String CATEGORY_IS_PLACE_FLAG = "category_is_place_flag";

	// Location table
	public static final String LOCATION_ID = "location_id";
	public static final String LOCATION_NAME = "location_name";
	public static final String LOCATION_LATITUDE = "location_latitude";
	public static final String LOCATION_LONGITUDE = "location_longitude";
	
	// User_Preferences table
	public static final String PREF_ID = "pref_id";
	public static final String PREF_EVENT_CATEGORY = "pref_event_category";
	public static final String PREF_PLACE_CATEGORY = "pref_place_category";
	public static final String PREF_RADIUS_CATEGORY = "pref_radius_category";
	public static final String PREF_IS_ACTIVE_FLAG = "pref_is_active_flag";

	public static final int SET_DELETED_FLAG = 1;
	public static final int NOT_SET_DELETED_FLAG = 0;

	private MbGuideHelper mbHelper;
	private final Context mbContext;
	private SQLiteDatabase mbDatabase;
	

	public MbGuideDB(Context context) {
		this.mbContext = context;
	}

	public MbGuideDB openDatabase() throws SQLException {
		mbHelper = new MbGuideHelper(mbContext);
		mbDatabase = mbHelper.getWritableDatabase();
		return this;
	}

	public void closeDatabase() throws SQLException {
		mbDatabase.close();
		mbHelper.close();
	}

//	public void createEventRecord(String eventName, long eventCalendarID,
//			String eventLocation) throws SQLException {
//		ContentValues contentValues = new ContentValues();
//		contentValues.put(EVENT_NAME, eventName);
//		contentValues.put(EVENT_CALENDAR_ID, eventCalendarID);
//		contentValues.put(EVENT_LOCATION, eventLocation);
//		mbDatabase.insert(EVENT_TABLE_NAME, null, contentValues);
//	}
	
	public void createUserPrefRecord(String eCategory, String pCategory, String radius ) throws SQLException {
		ContentValues contentValues = new ContentValues();
		
		if(eCategory != null)
			contentValues.put(PREF_EVENT_CATEGORY, eCategory);
		if(pCategory != null)
			contentValues.put(PREF_PLACE_CATEGORY, pCategory);
		if(radius != null)
			contentValues.put(PREF_RADIUS_CATEGORY, radius);
		mbDatabase.insert(USER_PREFERENCES_TABLE_NAME, null, contentValues);
	}
	
	
	private String cleanString(String s)
	{
		String cs="";
		int len = s.length();
		for(int i=0; i<len; i++)
		{
			char c = s.charAt(i);
			if(c== '\'')
				cs += "";
			else		
				cs +=c;		
		}
		
		Log.i("event name",String.format("Event NAME: ", s));	
		Log.i("cleanned name",String.format(" cleanned Event NAME: ", cs));	
		
		return cs;
	}

   	public void createEventRecordVersion2(String eventName,
			long eventCalendarID, String eventLocation, long eTimeStarts,
			long eTimeEnds) throws SQLException {
		ContentValues contentValues = new ContentValues();
		
		String cName = cleanString(eventName);
		contentValues.put(EVENT_NAME, cName);
		contentValues.put(EVENT_CALENDAR_ID, eventCalendarID);
		contentValues.put(EVENT_LOCATION, eventLocation);
		contentValues.put(EVENT_TIME_STARTS, eTimeStarts);
		contentValues.put(EVENT_TIME_ENDS, eTimeEnds);

		mbDatabase.insert(EVENT_TABLE_NAME, null, contentValues);
	}
   	
   	public void createPlaceRecord(String placeId, String placeName, String placeAddress, String placeLatitude, String placeLongitude, 
			String placeDescrition, String  placeWebsite, String placeSourceType ) throws SQLException {
		
		ContentValues contentValues = new ContentValues();
		
		if(placeId != null)
			contentValues.put(PLACE_ID, placeId);
		
		if(placeName != null)
			contentValues.put(PLACE_NAME, placeName);
		
		if(placeAddress != null)
			contentValues.put(PLACE_ADDRESS, placeAddress);
		
		if(placeLatitude != null)
		contentValues.put(PLACE_LATITUDE, placeLatitude);
		
		if(placeLongitude != null)
			contentValues.put(PLACE_LONGITUDE, placeLongitude);
		
		if(placeDescrition != null)		
			contentValues.put(PLACE_DESCRIPTION, placeDescrition);
		
		if(placeWebsite != null)	
			contentValues.put(PLACE_WEBSITE, placeWebsite);
		
		if( placeSourceType != null)
			contentValues.put(PLACE_SOURCE_TYPE, placeSourceType);
		
		mbDatabase.insert(PLACE_TABLE_NAME, null, contentValues);
	}
//
//	public boolean existsEvent(String s) throws SQLException {
//		String[] columns = new String[] { EVENT_NAME, EVENT_CALENDAR_ID,
//				EVENT_LOCATION }; // KEY_ROW_ID,
//		Cursor c = mbDatabase.query(EVENT_TABLE_NAME, columns, EVENT_NAME
//				+ " = " + "'s'", null, null, null, null);
//		if (c != null) {
//			c.close();
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public boolean existsVersion2(String s) throws SQLException {
//		String[] columns = new String[] { EVENT_NAME, EVENT_CALENDAR_ID,
//				EVENT_LOCATION }; // KEY_ROW_ID,
//		Cursor c = mbDatabase.query(EVENT_TABLE_NAME, columns, EVENT_NAME
//				+ " = " + "'s'", null, null, null, null);
//		String sTrimmed = s.trim();
//		int iName = c.getColumnIndex(EVENT_NAME);
//
//		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//			if (sTrimmed.equalsIgnoreCase(c.getString(iName).trim())) {
//				c.close();
//				return true;
//			}
//		}
//
//		c.close();
//		return false;
//	}

//	public boolean existsVersion3_ussingFlag(String name) throws SQLException {
//		Cursor cursor = mbDatabase.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME
//				+ " WHERE " + EVENT_NAME + " = '" + name + "' " + "AND "
//				+ EVENT_IS_DELETED_FLAG + " = " + NOT_SET_DELETED_FLAG, null);
//
//		if (cursor != null) {
//			if (cursor.getCount() > 0) {
//				cursor.close();
//				return true;
//			}
//		}
//		cursor.close();
//		return false;
//	}

	public boolean existsVersion3(String name) throws SQLException {
		String cName = cleanString(name);
		Cursor cursor = mbDatabase.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME
				+ " WHERE " + EVENT_NAME + " = '" + cName + "' ", null);

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
		}
		cursor.close();
		return false;
	}

	public int deleteEvent_settingFlag(long calEventID) throws SQLException {
		String updateFilter = EVENT_CALENDAR_ID + " = " + calEventID;
		ContentValues args = new ContentValues();
		args.put(EVENT_IS_DELETED_FLAG, SET_DELETED_FLAG);
		int affectedRows = mbDatabase.update(EVENT_TABLE_NAME, args,
				updateFilter, null);
		return affectedRows;
	}

	// this version will delete the event physically from the dataase
	public int deleteEvent(long calEventID) throws SQLException {
		
		return mbDatabase.delete(EVENT_TABLE_NAME, EVENT_CALENDAR_ID + " = "
				+ calEventID, null);
	}

	public int deleteAllEvents() {
		return mbDatabase.delete(PLACE_TABLE_NAME, null, null);
	}

	// public int deleteAllEvents()
	// {
	// ContentValues args = new ContentValues();
	// args.put(EVENT_IS_DELETED_FLAG, SET_DELETED_FLAG);
	// int affectedRows = mbDatabase.update(EVENT_TABLE_NAME, args, null, null);
	// return affectedRows;
	// }
	//

	public long getEventCalendarID(String name) throws SQLException {
		String eCalName = cleanString(name);
		long eCalID = -1;
		Cursor c = mbDatabase.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME
				+ " WHERE " + EVENT_NAME + " = '" + eCalName + "'", null);

		if (c != null) {
			int calIdIndex = c.getColumnIndex(EVENT_CALENDAR_ID);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				eCalID = c.getLong(calIdIndex);
			}

		}
		c.close();
		return eCalID;
	}

	public ArrayList<String> listEventNames() throws SQLException {
		String[] columns = new String[] { EVENT_NAME, EVENT_CALENDAR_ID,
				EVENT_LOCATION }; // KEY_ROW_ID,
		Cursor c = mbDatabase.query(EVENT_TABLE_NAME, columns, null, null,
				null, null, null);
		ArrayList<String> events = new ArrayList<String>();
		int iName = c.getColumnIndex(EVENT_NAME);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			events.add(c.getString(iName));
		}
		return events;
	}

	
	
	public boolean existsPlace(String placeId) throws SQLException {
		Cursor cursor = mbDatabase.rawQuery("SELECT * FROM " + PLACE_TABLE_NAME
				+ " WHERE " + PLACE_ID + " = '" + placeId + "' ", null);

		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
				return true;
			}
		}
	
		return false;
	}
	
	public ArrayList<String> listPlaceNames() throws SQLException {
		String[] columns = new String[] {PLACE_ID, PLACE_NAME, PLACE_ADDRESS, PLACE_LATITUDE, PLACE_LONGITUDE,
											PLACE_DESCRIPTION, PLACE_WEBSITE, PLACE_SOURCE_TYPE};
		Cursor c = mbDatabase.query(PLACE_TABLE_NAME, columns, null, null,
				null, null, null);
		ArrayList<String> places = new ArrayList<String>();
		
		int iId = c.getColumnIndex(PLACE_ID);
		int iName = c.getColumnIndex(PLACE_NAME);
		int iAdd = c.getColumnIndex(PLACE_ADDRESS);
		int iLat = c.getColumnIndex(PLACE_LATITUDE);
		int iLon = c.getColumnIndex(PLACE_LONGITUDE);
		int iDesc = c.getColumnIndex(PLACE_DESCRIPTION);
		int iWebs = c.getColumnIndex(PLACE_WEBSITE);
		int iSourc = c.getColumnIndex(PLACE_SOURCE_TYPE);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			places.add(c.getString(iId)+ "..."+c.getString(iName)+ "..." +c.getString(iAdd)+ "..." +c.getString(iLat)+ "..." +c.getString(iLon)
					+ "..." +c.getString(iDesc)+ "..." +c.getString(iWebs)+ "..." +c.getString(iSourc));
		}
		return places;
	}

	// this version will delete the all places physically from the dataase
	public int deleteAllPlaces() throws SQLException
	{
		return mbDatabase.delete(PLACE_TABLE_NAME, null, null);
	}

	// this version will delete the place physically from the dataase
	public int deletePlace(String placeiD) throws SQLException 
	{
		return mbDatabase.delete(PLACE_TABLE_NAME, PLACE_ID + " = "
				+ "'" +placeiD+ "'", null);
	}

	/**
	 * Private class to create and work database
	 */
	private static class MbGuideHelper extends SQLiteOpenHelper {

		private static final String createEventTableQuery = "CREATE TABLE "
				+ EVENT_TABLE_NAME + " ( " +

				EVENT_NAME + " TEXT PRIMARY KEY, " + EVENT_CALENDAR_ID
				+ " INTEGER NOT NULL, " + EVENT_LOCATION + " TEXT NOT NULL, "
				+ EVENT_CATEGORY_ID + " INTEGER, " + EVENT_CATEGORY_DESCRIPTION
				+ " TEXT, " + EVENT_TIME_STARTS + " INTEGER NOT NULL, "
				+ EVENT_TIME_ENDS + " INTEGER NOT NULL, "				
				+ EVENT_IS_EXPIRED_FLAG + " INTEGER DEFAULT (0), "
				+ EVENT_PLACE_ID + " TEXT );";

		private static final String createPlaceTableQuery = "CREATE TABLE "
				+ PLACE_TABLE_NAME + " ( " +
					PLACE_ID + " TEXT PRIMARY KEY, "+
					PLACE_NAME + " TEXT, " + PLACE_ADDRESS + " TEXT "+
					PLACE_ADDRESS + " TEXT, " +
					PLACE_LATITUDE + " INTEGER, " +
					PLACE_LONGITUDE + " INTEGER, " + 
					PLACE_DESCRIPTION + " TEXT, " +
					PLACE_WEBSITE +  " TEXT, " +
					PLACE_SOURCE_TYPE + " TEXT, " +
					PLACE_CATEGORY_ID + " INTEGER, " + PLACE_CATEGORY_DESCRIPTION + " TEXT, " + 
					PLACE_TIME_OPENS + " INTEGER, " + 
					PLACE_TIME_CLOSES + " INTEGER, " +
					PLACE_HAS_EVENT_FLAG +" INTEGER DEFAULT (0), " +
					PLACE_EVENT_ID + " TEXT );";
		
		private static final String createUserPrefTableQuery = "CREATE TABLE "
				+ USER_PREFERENCES_TABLE_NAME + " ( " +
				PREF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				PREF_EVENT_CATEGORY + " TEXT, " + 
				PREF_PLACE_CATEGORY + " TEXT, " +
				PREF_RADIUS_CATEGORY + " TEXT, "+
				PREF_IS_ACTIVE_FLAG +" INTEGER DEFAULT (0) );";					


		public MbGuideHelper(Context context) {

			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(createEventTableQuery);
			db.execSQL(createPlaceTableQuery);
			db.execSQL(createUserPrefTableQuery);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + PLACE_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + USER_PREFERENCES_TABLE_NAME );
			onCreate(db);
		}
	}

	public boolean isUserPrefSet() throws SQLException{
		
			//Cursor cursor = mbDatabase.rawQuery("SELECT * FROM " + USER_PREFERENCES_TABLE_NAME, null);
			Cursor cursor = mbDatabase.rawQuery("SELECT "+ PREF_ID +" FROM " + USER_PREFERENCES_TABLE_NAME, null);
			if (cursor != null) {
				if (cursor.getCount() > 0) {
					cursor.close();
					return true;
				}
			}
		
		return false;
	}

	public int setUserPrefInactiveFlag() throws SQLException{
		
		String updateFilter = PREF_IS_ACTIVE_FLAG + " = " + NOT_SET_DELETED_FLAG ;
		ContentValues args = new ContentValues();
		args.put(PREF_IS_ACTIVE_FLAG, SET_DELETED_FLAG);
		int affectedRows = mbDatabase.update(USER_PREFERENCES_TABLE_NAME, args,updateFilter, null);
		return affectedRows;
	}
	
	public int getEventCategoryCount()
	{
		
		int count =0;
		return count;
	}
	public int getPlaceCategoryCount()
	{
		int count =0;
		return count;
	}
	public int getRadiusCount()
	{
		int count =0;
		return count;
	}
	
	public String selectPrefEventCategory()
	{
		String eCategory = "";
		return eCategory;
	}
	public String selectPrefPlaceCategory()
	{
		String pCategory = "";
		return pCategory;
	}
	public String selectPrefRadius()
	{
		String radius = "";
		return radius;
	}
	
}
