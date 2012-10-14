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
	
	public List<Event> getEventList(Location location, EventCategoryFilter category, String radius, String query, DateFilter date) {
		List<Event> result = null;
		
		if ( mProviderList.size() > 0 ) {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				
				if ( provider.supportEvents() ) {
					
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
		ConcurrentEventListLoader loader = new ConcurrentEventListLoader();
		loader.execute(location, category, radius, query, date);
		return loader;
	}
	
	public List<Place> getPlaceList(Location location, PlaceCategoryFilter category, String radius, String query) {
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
	
	public ConcurrentPlaceListLoader getConcurrentPlaceList(Location location, PlaceCategoryFilter category, String radius, String query) {
		Logger.Debug("get concurrent place list");
		ConcurrentPlaceListLoader loader = new ConcurrentPlaceListLoader();
		loader.execute(location, category, radius, query);
		return loader;
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
	
	public Place getPlaceDetails( String placeId, SourceType source ) {
		Place result = null;
		
		try {
			for( int i = 0; i < mProviderList.size(); i++ ) {
				DataProvider provider = mProviderList.get(i);
				if ( provider.supportPlaces() && provider.getSource() == source ) {
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
	
	public class ConcurrentPlaceListLoader {
		private final AtomicInteger counter = new AtomicInteger(0);
		private final List<FutureTask<List<Place>>> mTaskList = new ArrayList<FutureTask<List<Place>>>(mProviderList.size());
		private final BlockingQueue<List<Place>> mResultQueue = new ArrayBlockingQueue<List<Place>>(mProviderList.size());
		
		private ConcurrentPlaceListLoader() {
		}
		
		protected void execute(final Location location, final PlaceCategoryFilter category, final String radius, final String query) {
			
			if ( mProviderList.size() > 0 ) {
				for( int i = 0; i < mProviderList.size(); i++ ) {
					final DataProvider provider = mProviderList.get(i);
					
					if ( provider != null && provider.supportPlaces() ) {
						Callable<List<Place>> worker = new Callable<List<Place>>() {
							@Override
							public List<Place> call() throws Exception {
								List<Place> list = null;
								try {
									list = provider.getPlaceList(location, category, radius, query);
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
					result = mResultQueue.take();
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
		
		private ConcurrentEventListLoader() {			
		}
		
		protected void execute(final Location location, final EventCategoryFilter category, final String radius, final String query, final DateFilter date) {
			
			if ( mProviderList.size() > 0 ) {
				for( int i = 0; i < mProviderList.size(); i++ ) {
					final DataProvider provider = mProviderList.get(i);
					
					if ( provider != null && provider.supportEvents() ) {
						Callable<List<Event>> worker = new Callable<List<Event>>() {
							@Override
							public List<Event> call() throws Exception {
								List<Event> list = null;
								try {
									list = provider.getEventList(location, category, radius, query, date);
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
					result = mResultQueue.take();
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
