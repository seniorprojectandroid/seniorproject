package edu.fiu.cs.seniorproject.client;

import java.io.IOException;
import java.net.MalformedURLException;

import android.os.Bundle;

public class RestClient {
	
	private String appId = null;
	
	
	public RestClient(String appID)
	{
		this.appId = appId;
	}
	
	public static String encodeUrl(Bundle parameters)
	{
		
		return "";
	}
	
	  
	public static String openUrl(String url, String method, Bundle params)
									throws MalformedURLException, IOException {
		  
		  return "";
	}
}
