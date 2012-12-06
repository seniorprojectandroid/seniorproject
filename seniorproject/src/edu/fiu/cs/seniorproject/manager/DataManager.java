package edu.fiu.cs.seniorproject.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import edu.fiu.cs.seniorproject.client.RestClient;
import edu.fiu.cs.seniorproject.data.DateFilter;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.EventCategoryFilter;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.PlaceCategoryFilter;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.data.provider.DataProvider;
import edu.fiu.cs.seniorproject.data.provider.EventFullProvider;
import edu.fiu.cs.seniorproject.data.provider.GPProvider;
import edu.fiu.cs.seniorproject.data.provider.MBVCAProvider;
import edu.fiu.cs.seniorproject.utils.DataUtils;
import edu.fiu.cs.seniorproject.utils.Logger;

public class DataManager {

	private LinkedList<DataProvider> mProviderList = new LinkedList<DataProvider>();
	private static DataManager mSingleton = null;
	
	private List<Event> mEventList = null;
	private List<Place> mPlaceList = null;
	
	private DataManager() {
		//register all the provider
		mProviderList.add(new MBVCAProvider());
		mProviderList.add(new GPProvider());
		mProviderList.add(new EventFullProvider());
	}
	
	public void enableProvider( SourceType source, boolean enabled ) {
		Logger.Debug("Enable data source " + source.toString() + " enabled=" + enabled );
		for (DataProvider provider : this.mProviderList) {
			if ( provider.getSource() == source ) {
				provider.setEnabled(enabled);
			}
		}
	}
	
	public List<Event> getEventListFromMB(Location location, EventCategoryFilter category, String radius, String query, DateFilter date) {

		List<Event> providerList = null;
		this.mEventList = null;
		if ( mProviderList.size() > 0 ) 
		{
			
			DataProvider provider = mProviderList.get(0);
			
			if (provider.isEnabled() ) 
			{					
					
					try {
						providerList = provider.getEventList(location, category, radius, query, date);
					} 
					catch ( Exception e ) 
					{
						providerList = null;
						Logger.Error("Exception getting event list " + e.getMessage());
					}			
				
			}
		}		
		return providerList;
	}
	
