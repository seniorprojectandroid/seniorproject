package edu.fiu.cs.seniorproject.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import edu.fiu.cs.seniorproject.data.Place;
import android.app.Activity;
import android.content.res.Resources;

public class JsonParser
{
	
	public JsonParser(){};
	
	public ByteArrayOutputStream getJsonFromSRC(Activity activity) 	
	{
		 Resources res = activity.getResources();	 
	
	     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	     
	     int ctr;
	        try {
	        	InputStream inputStream = res.getAssets().open("tours.json");
	            ctr = inputStream.read();
	            while (ctr != -1) {
	                byteArrayOutputStream.write(ctr);
	                ctr = inputStream.read();
	            }
	            inputStream.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        Logger.Debug("Text Data", byteArrayOutputStream.toString());
		
	     return byteArrayOutputStream;
     
	}// end getJsonFromSRC
	
	public List<String> getToursName(ByteArrayOutputStream parser)
	{
		List<String> list = new ArrayList<String>();
		
		try {
			JSONObject toursObject = new JSONObject(parser.toString());
			
			if ( toursObject != null && toursObject.has("tours") && !toursObject.isNull("tours") )
			{
				JSONObject tours = toursObject.getJSONObject("tours");
				
				if ( tours != null && tours.has("tour") && !tours.isNull("tour"))
				{
					JSONArray jsonToursList = null;
					
					try {
						
						jsonToursList = tours.getJSONArray("tour");
						
					} catch (JSONException e) {
						
						jsonToursList = new JSONArray();
						jsonToursList.put(tours.getJSONObject("tour"));
					}
					
					if ( jsonToursList != null && jsonToursList.length() > 0 )
					{
												
						for( int i = 0; i < jsonToursList.length(); i++ )
						{
							JSONObject iter = jsonToursList.getJSONObject(i);
							
							if ( iter != null && iter.has("name"))
							{
								list.add(iter.getString("name"));
							}
						}// end for
					
					}// end if
				}// end if
			}// end if
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
		return list;
	}// end getTourName
	
	public List<Place> getTourByName(ByteArrayOutputStream parser, String tourName)
	{
		List<Place> list = new ArrayList<Place>();
		
		try {
			JSONObject toursObject = new JSONObject(parser.toString());
			
			if ( toursObject != null && toursObject.has("tours") && !toursObject.isNull("tours") )
			{
				JSONObject tours = toursObject.getJSONObject("tours");
				
				if ( tours != null && tours.has("tour") && !tours.isNull("tour"))
				{
					JSONArray jsonToursList = null;
					
					try {
						
						jsonToursList = tours.getJSONArray("tour");
						
					} catch (JSONException e) {
						
						jsonToursList = new JSONArray();
						jsonToursList.put(tours.getJSONObject("tour"));
					}
					
					if ( jsonToursList != null && jsonToursList.length() > 0 )
					{
												
						for( int i = 0; i < jsonToursList.length(); i++ )
						{
							JSONObject iter = jsonToursList.getJSONObject(i);
							
							if ( iter != null && iter.has("name") && iter.getString("name").equalsIgnoreCase(tourName))
							{
								JSONObject tourObject = iter.getJSONObject("name");
								JSONArray placesArray = tourObject.getJSONArray("place");
								
								for(int j=0; j<placesArray.length(); j++)
								{
									JSONObject jsonPlace = placesArray.getJSONObject(j);
									
									if(jsonPlace != null)
									{										
										list.add(parsePlace(jsonPlace));
									}
								}// end for
								
							}// end if
						}// end for
					
					}// end if
				}// end if
			}// end if
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
		return list;		
		
	}// end getTourByName
	
	private Place parsePlace( JSONObject jsonPlace )
	{
		Place place = new Place();
		try {
			if ( jsonPlace.has("image") && jsonPlace.isNull("image"))
			{
				place.setImage(jsonPlace.getString("image"));
			}
			
			if ( jsonPlace.has("imageBase64") && jsonPlace.isNull("imageBase64"))
			{
				place.setImage(jsonPlace.getString("imageBase64"));
			}
			
			if ( jsonPlace.has("name") && jsonPlace.isNull("name"))
			{
				place.setImage(jsonPlace.getString("name"));
			}
			
			if ( jsonPlace.has("description") && jsonPlace.isNull("description"))
			{
				place.setImage(jsonPlace.getString("description"));
			}
			
			if ( jsonPlace.has("address") && jsonPlace.isNull("address"))
			{
				place.setImage(jsonPlace.getString("address"));
			}
			
			if ( jsonPlace.has("latitude") && jsonPlace.isNull("latitude"))
			{
				place.setImage(jsonPlace.getString("latitude"));
			}
			
			if ( jsonPlace.has("longitude") && jsonPlace.isNull("longitude"))
			{
				place.setImage(jsonPlace.getString("longitude"));
			}
			
			if ( jsonPlace.has("telephone") && jsonPlace.isNull("telephone"))
			{
				place.setImage(jsonPlace.getString("telephone"));
			}
			
			if ( jsonPlace.has("category") && jsonPlace.isNull("category"))
			{
				place.setImage(jsonPlace.getString("category"));
			}
			
		} catch (JSONException e ) {
			place = null;
			Logger.Error("Exception decoding place from json " + e.getMessage() );
		}
		return place;
	}// end parsePlace

}// end JsonParser
