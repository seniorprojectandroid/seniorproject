package edu.fiu.cs.seniorproject.manager;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import edu.fiu.cs.seniorproject.client.RestClient;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.data.provider.DataProvider;
import edu.fiu.cs.seniorproject.data.provider.EventFullProvider;
import edu.fiu.cs.seniorproject.data.provider.GPProvider;
import edu.fiu.cs.seniorproject.data.provider.MBVCAProvider;
import edu.fiu.cs.seniorproject.utils.Logger;

public class DataManager {

	private LinkedList<DataProvider> mProviderList = new LinkedList<DataProvider>();
	private static DataManager mSingleton = null;
	
	private DataManager() {
		// register all the provider
		mProviderList.add(new MBVCAProvider());
		mProviderList.add(new GPProvider());
		mProviderList.add(new EventFullProvider());
	}
	
	public List<Event> getEventList(Location location, String category, String radius, String query) {
		List<Event> result = null;
		
		if ( mProviderList.size() > 0 ) {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				
				if ( provider.supportEvents() ) {
					
					List<Event> providerList = null;
					try {
						providerList = provider.getEventList(location, category, radius, query);
					} catch ( Exception e ) {
						providerList = null;
						Logger.Error("Exception getting event list " + e.getMessage());
					}
					
					if ( providerList != null && providerList.size() > 0 ) {
						if ( result == null ) {
							result = providerList;
						} else {
							result.addAll(providerList);
						}
					}
				}
			}
		}
		
		return result;
	}
	
	public List<Place> getPlaceList(Location location, String category, String radius, String query) {
		List<Place> result = null;
		
		if ( mProviderList.size() > 0 ) {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				
				if ( provider.supportPlaces() ) {
					List<Place> providerList = null;
					try {
						providerList = provider.getPlaceList(location, category, radius, query);
					} catch (Exception e) {
						providerList = null;
						Logger.Error("Exception getting place list " + e.getMessage() );
					}
					
					if ( providerList != null && providerList.size() > 0 )					{
						if ( result == null ) {
							result = providerList;
						} else {
							result.addAll(providerList);
						}
					}
				}
			}
		}
		return result;
	}
	
	public Event getEventDetails( String eventId, SourceType source ) {
		Event result = null;
		
		try {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				if ( provider.supportEvents() && provider.getSource() == source ) {
					result = provider.getEventDetails(eventId,null);
					break;
				}
			}
		} catch (Exception e) {
			Logger.Error("Exception getting event details " + e.getMessage() );
		}
		return result;
	}
	
	public Place getPlaceDetails( String placeId, String reference, SourceType source ) {
		Place result = null;
		
		try {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				if ( provider.supportPlaces() && provider.getSource() == source ) {
					result = provider.getPlaceDetails(placeId,reference);
					break;
				}
			}
		} catch (Exception e ) {
			Logger.Error("Exception getting place details " + e.getMessage());
		}
		return result;
	}
	
	public void downloadBitmap( String url, ImageView target ) {
		try {
			(new BitmapDownloader(target)).execute(url);
		} catch (Exception e) {
			Logger.Error("exception downloading bitmap " + e.getMessage());
		}
	}
	
	public static DataManager getSingleton() {
		if ( mSingleton == null ) {
			mSingleton = new DataManager();
		}
		return mSingleton;
	}
	
	private class BitmapDownloader extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> mImageView;
		
		public BitmapDownloader(ImageView target) {
			mImageView = new WeakReference<ImageView>(target);
		}
		@Override
		protected Bitmap doInBackground(String... params) {
			return RestClient.downloadBitmap(params[0]);
		}
		
		@Override
		protected void onPostExecute(Bitmap bm) {
			if ( bm != null && mImageView != null && mImageView.get() != null ) {
				mImageView.get().setImageBitmap(bm);
			}
		}
		
	}
}
