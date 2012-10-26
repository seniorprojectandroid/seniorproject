package edu.fiu.cs.seniorproject.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MbGuideDB 
{
	
	// Database Name and Version
	private static final String DATABASE_NAME = "MB_Guide_DB";
	private static final int DATABASE_VERSION = 5;
	
	
	// Database Tables
	private static final String EVENT_TABLE_NAME = "Event";
	private static final String TEST_TABLE_NAME = "Test";
	private static final String PLACE_TABLE_NAME = "Place";
	private static final String CATEGORY_TABLE_NAME = "Categoty";
	private static final String LOCATION_TABLE_NAME = "Location";
	private static final String USER_PREFFERENCES_TABLE_NAME = "User_Prefferences";
	
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
	public static final String PLACE_CATEGORY_ID = "place_category_id";
	//public static final String PLACE_LOCATION = "place_location";
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
	public static final String LOCATION_NAME = "location_name";
	public static final String LOCATION_LATITUDE = "location_latitude";
	public static final String LOCATION_LONGITUDE = "location_longitude";
	
	
	public static final int SET_DELETED_FLAG = 1;
	public static final int NOT_SET_DELETED_FLAG = 0;
	
	private MbGuideHelper mbHelper;
	private final Context mbContext;
	private SQLiteDatabase mbDatabase;
	
	
	public MbGuideDB(Context context)
	{
		this.mbContext = context;
	}
	
	public MbGuideDB openDatabase()  throws SQLException
	{
		mbHelper = new MbGuideHelper(mbContext);
		mbDatabase = mbHelper.getWritableDatabase();
		return this;
	}
	
	public void closeDatabase() throws SQLException
	{
		mbHelper.getWritableDatabase();
		
	}
	public void createEventRecord(String eventName, long eventCalendarID, String eventLocation) throws SQLException
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(EVENT_NAME, eventName);
		contentValues.put(EVENT_CALENDAR_ID, eventCalendarID);
		contentValues.put(EVENT_LOCATION, eventLocation);
		mbDatabase.insert(EVENT_TABLE_NAME, null,contentValues);
	}
	
	public void createEventRecordVersion2(String eventName, long eventCalendarID, String eventLocation,
												 long eTimeStarts, long eTimeEnds) throws SQLException
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(EVENT_NAME, eventName);
		contentValues.put(EVENT_CALENDAR_ID, eventCalendarID);
		contentValues.put(EVENT_LOCATION, eventLocation);
		contentValues.put(EVENT_TIME_STARTS, eTimeStarts);
		contentValues.put(EVENT_TIME_ENDS, eTimeEnds);
		
		mbDatabase.insert(EVENT_TABLE_NAME, null,contentValues);
	}
	
	public boolean exists (String s ) throws SQLException
	{		
		String[] columns = new String []{EVENT_NAME,EVENT_CALENDAR_ID,EVENT_LOCATION }; //KEY_ROW_ID, 
		Cursor c = 	mbDatabase.query(EVENT_TABLE_NAME, columns, EVENT_NAME + " = " + "'s'" ,  null,null,null,null);
		if(c != null)
		{
			c.close();
			return true;
			}
		else
		{			
			return false;
		}
	}
	
	public boolean existsVersion2 (String s ) throws SQLException
	{		
		String[] columns = new String []{EVENT_NAME,EVENT_CALENDAR_ID,EVENT_LOCATION }; //KEY_ROW_ID, 
		Cursor c = 	mbDatabase.query(EVENT_TABLE_NAME, columns, EVENT_NAME + " = " + "'s'" ,  null,null,null,null);
		String sTrimmed = s.trim();
		int iName = c.getColumnIndex(EVENT_NAME);
	    
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			if(sTrimmed.equalsIgnoreCase(c.getString(iName).trim())){
				c.close();
				return true;
				}
		}
		
		    c.close();
			return false;
	}
	
	public boolean existsVersion3_ussingFlag(String name) throws SQLException
	{
		   Cursor cursor = mbDatabase.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME + " WHERE " + EVENT_NAME + " = '" +name+ "' " +
		   		"AND "+ EVENT_IS_DELETED_FLAG + " = "+ NOT_SET_DELETED_FLAG, null);
		   
		   if(cursor!=null)
		   {			   
			   if(cursor.getCount()>0)
			   {
				   cursor.close();
				   return true;
			   }
		   }
		   cursor.close();
		   return false;
	}
	
	public boolean existsVersion3(String name) throws SQLException
	{
		   Cursor cursor = mbDatabase.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME + " WHERE " + EVENT_NAME + " = '" +name+ "' ", null);
		   
		   if(cursor!=null)
		   {			   
			   if(cursor.getCount()>0)
			   {
				   cursor.close();
				   return true;
			   }
		   }
		   cursor.close();
		   return false;
	}
	
	
	public int deleteEvent_settingFlag( long calEventID)throws SQLException
	{
		String 	updateFilter =  EVENT_CALENDAR_ID + " = " + calEventID;
		ContentValues args = new ContentValues();
		args.put(EVENT_IS_DELETED_FLAG, SET_DELETED_FLAG);
		int affectedRows = mbDatabase.update(EVENT_TABLE_NAME, args, updateFilter,null);	
		return affectedRows;		
	}
	
	
	// this version will delete the event physically from the dataase
	public int deleteEvent( long calEventID)throws SQLException
	{		
		return mbDatabase.delete(EVENT_TABLE_NAME, EVENT_CALENDAR_ID + " = " + calEventID  , null);		
	}
	
	public int deleteAllEvents()
	{		
		ContentValues args = new ContentValues();
		args.put(EVENT_IS_DELETED_FLAG, SET_DELETED_FLAG);
		int affectedRows = mbDatabase.update(EVENT_TABLE_NAME, args, null, null);	
		return affectedRows;		
	}
	
	public long getEventCalendarID( String eCalName)throws SQLException
	{
          long eCalID = -1;		
		  Cursor c = mbDatabase.rawQuery("SELECT * FROM " + EVENT_TABLE_NAME + " WHERE " + EVENT_NAME + " = '" +eCalName+ "'", null);
	      
		  
		   if(c!=null)
		   {
				int calIdIndex = c.getColumnIndex(EVENT_CALENDAR_ID);
			    
				for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
				{
					eCalID = c.getLong(calIdIndex);
				}
			 
		   }
		   c.close();
		   return eCalID;		
	}

	public ArrayList<String> listEventNames() throws SQLException
	{		
		String[] columns = new String []{EVENT_NAME,EVENT_CALENDAR_ID,EVENT_LOCATION }; //KEY_ROW_ID, 
		Cursor c = 	mbDatabase.query(EVENT_TABLE_NAME, columns, null,null,null,null,null);
		ArrayList<String> events = new ArrayList<String>();
		int iName = c.getColumnIndex(EVENT_NAME);
	    
		for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
		{
			events.add(c.getString(iName));
		}
			return events;
	}
	

	
	
	
	/**
	 * Private class to create and work database
	 */
	private static class MbGuideHelper extends SQLiteOpenHelper
	{	

		
		private static final String createEventTableQuery  = "CREATE TABLE " + EVENT_TABLE_NAME + " ( " +
								
									EVENT_NAME + " TEXT PRIMARY KEY, " +
									EVENT_CALENDAR_ID + " INTEGER NOT NULL, "+ 
									EVENT_LOCATION + " TEXT NOT NULL, "+ 
									EVENT_CATEGORY_ID + " INTEGER, "+ 
									EVENT_CATEGORY_DESCRIPTION + " TEXT, "+ 
									EVENT_TIME_STARTS + " INTEGER NOT NULL, "+ 
									EVENT_TIME_ENDS + " INTEGER NOT NULL, "+ 
									EVENT_IS_DELETED_FLAG + " INTEGER DEFAULT (0), "+ 
									EVENT_IS_EXPIRED_FLAG + " INTEGER DEFAULT (0), "+ 
									EVENT_PLACE_ID  + " INTEGER );";
		
		private static final String createPlaceTableQuery = "CREATE TABLE " + PLACE_TABLE_NAME + " ( " +
								
								PLACE_NAME + " TEXT PRIMARY KEY, " +
								PLACE_CATEGORY_ID + " INTEGER, "+ 	
								PLACE_CATEGORY_DESCRIPTION  + " TEXT, "+ 
								PLACE_TIME_OPENS + " INTEGER, "+ 
								PLACE_TIME_CLOSES + " INTEGER, "+ 
								PLACE_IS_DELETED_FLAG + " INTEGER DEFAULT (0), "+ 
								PLACE_HAS_EVENT_FLAG + " INTEGER DEFAULT (0), "+ 
								PLACE_EVENT_ID + " INTEGER, " +
								"FOREIGN KEY(PLACE_EVENT_ID) REFERENCES " + 
										 PLACE_TABLE_NAME + "(EVENT_PLACE_ID) );";								
								
		

				 
		public MbGuideHelper(Context context) 
		{
			
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}
		
		

		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			// Still Using VERSION 2
			//db.execSQL(createEventTableQuery1);
			//db.execSQL(createTestTableQuery);
			
			// Next Will Use VERSION 3
			 db.execSQL(createEventTableQuery);
			// db.execSQL(createPlaceTableQuery);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
		{
			db.execSQL("DROP TABLE IF EXISTS "+EVENT_TABLE_NAME );
			onCreate(db);
		
//			db.execSQL("DROP TABLE IF EXISTS "+TEST_TABLE_NAME );
//			onCreate(db);
			
			//db.execSQL("DROP TABLE IF EXISTS "+PLACE_TABLE_NAME );
			//onCreate(db);
			
	    }
	
	}}
