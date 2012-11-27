package edu.fiu.cs.seniorproject.utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import edu.fiu.cs.seniorproject.data.Location;
import edu.fiu.cs.seniorproject.data.Place;

import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class XMLParser
{
	
	public XMLParser(){}	
	
	//Getting XML content from src xml
	public XmlResourceParser getXMLFromSRC(Activity activity, int r) 			
   {
	  
	   Resources res = activity.getResources();
	   XmlResourceParser xpp = res.getXml(r);// tour is the xml index file on res	   
	   
	   return xpp;
   }// end getTourFromAnXML
	
	public List<String> getTourName(XmlResourceParser parser)
	{
		List<String> list = new ArrayList<String>();
		try {
			
			while( parser.next() != XmlPullParser.END_DOCUMENT )
			{
				if (parser.getEventType() != XmlPullParser.START_TAG)
				{
					continue;
				}
			
				String name = parser.getName();
								
				if (name.equals("tour"))
				{
					String tourname = parser.getAttributeValue(null, "name");
					
					list.add(tourname);
				}
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		return list;
	}// name
	
	public List<Place> getTourByName(XmlResourceParser parser, String tourName)
	{
		List<Place> list = new ArrayList<Place>();
		try {
			
			while( parser.next() != XmlPullParser.END_DOCUMENT )
			{
				if (parser.getEventType() != XmlPullParser.START_TAG)
				{
					continue;
				}
			
				String name = parser.getName();
				String tourname = parser.getAttributeValue(null, "name");
								
				if (name.equals("tour") && tourname.equals(tourName))
				{
					while (!(parser.next() == XmlPullParser.END_TAG && parser.getName().equals("tour")) )
					{						
						Place place = new Place();
						place.setLocation(new Location());
						
						while (!(parser.next() == XmlPullParser.END_TAG && parser.getName().equals("place")) )
						{
							if ( parser.getEventType() == XmlPullParser.START_TAG ) {
							
								name = parser.getName();
								
								if ( name.equals("image")) {
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.setImage(parser.getText());
										parser.nextTag();
									}
								}
								
								if ( name.equals("imageBase64")) {
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.setImageBase64(parser.getText());
										parser.nextTag();
									}
								}
								
								if ( name.equals("name")) {
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.setName( parser.getText() );
										parser.nextTag();
									}
								}
								
								if ( name.equals("description")) {
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.setDescription(parser.getText() );
										parser.nextTag();
									}
								}	
								
								if (name.equals("address"))
								{
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.getLocation().setAddress( parser.getText() );
										parser.nextTag();
									}									
								}
								
								if (name.equals("latitude"))
								{
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.getLocation().setLatitude( parser.getText() );
										parser.nextTag();
									}					
								}
								
								if (name.equals("longitude"))
								{
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.getLocation().setLongitude( parser.getText() );
										parser.nextTag();
									}	
								}
								
								if (name.equals("telephone"))
								{					
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.setTelephone( parser.getText() );
										parser.nextTag();
									}					
								}
								
								if (name.equals("category"))
								{	
									if ( parser.next() == XmlPullParser.TEXT ) {
										place.setCategory( parser.getText() );
										parser.nextTag();
									}	
								}
							}							
							
						}// end while place					
						list.add(place);
					}// end while tour
					
				}// end if
			}// end while
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		return list;
	}// name

}// end XMLParser
