package edu.fiu.cs.seniorproject.data.provider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.fiu.cs.seniorproject.client.EventfulRestClient;
import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;
import edu.fiu.cs.seniorproject.utils.Logger;

public class EventFullProvider extends DataProvider
{
	private EventfulRestClient myRestClient;
	
	private final static String IMAGE_BASE_URL = "http://www.eventful.com";	
	
		
	 public EventFullProvider()
	 {
		 this.myRestClient = new EventfulRestClient(AppConfig.EVENTFUL_APP_ID);
	 }// EventFullProvider
	 
	 public List<Event> getEventList(Location location, String category, String radius, String query ) 
	 {
		 List<Event> myEventList = null;
		 String myListRequestClient = this.myRestClient.getEventList(query, location, null, category, (int)Math.ceil(Double.valueOf(radius))); 
		 if ( myListRequestClient != null && !myListRequestClient.isEmpty() )
		 {
				try {
					JSONObject eventsObject = new JSONObject(myListRequestClient);
					if ( eventsObject != null && eventsObject.has("events"))
					{
						JSONObject events = eventsObject.getJSONObject("events");
						JSONArray jsonEventList = events.getJSONArray("event");
						
						if ( jsonEventList != null && jsonEventList.length() > 0 )
						{
							
							myEventList = new LinkedList<Event>();
							
							for( int i = 0; i < jsonEventList.length(); i++ )
							{
								JSONObject iter = jsonEventList.getJSONObject(i);
								
								Event event = this.parseEvent(iter);
								if ( event != null ) {
									myEventList.add(event);
								}
							}
						}
					}
				}
				catch (JSONException e)
				{
					Logger.Error("Exception decoding json object in MBVCA " );
					e.printStackTrace();
				}
		 }
		 return myEventList;
	 }// getEventList 
	
	 
	 @Override
	public Event getEventDetails(String eventId, String reference) {
		String eventStr = this.myRestClient.getEventDetails(eventId);
		
		Event event = null;
		
		if ( eventStr != null && !eventStr.isEmpty() ) {
			try {
				JSONObject json = new JSONObject(eventStr);
				if ( json!= null ) {
					event = this.parseEvent(json);
				}
			} catch (JSONException e) {
				event = null;
				Logger.Warning("Exception decoding json from eventful " + e.getMessage() );
			}
			
		}
		return event;
	}
	 
	 public List<Place> getPlaceList(Location location, String category, String radius, String query)
	 {	
		
		List<Place> myPlaceList = null;
		String myListRequestClient = this.myRestClient.getPlaceList(query, new Location("32.746682","-117.162741"), 10, 10,150);
		 if ( myListRequestClient != null && !myListRequestClient.isEmpty() )
		 {
				try {
					JSONObject placesObject = new JSONObject(myListRequestClient);
					if ( placesObject != null && placesObject.has("venues"))
					{
						JSONObject venues = placesObject.getJSONObject("venues");
						JSONArray jsonPlaceList = venues.getJSONArray("venue");
						
						if ( jsonPlaceList != null && jsonPlaceList.length() > 0 )
						{
							
							myPlaceList = new LinkedList<Place>();
							
							for( int i = 0; i < jsonPlaceList.length(); i++ )
							{
								JSONObject iter = jsonPlaceList.getJSONObject(i);
								
								Place place = this.parsePlace(iter);
								if ( place != null ) {
									myPlaceList.add(place);
								}
							}
						}
					}
				}
				catch (JSONException e)
				{
					Logger.Error("Exception decoding json object in MBVCA " );
					e.printStackTrace();
				}
		 }		
		 
		 return myPlaceList;
	 }// getPlaceList
	
	 @Override
	public Place getPlaceDetails(String placeId, String reference) {
		Place place = null;
		String placeStr = this.myRestClient.getPlaceDetails(placeId);
		
		if ( placeStr != null && !placeStr.isEmpty() ) {
			try {
				JSONObject json = new JSONObject(placeStr);
				if ( json != null ) {
					place = this.parsePlace(json);
				}
			} catch (JSONException e ) {
				place = null;
				Logger.Error("Exception decoding place from eventful " + e.getMessage());
			}
		}
		return place;
	}

	@Override
	public SourceType getSource() {
		return SourceType.EVENTFUL;
	}
	