	public List<Event> getEventList(Location location, EventCategoryFilter category, String radius, String query, DateFilter date) {
		List<Event> result = null;
		
		this.mEventList = null;
		if ( mProviderList.size() > 0 ) {
			
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				
				
				if ( provider.supportEvents() && provider.isEnabled() ) {
					
					List<Event> providerList = null;
					try {
						providerList = provider.getEventList(location, category, radius, query, date);
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
	
	public ConcurrentEventListLoader getConcurrentEventList(Location location, EventCategoryFilter category, String radius, String query, DateFilter date) {
		this.mEventList = null;
		ConcurrentEventListLoader loader = new ConcurrentEventListLoader(false);
		loader.execute(location, category, radius, query, date);
		return loader;
	}
	
	public ConcurrentEventListLoader getConcurrentNextEventList() {
		ConcurrentEventListLoader loader = new ConcurrentEventListLoader(true);
		loader.execute(null, null, null, null, null);
		return loader;
	}
	
	public List<Place> getPlaceList(Location location, PlaceCategoryFilter category, String radius, String query) {
		List<Place> result = null;
		
		this.mPlaceList = null;
		if ( mProviderList.size() > 0 ) {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				
				if ( provider.supportPlaces() && provider.isEnabled() ) {
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
	
	public List<Place> getPlaceList2(Location location, PlaceCategoryFilter category, String radius, String query) {
		
		List<Place> result = null;		
		this.mPlaceList = null;
		if ( mProviderList.size() > 0 ) {
		//	for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(0);
				
				if ( provider.supportPlaces() && provider.isEnabled() ) {
				//	List<Place> providerList = null;
					try {
						result = provider.getPlaceList(location, category, radius, query);
					} catch (Exception e) {
						result = null;
						Logger.Error("Exception getting place list " + e.getMessage() );
					}
					
					//if ( providerList != null && providerList.size() > 0 )					{
					//	if ( result == null ) {
					//		result = providerList;
					//	} else {
					//		result.addAll(providerList);
					//	}
					//}
				}
			//}
		}
		return result;
	}
	
	public ConcurrentPlaceListLoader getConcurrentPlaceList(Location location, PlaceCategoryFilter category, String radius, String query) {
		this.mPlaceList = null;
		ConcurrentPlaceListLoader loader = new ConcurrentPlaceListLoader(false);
		loader.execute(location, category, radius, query);
		return loader;
	}
	
	
	public ConcurrentPlaceListLoader getConcurrentNextPlaceList() {
		Logger.Debug("get concurrent place list");
		ConcurrentPlaceListLoader loader = new ConcurrentPlaceListLoader(true);
		loader.execute(null, null, null, null);
		return loader;
	}
	
	public Event getEventDetails( String eventId, SourceType source ) {
		Event result = null;
		
		try {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				if ( provider.isEnabled() && provider.supportEvents() && provider.getSource() == source ) {
					result = provider.getEventDetails(eventId,null);
					break;
				}
			}
		} catch (Exception e) {
			Logger.Error("Exception getting event details " + e.getMessage() );
		}
		return result;
	}
	
	public Place getPlaceDetails( String placeId, SourceType source ) {
		Place result = null;
		
		try {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				if ( provider.isEnabled() && provider.supportPlaces() && provider.getSource() == source ) {
					result = provider.getPlaceDetails(placeId);
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
			(new BitmapDownloader(target)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
		} catch (Exception e) {
			Logger.Error("exception downloading bitmap " + e.getMessage());
		}
	}
	
	public void downloadBitmap( String url, IBitmapDownloaderListener listener ) {
		try {
			(new BitmapDownloader(listener)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url);
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
	
	synchronized private List<Event> filterEventList( List<Event> list ) {
		List<Event> result = null;
		if ( this.mEventList == null ) {
			this.mEventList = list;
			result = list;
		} else if ( list != null ) {
			result = new ArrayList<Event>(list.size());
			
			for (Event newEvent : list) {
				boolean alreadyExist = false;
				
				for (Event currentEvent : this.mEventList) {
					if ( DataUtils.isSameEvent(currentEvent, newEvent)) {
						Logger.Debug( "Event merged a = " + currentEvent.toString() + " b = " + newEvent.toString() );
						alreadyExist = true;
						break;
					}
				}
				
				if ( !alreadyExist ) {
					result.add(newEvent);
				}
			}
			
			this.mEventList.addAll(result);
		}
		return result;
	}
	
	synchronized private List<Place> filterPlaceList( List<Place> list ) {
		List<Place> result = null;
		if ( this.mPlaceList == null ) {
			this.mPlaceList = list;
			result = list;
		} else if ( list != null ){
			result = new ArrayList<Place>(list.size());			
			for(Place newPlace : list ){
				boolean alreadyExist = false;
				
				for (Place currentPlace : this.mPlaceList) {
					if ( DataUtils.isSamePlace(currentPlace, newPlace)) {
						Logger.Debug("Place merged a=" + currentPlace.toString() + " b=" + newPlace.toString());
						alreadyExist = true;
						break;
					}
				}
				
				if ( !alreadyExist ) {
					result.add(newPlace);
				}
			}
			this.mPlaceList.addAll(result);
		}
		return result;
	}
	
	public interface IBitmapDownloaderListener {
		void OnCompleted(Bitmap bm);
		void OnFail();
	}
	
	private class BitmapDownloader extends AsyncTask<String, Void, Bitmap> {

		private final WeakReference<ImageView> mImageView;
		private final WeakReference<IBitmapDownloaderListener> mListener;
		
		public BitmapDownloader(ImageView target) {
			mImageView = new WeakReference<ImageView>(target);
			mListener = null;
		}
		
		public BitmapDownloader(IBitmapDownloaderListener listener) {
			mImageView = null;
			mListener = new WeakReference<IBitmapDownloaderListener>(listener);
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			return RestClient.downloadBitmap(params[0]);
		}
		
		@Override
		protected void onPostExecute(Bitmap bm) {
			if ( mListener != null && mListener.get() != null ) {
				IBitmapDownloaderListener listener = mListener.get();
				if ( bm != null ) {
					listener.OnCompleted(bm);
				} else {
					listener.OnFail();
				}
			}
			else if ( bm != null && mImageView != null && mImageView.get() != null ) {
				mImageView.get().setImageBitmap(bm);
			}
		}
	}
	
	public class ConcurrentPlaceListLoader {
		private final AtomicInteger counter = new AtomicInteger(0);
		private final List<FutureTask<List<Place>>> mTaskList = new ArrayList<FutureTask<List<Place>>>(mProviderList.size());
		private final BlockingQueue<List<Place>> mResultQueue = new ArrayBlockingQueue<List<Place>>(mProviderList.size());
		
		private boolean useNextPage = false;
		
		private ConcurrentPlaceListLoader( boolean useNext ) {
			this.useNextPage = useNext;
		}
		
		protected void execute(final Location location, final PlaceCategoryFilter category, final String radius, final String query) {
			
			if ( mProviderList.size() > 0 ) {
				for( int i = 0; i < mProviderList.size(); i++ ) {
					final DataProvider provider = mProviderList.get(i);
					
					if ( provider != null && provider.supportPlaces() && provider.isEnabled() ) {
						Callable<List<Place>> worker = new Callable<List<Place>>() {
							@Override
							public List<Place> call() throws Exception {
								List<Place> list = null;
								try {
									list = useNextPage ? provider.getNextPlacePage() : provider.getPlaceList(location, category, radius, query);
								} catch (Exception e) {
									list = null;
									Logger.Error("Exception getting event list from provider " + e.getMessage());
								}
								
								if ( list == null ) {
									list = new ArrayList<Place>(0);
								}
								return list;
							}
						};
						
						FutureTask<List<Place>> future = new FutureTask<List<Place>>(worker) {
							@Override
				            protected void done() {
								try {
									List<Place> result = get();
									if ( result != null ) {
										mResultQueue.put(result);
									}
								} catch (InterruptedException e) {
									Logger.Error("Exception thread was intereupted " + e.getMessage());
								} catch (ExecutionException e) {
									Logger.Error("Exception ExecutionException " + e.getMessage() );
								} catch (CancellationException e ) {
									Logger.Error("Exception CancellationException " + e.getMessage() );
								}
							}
						};
						
						mTaskList.add(future);
						AsyncTask.THREAD_POOL_EXECUTOR.execute(future);
						counter.incrementAndGet();
					}
				}
			}			
		}

		synchronized public void cancel() {
			for (FutureTask<List<Place>> future : mTaskList) {
				if ( !future.isDone() && !future.isCancelled() ) {
					future.cancel(true);
				}
			}
		}
		
		public List<Place> getNext() {
			List<Place> result = null;
			if ( counter.get() > 0 ) {
				try {
					List<Place> list = mResultQueue.take();
					result = filterPlaceList( list );
				} catch (InterruptedException e) {
					Logger.Error("InterruptedException exception getting result " + e.getMessage());
				}
				counter.decrementAndGet();
			} else {
				this.cancel();	// make sure all of them are cancelled
			}
			return result;
		}
	}
	
	public class ConcurrentEventListLoader {
		private final AtomicInteger counter = new AtomicInteger(0);
		private final List<FutureTask<List<Event>>> mTaskList = new ArrayList<FutureTask<List<Event>>>( mProviderList.size() );
		private final BlockingQueue<List<Event>> mResultQueue = new ArrayBlockingQueue<List<Event>>(mProviderList.size());
		
		private boolean useNextPage = false;
		
		private ConcurrentEventListLoader( boolean useNext ) {
			this.useNextPage = useNext;
		}
		
		protected void execute(final Location location, final EventCategoryFilter category, final String radius, final String query, final DateFilter date) {
			
			if ( mProviderList.size() > 0 ) {
				for( int i = 0; i < mProviderList.size(); i++ ) {
					final DataProvider provider = mProviderList.get(i);
					
					if ( provider != null && provider.supportEvents() && provider.isEnabled()) {
						Callable<List<Event>> worker = new Callable<List<Event>>() {
							@Override
							public List<Event> call() throws Exception {
								List<Event> list = null;
								try {
									list = useNextPage ? provider.getNextEventPage() : provider.getEventList(location, category, radius, query, date);
								} catch (Exception e) {
									list = null;
									Logger.Error("Exception getting event list from provider " + e.getMessage());
								}
								
								if ( list == null ) {
									list = new ArrayList<Event>(0);
								}
								return list;
							}
						};
						
						FutureTask<List<Event>> future = new FutureTask<List<Event>>(worker) {
							@Override
				            protected void done() {
								try {
									List<Event> result = get();
									if ( result != null ) {
										mResultQueue.put(result);
									}
								} catch (InterruptedException e) {
									Logger.Error("Exception thread was intereupted " + e.getMessage());
								} catch (ExecutionException e) {
									Logger.Error("Exception ExecutionException " + e.getMessage() );
								} catch ( CancellationException e ) {
									Logger.Error("Exception CancellationException " + e.getMessage() );
								}
							}
						};
						
						mTaskList.add(future);
						AsyncTask.THREAD_POOL_EXECUTOR.execute(future);
						counter.incrementAndGet();
					}
				}
			}			
		}
		
		synchronized public void cancel() {
			for (FutureTask<List<Event>> future : mTaskList) {
				if ( !future.isDone() && !future.isCancelled() ) {
					future.cancel(true);
				}
			}
		}
		
		public List<Event> getNext() {
			List<Event> result = null;
			if ( counter.get() > 0 ) {
				try {
					List<Event> list = mResultQueue.take();
					result = filterEventList( list );
				} catch (InterruptedException e) {
					Logger.Error("InterruptedException exception getting result " + e.getMessage());
				}
				counter.decrementAndGet();
			} else {
				this.cancel();	// make sure all of them are cancelled
			}
			return result;
		}
	}
}
