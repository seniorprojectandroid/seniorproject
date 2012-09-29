package edu.fiu.cs.seniorproject.data.provider;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import edu.fiu.cs.seniorproject.client.EventfulRestClient;
import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.data.Event;
import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;
import edu.fiu.cs.seniorproject.data.SourceType;

public class EventFullProvider extends DataProvider
{
	private EventfulRestClient myRestClient = null;
		
	 public EventFullProvider()
	 {
		 this.myRestClient = new EventfulRestClient(AppConfig.EVENTFUL_APP_ID);
	 }// EventFullProvider
	 
	 public List<Event> getEventList(Location location, String category, String radius, String query ) 
	 {
		 
		 List<Event> myEventList = new LinkedList<Event>();
		 String myListRequestClient = this.myRestClient.getEventList(query, new Location("32.746682","-117.162741"), null, category,10); // ojo con el signature
		 XmlParser myparce = new XmlParser();
		 try {
			myEventList = myparce.parse(myListRequestClient);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return myEventList;
		 
	 }// getEventList 
	
	 
	 public Event getEventDetails(String eventId)
	 {
		 String myEventXml = this.myRestClient.getEventDetails(eventId);	 
			
		Event myEvent = new Event();    	
    	
	 
        return myEvent;
	        
	 }// getEventDetails 
	 
	 public List<Place> getPlaceList(Location location, String category, String radius, String query)
	 {
		
		Place myPlace = null;   
		List<Place> myListPlace = new LinkedList<Place>();
		String myListRequestClient = this.myRestClient.getPlaceList(query, location, 10, 10, 10);
		
		
		 
		 return myListPlace;
	 }// getPlaceList
	 
	 
	 public Place getPlaceDetails(String placeId)
	 {
		 		 
		 String myEventXml = this.myRestClient.getPlaceDetails(placeId);		 
		
		 Place myPlace = new Place();    	
    	 
		 return myPlace;
	 }// getEventDetails
	 
	 @Override
	public Place getPlaceDetails(String placeId, String reference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SourceType getSource() {
		// TODO Auto-generated method stub
		return SourceType.EVENTFUL;
	}
	
	// private class
	private class XmlParser
	{
		    private final String ns = null;
		   
		    public List<Event> parse(String xmlData) throws XmlPullParserException, IOException
		    {
		       
		            XmlPullParser parser = Xml.newPullParser();
		            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		            parser.setInput(new StringReader(xmlData));
		            parser.nextTag();
		            return readFeed(parser, xmlData,"events");		       
		    }
		    
		    private List<Event> readFeed(XmlPullParser parser, String xmlData, String object) throws XmlPullParserException, IOException
		    {		        
		    	
		    	List<Event> myEventList = new LinkedList<Event>();		    	
	
		        parser.require(XmlPullParser.START_TAG, ns, xmlData);// xmlData this is the name of the xml document
		        
		        while (parser.next() != XmlPullParser.END_TAG)
		        {
		            if (parser.getEventType() != XmlPullParser.START_TAG)
		            {
		                continue;
		            }
		            String name = parser.getName();
		            // Starts by looking for the entry tag
		            if (name.equals(object))// put the object name event in this case (events or avenues)
		            {
		            	myEventList.add(readEntry(parser,"events"));
		            }
		            else
		            {
		                skip(parser);
		            }
		        }  
		        return myEventList;
		    }// end readFeed
		    
		    private Event readEntry(XmlPullParser parser, String object) throws XmlPullParserException, IOException
		    {
		        parser.require(XmlPullParser.START_TAG, ns, object);// put the object name event in this case
	
		        Event myEvent = new Event();        
		        String data = ""; 		    	
		    	
		        while (parser.next() != XmlPullParser.END_TAG)
		        {
		            if (parser.getEventType() != XmlPullParser.START_TAG)
		            {
		                continue;
		            }
		            String name = parser.getName();
		            if (name.equals("title"))
		            {
		            	// set the event name
		                data = readTitle(parser,"title");
		                myEvent.setName(data);
		            } 
		            else if (name.equals("start_time"))
		            {
		            	// set the event time
		                data = readTitle(parser,"start_time"); // process the time 
		                myEvent.setTime(data);
		            } 
		            else if (name.equals("description"))
		            {
		            	data = readTitle(parser,"description");
		            	myEvent.setDescription(data);
		            } 
		            else
		            {
		                skip(parser);
		            }
		        }// end while
		        
		        return myEvent;
		        
		    }// end Entry
		    
		    // For the tags extracts their text values.
		    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException 
		    {
		        String result = "";
		        if (parser.next() == XmlPullParser.TEXT) {
		            result = parser.getText();
		            parser.nextTag();
		        }
		        return result;
		    }// end readText
		    
		   // Processes tags in the feed.
		    private String readTitle(XmlPullParser parser, String value) throws IOException, XmlPullParserException 
		    {
		        parser.require(XmlPullParser.START_TAG, ns, value);
		        String title = readText(parser);
		        parser.require(XmlPullParser.END_TAG, ns, value);
		        return title;
		    }// end readTitle
		    
		    
		    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException
		    {
		        if (parser.getEventType() != XmlPullParser.START_TAG)
		        {
		            throw new IllegalStateException();
		        }// end if
		        
		        int depth = 1;
		        while (depth != 0)
		        {
		            switch (parser.next())
		            {
		            case XmlPullParser.END_TAG:
		                depth--;
		                break;
		            case XmlPullParser.START_TAG:
		                depth++;
		                break;
		            }// end switch
		        }// end while
		     }// end skip
	
		 
		}// StackOverflowXmlParser

	@Override
	public Event getEventDetails(String eventId, String reference) {
		// TODO Auto-generated method stub
		return null;
	}
	 
	 
	
}// EventFullProvider


 
//// duda sobre el InputStream notes
//








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