	private Event parseEvent( JSONObject iter ) {
		Event event = null;
		
		try {
			if ( iter != null && iter.has("id") && iter.has("title"))
			{
				event = new Event();
				event.setId(iter.getString("id"));
				event.setName(iter.getString("title"));
				
				if ( iter.has("description"))
				{
					event.setDescription(iter.getString("description"));
				}
				
				// Process the location
				if ( iter.has("latitude") && iter.has("longitude"))
				{
					Location eventLocation = new Location(iter.getString("latitude"),iter.getString("longitude") );
					
					StringBuilder myAddress = new StringBuilder(110);	
					
					if ( iter.has("venue_address") )
					{
						myAddress.append(iter.getString("venue_address")+",");
					}
					else if ( iter.has("city_name"))
					{
						myAddress.append(iter.getString("city_name")+",");
					}
					else if( iter.has("region_abbr"))
					{
						myAddress.append(iter.getString("region_abbr")+",");
					}
					else if( iter.has("postal_code"))
					{
						myAddress.append(iter.getString("postal_code")+",");
					}
					
					eventLocation.setAddress(myAddress.toString());
					event.setLocation(eventLocation);										
				}//set the location
	
				if ( iter.has("start_time"))
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date;
					try {
						date = sdf.parse(iter.getString("start_time"));
						event.setTime(String.valueOf(date.getTime()/1000L));
					} catch (ParseException e) {											
						e.printStackTrace();
					}										
				}
				
				if ( iter.has("image") && !iter.isNull("image"))
				{
					JSONObject imageObject = iter.getJSONObject("image");								
					if(imageObject != null && imageObject.has("small"))
					{
						JSONObject small = imageObject.getJSONObject("small");
						if (small != null && small.has("url") && !small.getString("url").isEmpty() && !small.getString("url").equals("null"))
						{
							event.setImage( IMAGE_BASE_URL + small.getString("url"));
						}
					}										
				}
				event.setSource(SourceType.EVENTFUL);
			}
		} catch ( JSONException e ) {
			Logger.Warning("exception decoding json from eventful " + e.getMessage() );
		}
		return event;
	}
	
	private Place parsePlace( JSONObject iter )
	{
		Place place = null;
		try {
			if ( iter != null && iter.has("id") && iter.has("name"))
			{
				place = new Place();
				place.setId(iter.getString("id"));
				place.setSource(SourceType.EVENTFUL);
				place.setName(iter.getString("name"));	
				
				// Process the location
				if ( iter.has("latitude") && iter.has("longitude"))
				{
					Location placeLocation = new Location(iter.getString("latitude"),iter.getString("longitude") );
					
					StringBuilder myAddress = new StringBuilder(110);	
					
					if ( iter.has("venue_address") )
					{
						myAddress.append(iter.getString("venue_address")+",");
					}
					else if ( iter.has("city_name"))
					{
						myAddress.append(iter.getString("city_name")+",");
					}
					else if( iter.has("region_abbr"))
					{
						myAddress.append(iter.getString("region_abbr")+",");
					}
					else if( iter.has("postal_code"))
					{
						myAddress.append(iter.getString("postal_code")+",");
					}
					
					placeLocation.setAddress(myAddress.toString());
					place.setLocation(placeLocation);										
				}//set the location

				
				if ( iter.has("description") && iter.isNull("description"))
				{
					place.setDescription(iter.getString("description"));
				}
			}
		} catch (JSONException e ) {
			place = null;
			Logger.Error("Exception decoding place from eventful " + e.getMessage() );
		}
		return place;
	}
}// EventFullProvider



/////// http://api.evdb.com/json/events/get?app_key=HrsPRcW3W49b6hZq&id=E0-001-000278174-6


