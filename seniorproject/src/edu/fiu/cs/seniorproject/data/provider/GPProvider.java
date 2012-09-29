package edu.fiu.cs.seniorproject.data.provider;

import java.io.IOException;
//import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.util.LinkedList;
//import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.provider.ContactsContract.CommonDataKinds.Event;

import edu.fiu.cs.seniorproject.client.GPClient;
import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.utils.Logger;

public class GPProvider extends DataProvider {

	private GPClient gpClient;

	public GPProvider() {
		this.gpClient = new GPClient(AppConfig.GOOGLE_PLACE_APP_ID);
	}

	@Override
	public Event getEventDetails(String eventId, String reference) {
		JSONObject data = null;
		JSONObject results;
		String result = null;
		Event event = null;

		result = this.getEventDet(eventId, reference);

		if (result != null && result.length() > 0) {
			try {
				data = new JSONObject(result);

				if (data != null && data.has("result")) {
					event = new Event();
					results = data.getJSONObject("result");
					
					event.setTime(results.getString("start_time"));
					event.setDescription(results.getString("summary"));
					event.setSource(this.getSource());

				} else {
					Logger.Error("parseEvent: invalid data.");
				}

			} catch (JSONException e) {
				Logger.Error("JSONException in parseEvent");
			}
		} else {
			Logger.Error("parseEvent: invalid result.");
		}
		return event;

	}



	@Override
	public List<Event> getEventList(Location location, String category,
			String radius, String query) {
		
		String result = null;
		JSONObject data = null;
		JSONArray jsonArray = null;
		LinkedList<Event> eventList = new LinkedList<Event>();
		JSONObject eachPlace = null;
		Event event = null;
		Location loc = null;

		result = getPlaces(location, category, radius, query);

		if (result != null && result.length() > 0) {
			try {
				data = new JSONObject(result);
				jsonArray = data.getJSONArray("result");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				event = new Event();
				loc = new Location();
				JSONObject singleEvent;

				try {
					eachPlace = jsonArray.getJSONObject(i);
					
					if (eachPlace != null) {
						
						singleEvent = eachPlace.getJSONArray("events").getJSONObject(0);
						
						if(singleEvent != null)
						{
							event.setId(singleEvent.getString("event_id"));
							event.setDescription(singleEvent.getString("summary"));
							//singleEvent.getString("url");
							
							event.setName(eachPlace.getString("name"));
							event.setImage(eachPlace.getString("icon"));
							
							loc.setLatitude(eachPlace.getJSONObject("geometry").getJSONObject("location").getString("lat"));
							loc.setLongitude(eachPlace.getJSONObject("geometry").getJSONObject("location").getString("lng"));
							loc.setAddress(eachPlace.getString("formatted_address"));
							event.setLocation(loc);
							//event.setTime(time)
							event.setSource(this.getSource());
							
							eventList.add(event);
						}
					} else {
						Logger.Error("empty place");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			Logger.Error("");
		}

		return eventList;
	}
	
	@Override
	public Place getPlaceDetails(String placeId, String reference) {

		JSONObject data = null;
		JSONObject results;
		JSONObject resultLocation;
		String result = null;
		Place place = null;
		Location locat;

		result = this.getPlaceDet(reference);

		if (result != null && result.length() > 0) {
			try {
				data = new JSONObject(result);

				if (data != null && data.has("result")) {
					place = new Place();
					results = data.getJSONObject("result");

					// results.getString("formatted_address");
					// results.getString("formatted_phone_number");

					// name
					place.setName(results.getString("name"));

					// rating
					// results.getString("rating");

					resultLocation = results.getJSONObject("geometry")
							.getJSONObject("location");

					locat = new Location();

					locat.setLongitude(resultLocation.getString("lng"));
					locat.setLatitude(resultLocation.getString("lat"));

					place.setLocation(locat);

					// website
					place.setWebsite(results.getString("website"));

				} else {
					Logger.Error("parseEvent: invalid data.");
				}

			} catch (JSONException e) {
				Logger.Error("JSONException in parseEvent");
			}
		} else {
			Logger.Error("parseEvent: invalid result.");
		}
		return place;

	}

	@Override
	public List<Place> getPlaceList(Location location, String category,
			String radius, String query) {

		String result = null;
		JSONObject data = null;
		JSONArray jsonArray = null;
		LinkedList<Place> placeList = new LinkedList<Place>();
		JSONObject eachPlace = null;
		Place place = null;
		Location loc = null;

		result = getPlaces(location, category, radius, query);

		if (result != null && result.length() > 0) {
			try {
				data = new JSONObject(result);
				jsonArray = data.getJSONArray("result");

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int i = 0; i < jsonArray.length(); i++) {
				place = new Place();
				loc = new Location();

				try {
					eachPlace = jsonArray.getJSONObject(i);

					if (eachPlace != null) {
						loc.setLatitude(eachPlace.getJSONObject("geometry")
								.getJSONObject("location").getString("lat"));
						loc.setLongitude(eachPlace.getJSONObject("geometry")
								.getJSONObject("location").getString("lng"));
						place.setName(eachPlace.getString("name"));
						place.setReference(eachPlace.getString("reference"));
						place.setLocation(loc);
						place.setId(eachPlace.getString("id"));

						placeList.add(place);
					} else {
						Logger.Error("");
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			Logger.Error("");
		}

		return placeList;
	}

	private String getPlaces(Location location, String category, String radius,
			String query) {
		String result = null;

		try {
			result = this.gpClient.getPlaces(location, category, radius, query);
		} catch (MalformedURLException e) {
			Logger.Error("MalformedURLException in parseEvent");
		} catch (IOException e) {
			Logger.Error("IOException in parseEvent");
		}

		return result;

	}

	private String getPlaceDet(String reference) {
		String result = null;

		try {
			result = this.gpClient.getPlaceDetails(reference);
		} catch (MalformedURLException e) {
			Logger.Error("MalformedURLException in parseEvent");
		} catch (IOException e) {
			Logger.Error("IOException in parseEvent");
		}

		return result;
	}
	
	private String getEventDet(String eventId , String reference)
	{
		String result = null;
		
		result = this.gpClient.getEventDet(eventId, reference);

		return result;
	}

	@Override
	public boolean supportEvents() {
		return true;
	}

	@Override
	public SourceType getSource() {
		return SourceType.GOOGLE_PLACE;
	}

}
