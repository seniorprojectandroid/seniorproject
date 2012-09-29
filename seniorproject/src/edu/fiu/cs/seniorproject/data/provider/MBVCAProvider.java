package edu.fiu.cs.seniorproject.data.provider;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.fiu.cs.seniorproject.client.MBVCAClient;
import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.utils.Logger;

public class MBVCAProvider extends DataProvider 
{
	private final static String IMAGE_BASE_URL = "http://www.miamibeachapi.com";
	
	private final MBVCAClient mMBVCAClient;
	private final Hashtable<String, Event> mEventMap;
	
	public MBVCAProvider()
	{
		this.mMBVCAClient  = new MBVCAClient(AppConfig.MBVCA_APP_ID);
		this.mEventMap = new Hashtable<String, Event>();
	}

	@Override
	public List<Event> getEventList(Location location, String category,
			String radius, String query) {
		List<Event> result = null;
		
		String events = mMBVCAClient.getEventList();
		if ( events != null && !events.isEmpty() ) {
			try {
				JSONObject eventsObject = new JSONObject(events);
				
				if ( eventsObject != null && eventsObject.has("solodev_view")) {
					JSONArray eventList = eventsObject.getJSONArray("solodev_view");
					
					if ( eventList != null && eventList.length() > 0 ) {
						
						result = new LinkedList<Event>();
						mEventMap.clear();
						for( int i = 0; i < eventList.length(); i++ ) {
							JSONObject iter = eventList.getJSONObject(i);
							
							if ( iter.has("calendar_entry_id") && iter.has("name")) {
								Event event = new Event();
								event.setId(iter.getString("calendar_entry_id"));
								event.setName(iter.getString("name"));
								
								if ( iter.has("description")) {
									event.setDescription(iter.getString("description"));
								}
								
								if ( iter.has("lat") && iter.has("lng")) {
									Location eventLocation = new Location(String.valueOf( iter.getDouble("lat") ),String.valueOf(iter.getDouble("lng")) );
									
									if ( iter.has("venue") ) {
										eventLocation.setAddress( iter.getString("venue"));
									} else if ( iter.has("location")) {
										eventLocation.setAddress("location");
									}
									
									event.setLocation(eventLocation);
								}

								if ( iter.has("start_time")) {
									event.setTime(String.valueOf(iter.getInt("start_time")));
								}
								
								if ( iter.has("image")) {
									String image = iter.getString("image");
									
									if ( !image.isEmpty() && !image.equals("null")) {
										event.setImage( IMAGE_BASE_URL + iter.getString("image"));
									}
								}
								
								event.setSource(SourceType.MBVCA);
								result.add(event);
								mEventMap.put(event.getId(), event);
							}
						}
					}
				}
			} catch (JSONException e) {
				Logger.Error("Exception decoding json object in MBVCA " + e.getMessage());
			}
			
		}
		return result;
	}

	@Override
	public List<Place> getPlaceList(Location location, String category,
			String radius, String query) {
		return null;
	}

	@Override
	public Event getEventDetails(String eventId, String reference) {
		return mEventMap.get(eventId);
	}

	@Override
	public Place getPlaceDetails(String placeId, String reference) {
		return null;
	}

	@Override
	public SourceType getSource() {
		return SourceType.MBVCA;
	}
	
	
}