//public class EventFullProvider extends DataProvider
//{
//	private EventfulRestClient myRestClient = null;
//		
//	 public EventFullProvider()
//	 {
//		 this.myRestClient = new EventfulRestClient(AppConfig.EVENTFUL_APP_ID);
//	 }// EventFullProvider
//	 
//	 public List<Event> getEventList(Location location, String category, String radius, String query ) 
//	 {
//		 Event myEvent = null;   
//		 List<Event> myEventList = new LinkedList<Event>();
//		 String myListRequestClient = this.myRestClient.getEventList(query, new Location("32.746682","-117.162741"), null, category,10); // ojo con el signature
//		 
////		DocumentBuilder db = null;
////		
////		try { db = DocumentBuilderFactory.newInstance().newDocumentBuilder(); } 
////		catch (ParserConfigurationException e1) { e1.printStackTrace();	}
////		
////	    InputSource is = new InputSource();
////	    is.setCharacterStream(new StringReader(myListRequestClient));
////
////	    Document doc = null;
////		try { doc = db.parse(is);} 
////		catch (SAXException e) { e.printStackTrace(); } 
////		catch (IOException e) {	e.printStackTrace(); }
////		
////	    NodeList nodes = doc.getElementsByTagName("events");
////	    
////	    for (int i = 0; i < nodes.getLength(); i++)
////	    {
////	    	myEvent = new Event();    	
////	    	Element element = (Element) nodes.item(i);	    	
////	    	// Set the event values
////	    	this.setEvent(element, myEvent);   	
////	        // Add myEvent to the list
////	        myEventList.add(myEvent);
////	    }// end for		 
//		 				 
//		 return myEventList;
//		 
//	 }// getEventList 
//	
//	 
//	 public Event getEventDetails(String eventId)
//	 {
//		 String myEventXml = this.myRestClient.getEventDetails(eventId);
//		 
//		 DocumentBuilder db = null;		 
//			
//		try { db = DocumentBuilderFactory.newInstance().newDocumentBuilder(); } 
//		catch (ParserConfigurationException e1) { e1.printStackTrace();	}
//		
//	    InputSource is = new InputSource();
//	    is.setCharacterStream(new StringReader(myEventXml));
//
//	    Document doc = null;
//		try { doc = db.parse(is);} 
//		catch (SAXException e) { e.printStackTrace(); } 
//		catch (IOException e) {	e.printStackTrace(); }			
//		
//		NodeList nodes = doc.getElementsByTagName("events");    
//		
//		Event myEvent = new Event();    	
//    	Element element = (Element) nodes.item(0);   
//    	// Set the event values
//    	this.setEvent(element, myEvent);   	
//	 
//        return myEvent;
//	        
//	 }// getEventDetails 
//	 
//	 public List<Place> getPlaceList(Location location, String category, String radius, String query)
//	 {
//		
//		Place myPlace = null;   
//		List<Place> myListPlace = new LinkedList<Place>();
//		String myListRequestClient = this.myRestClient.getPlaceList(query, location, 10, 10, 10);
//		
//		DocumentBuilder db = null;		 
//		
//		try { db = DocumentBuilderFactory.newInstance().newDocumentBuilder(); } 
//		catch (ParserConfigurationException e1) { e1.printStackTrace();	}
//		
//	    InputSource is = new InputSource();
//	    is.setCharacterStream(new StringReader(myListRequestClient));
//
//	    Document doc = null;
//		try { doc = db.parse(is);} 
//		catch (SAXException e) { e.printStackTrace(); } 
//		catch (IOException e) {	e.printStackTrace(); }
//		
//	    NodeList nodes = doc.getElementsByTagName("venues");
//	    
//	    for (int i = 0; i < nodes.getLength(); i++)
//	    {
//	    	myPlace = new Place();  	
//	    	Element element = (Element) nodes.item(i);	    	
//	    	// Set the place values
//	    	this.setPlace(element, myPlace);   	
//	        // Add myPlace to the list
//	    	myListPlace.add(myPlace);
//	    }// end for	
//		 
//		 return myListPlace;
//	 }// getPlaceList
//	 
//	 
//	 public Place getPlaceDetails(String placeId)
//	 {
//		 		 
//		 String myEventXml = this.myRestClient.getPlaceDetails(placeId);		 
//		 DocumentBuilder db = null;		 
//			
//		 try { db = DocumentBuilderFactory.newInstance().newDocumentBuilder(); } 
//		 catch (ParserConfigurationException e1) { e1.printStackTrace(); }
//		
//	     InputSource is = new InputSource();
//	     is.setCharacterStream(new StringReader(myEventXml));
//
//	     Document doc = null;
//		 try { doc = db.parse(is);} 
//		 catch (SAXException e) { e.printStackTrace(); } 
//		 catch (IOException e) {	e.printStackTrace(); }			
//		
//		 NodeList nodes = doc.getElementsByTagName("venue");    
//		
//		 Place myPlace = new Place();    	
//    	 Element element = (Element) nodes.item(0);   
//    	// Set the place values
//    	 this.setPlace(element, myPlace);         
//		 
//		 return myPlace;
//	 }// getEventDetails
//	 
//	 @Override
//	public Place getPlaceDetails(String placeId, String reference) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public SourceType getSource() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	 
//	 
//	// helper to set the event values
//	 private void setEvent( Element element, Event myEvent)
//	 {
//		// Set event name
//        NodeList name = ((Document) element).getElementsByTagName("title");
//        Element line = (Element) name.item(0);        
//        myEvent.setName(getCharacterDataFromElement(line));
//        
//        // Set event time
//        NodeList time = ((Document) element).getElementsByTagName("start_time");
//        line = (Element) time.item(0);
//        myEvent.setTime(getCharacterDataFromElement(line));
//        
//        // Set event description
//        NodeList desscription = ((Document) element).getElementsByTagName("description");
//        line = (Element) desscription.item(0);
//        myEvent.setDescription(getCharacterDataFromElement(line));
//        
//        // Create Location Object
//        Location mylacation = new Location();
//    	//get latitude
//        NodeList nlatitude = ((Document) element).getElementsByTagName("latitude");
//        line = (Element) nlatitude.item(0);	        
//        mylacation.setLatitude(getCharacterDataFromElement(line));
//        //get longitude
//        NodeList nlongitude = ((Document) element).getElementsByTagName("longitude");
//        line = (Element) nlongitude.item(0);	        
//        mylacation.setLongitude(getCharacterDataFromElement(line));
//        
//        //String to hold the address
//        StringBuilder myAddress = new StringBuilder(110);
//        //get the address
//        NodeList naddress = ((Document) element).getElementsByTagName("venue_address");
//        line = (Element) naddress.item(0);	        
//        myAddress.append(getCharacterDataFromElement(line)+","); 
//        //get the city
//        NodeList city = ((Document) element).getElementsByTagName("city_name");
//        line = (Element) city.item(0);	        
//        myAddress.append(getCharacterDataFromElement(line)+",");	        
//        //get the region
//        NodeList region = ((Document) element).getElementsByTagName("region_abbr");
//        line = (Element) region.item(0);	        
//        myAddress.append(getCharacterDataFromElement(line)+","); 
//        //get the region
//        NodeList cPostal = ((Document) element).getElementsByTagName("postal_code");
//        line = (Element) cPostal.item(0);	        
//        myAddress.append(getCharacterDataFromElement(line)+"."); 
//        //set address
//        mylacation.setAddress(myAddress.toString());
//        
//        // Set myEvent Location
//        myEvent.setLocation(mylacation);	        
//        // Set myEvent source
//        myEvent.setSource(SourceType.EVENTFUL);	
//	 }// end setEvent
// 
//	// helper to set the event values
//	 private void setPlace( Element element, Place myPlace)
//	 {
//		// Set place id
//        NodeList id = ((Document) element).getElementsByTagName("venue id");
//        Element line = (Element) id.item(0);        
//        myPlace.setId(getCharacterDataFromElement(line));
//        
//        // Set place name
//        NodeList name = ((Document) element).getElementsByTagName("venue_name");
//        line = (Element) name.item(0);
//        myPlace.setName(getCharacterDataFromElement(line)); 
//       
//        // Create Location Object
//        Location mylacation = new Location();
//    	//get latitude
//        NodeList nlatitude = ((Document) element).getElementsByTagName("latitude");
//        line = (Element) nlatitude.item(0);	        
//        mylacation.setLatitude(getCharacterDataFromElement(line));
//        //get longitude
//        NodeList nlongitude = ((Document) element).getElementsByTagName("longitude");
//        line = (Element) nlongitude.item(0);	        
//        mylacation.setLongitude(getCharacterDataFromElement(line));
//        
//        //String to hold the address
//        StringBuilder myAddress = new StringBuilder(110);
//        //get the address
//        NodeList naddress = ((Document) element).getElementsByTagName("venue_address");
//        line = (Element) naddress.item(0);	        
//        myAddress.append(getCharacterDataFromElement(line)+","); 
//        //get the city
//        NodeList city = ((Document) element).getElementsByTagName("city_name");
//        line = (Element) city.item(0);	        
//        myAddress.append(getCharacterDataFromElement(line)+",");	        
//        //get the region
//        NodeList region = ((Document) element).getElementsByTagName("region_abbr");
//        line = (Element) region.item(0);	        
//        myAddress.append(getCharacterDataFromElement(line)+","); 
//        //get the region
//        NodeList cPostal = ((Document) element).getElementsByTagName("postal_code");
//        line = (Element) cPostal.item(0);	        
//        myAddress.append(getCharacterDataFromElement(line)+"."); 
//        //set address
//        mylacation.setAddress(myAddress.toString());
//        
//        //set location to myPlace
//        myPlace.setLocation(mylacation);
//        
//        // Set place description
//        NodeList desscription = ((Document) element).getElementsByTagName("description");
//        line = (Element) desscription.item(0);
//        myPlace.setDescription(getCharacterDataFromElement(line));
//        
//        // Set place description
//        NodeList url = ((Document) element).getElementsByTagName("url");
//        line = (Element) url.item(0);
//        myPlace.setWebsite(getCharacterDataFromElement(line));
//        
//        // see reference
//        
//	 }// end setEvent
//	 
//	 // helper method
//	 private static String getCharacterDataFromElement(Element e) 
//	 {
//	    Node child = ((Node) e).getFirstChild();
//	    if (child instanceof CharacterData)
//	    {
//	      CharacterData cd = (CharacterData) child;
//	      return cd.getData();
//	    }
//	    return "";
//	 }// end getCharacterDataFromElement	
//	 
//}// EventFullProvider
