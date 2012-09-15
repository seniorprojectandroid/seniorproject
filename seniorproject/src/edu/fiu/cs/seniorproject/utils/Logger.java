package edu.fiu.cs.seniorproject.utils;

import android.util.Log;

public class Logger {

	public static boolean ENABLED = true;
	public static String DEFAULT_TAG = "SeniorProject";
	
	public static void Debug(String msg) {
		Debug(DEFAULT_TAG, msg);
	}

	public static void Debug(String tag, String msg ) {
		if ( ENABLED ) {
			Log.d(tag, msg);
		}
	}
	
	public static void Info(String msg) {
		Info(DEFAULT_TAG, msg);
	}
	
	public static void Info(String tag, String msg) {
		if ( ENABLED ) {
			Log.i(tag, msg);
		}
	}
	
	public static void Warning(String msg) {
		Warning(DEFAULT_TAG, msg);
	}
	
	public static void Warning(String tag, String msg) {
		if ( ENABLED ) {
			Log.w(tag, msg);
		}
	}
	
	public static void Error( String msg ) {
		Error(DEFAULT_TAG, msg);
	}
	
	public static void Error( String tag, String msg ) {
		if ( ENABLED ) {
			Log.e(tag, msg);
		}
	}
}
