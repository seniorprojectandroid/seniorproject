/*
 * Copyright 2010 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.fiu.cs.seniorproject.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import edu.fiu.cs.seniorproject.utils.Logger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

public class RestClient {
	
	public static final String GET = "GET";
	public static final String POST = "POST";
	
	private static final String CHAR_SET = "UTF-8";
	
	protected String appId = null;
	
	public RestClient(String appID)
	{
		this.appId = appID;
	}
	
	protected String openUrl(String url, String method, Bundle params)
				throws MalformedURLException, IOException
	{
	
		// random string as boundary for multi-part http post
        String strBoundary = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";
        String endLine = "\r\n";

        OutputStream os;

        if (method.equals("GET")) {
            url = url + "?" + encodeUrl(params);
        }
        Logger.Info("Rest Client Open Url ", method + " URL: " + url);
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", System.getProperties().getProperty("http.agent") + " FacebookAndroidSDK");
        
        if (!method.equals("GET")) {
            Bundle dataparams = new Bundle();
            for (String key : params.keySet()) {
                Object parameter = params.get(key);
                if (parameter instanceof byte[]) {
                    dataparams.putByteArray(key, (byte[])parameter);
                }
            }
            
            conn.setRequestMethod("POST");
            conn.setRequestProperty( "Content-Type", "multipart/form-data;boundary="+strBoundary);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            os = new BufferedOutputStream(conn.getOutputStream());

            os.write(("--" + strBoundary +endLine).getBytes());
            os.write((encodePostBody(params, strBoundary)).getBytes());
            os.write((endLine + "--" + strBoundary + endLine).getBytes());

            if (!dataparams.isEmpty()) {

                for (String key: dataparams.keySet()){
                    os.write(("Content-Disposition: form-data; filename=\"" + key + "\"" + endLine).getBytes());
                    os.write(("Content-Type: content/unknown" + endLine + endLine).getBytes());
                    os.write(dataparams.getByteArray(key));
                    os.write((endLine + "--" + strBoundary + endLine).getBytes());

                }
            }
            os.flush();
        }

        String response = "";
        try {
        	signRequest(conn);	// sign the request if is needed
            response = read(conn.getInputStream());
        } catch (FileNotFoundException e) {
            // Error Stream contains JSON that we can parse to a FB error
            response = read(conn.getErrorStream());
        }
        return response;
	}// end openUrl
	
	private String encodeUrl(Bundle parameters)
	{
		if (parameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            Object parameter = parameters.get(key);
            if (!(parameter instanceof String)) {
                continue;
            }

            if (first) first = false; else sb.append("&");
            String encodedKey;
            String encodedValue;
			try {
				encodedKey = URLEncoder.encode(key, CHAR_SET);
				encodedValue = URLEncoder.encode(parameters.getString(key),CHAR_SET);
	            sb.append( encodedKey + "=" + encodedValue );
			} catch (UnsupportedEncodingException e) {
				Logger.Warning("Exception encoding params!!!");
			}
        }
        return sb.toString();
	}
	
	private String encodePostBody(Bundle parameters, String boundary) {
        if (parameters == null) return "";
        StringBuilder sb = new StringBuilder();

        for (String key : parameters.keySet()) {
            Object parameter = parameters.get(key);
            if (!(parameter instanceof String)) {
                continue;
            }

            sb.append("Content-Disposition: form-data; name=\"" + key +
                    "\"\r\n\r\n" + (String)parameter);
            sb.append("\r\n" + "--" + boundary + "\r\n");
        }

        return sb.toString();
    }
	
	private String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        try {
	        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
	        for (String line = r.readLine(); line != null; line = r.readLine()) {
	            sb.append(line);
	        }
	        in.close();
        } catch (Exception e) {
        	Logger.Error("Exception reading from network. " + e.getMessage() );
        }
        return sb.toString();
    }
	
	protected void signRequest(HttpURLConnection conn) {
		// override in derived classes
	}
	
	public static Bitmap downloadBitmap( String url ) {
		Logger.Debug("download bitmap from " + url );
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestProperty("User-Agent", System.getProperties().getProperty("http.agent") + " MBVCA");
			return BitmapFactory.decodeStream(conn.getInputStream());
		} catch (MalformedURLException e) {
			Logger.Error("MalformedURLException reading bitmap" + e.getMessage() );
		} catch (IOException e) {
			Logger.Error("IOException reading bitmap " + e.getMessage() );
		}
        
		return null;
	}
}
