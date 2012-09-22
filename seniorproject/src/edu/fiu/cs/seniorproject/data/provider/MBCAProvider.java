package edu.fiu.cs.seniorproject.data.provider;

import java.io.IOException;
//import java.lang.reflect.Array;
import java.net.MalformedURLException;
//import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.provider.ContactsContract.CommonDataKinds.Event;

import edu.fiu.cs.seniorproject.client.MBCAClient;
import edu.fiu.cs.seniorproject.config.AppConfig;
import edu.fiu.cs.seniorproject.utils.Logger;

public abstract class MBCAProvider extends DataProvider 
{
	private MBCAClient MBCAClient;
	
	public MBCAProvider()
	{
		this.MBCAClient  = new MBCAClient(AppConfig.MBCA_APP_ID);
	}

	public List<?> parseEvent(String reference, String eventId) throws MalformedURLException, IOException, JSONException  {
		
		String result = null;
		JSONObject data = null;

		try 
		{
			result = this.MBCAClient.getEvent(reference, eventId);
		} 
		catch (MalformedURLException e) 
		{
			Logger.Error("MalformedURLException in parseEvent");
		} 
		catch (IOException e) 
		{
			Logger.Error("IOException in parseEvent");
		}
		
		try
		{
			data = new JSONObject(result);
		}
		catch(JSONException e)
		{
			Logger.Error("JSONException in parseEvent");
		}
		
		JSONArray eventDetails = data.getJSONArray("result");
		
		return (List<?>)eventDetails;
		
	}
	
	public List<?> parsePlaces(String longitude, String latitude, String radius, String types, String name) throws MalformedURLException, IOException
	{
		
		String result = null;
		JSONObject data = null;
		
		try 
		{
			result = this.MBCAClient.getPlaces(longitude, latitude, radius, types, name);
		} 
		catch (MalformedURLException e) 
		{
			Logger.Error("MalformedURLException in parseEvent");
		} 
		catch (IOException e) 
		{
			Logger.Error("IOException in parseEvent");
		}
		
		try
		{
			data = new JSONObject(result);
		}
		catch(JSONException e)
		{
			Logger.Error("JSONException in parseEvent");
		}
		
//		try{
				    //Get the element that holds the earthquakes ( JSONArray )
//				    JSONArray  earthquakes = data.getJSONArray("earthquakes");
				 
				            //Loop the Array
//				        for(int i=0;i < earthquakes.length();i++){                      
//				 
//				            HashMap<String, String> map = new HashMap<String, String>();
//				            JSONObject e = earthquakes.getJSONObject(i);
//				 
//				            map.put("id",  String.valueOf(i));
//				            map.put("name", "Earthquake name:" + e.getString("eqid"));
//				            map.put("magnitude", "Magnitude: " +  e.getString("magnitude"));
//				            mylist.add(map);
//				    }
//				    }catch(JSONException e)        {
//				         Log.e("log_tag", "Error parsing data "+e.toString());
//				    }
		
		
		return null;
//		}
	}
	
	
}
