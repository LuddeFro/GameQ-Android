package com.example.gameq_android;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;

public class ConnectionHandler {
	private boolean disconnected;
	private final String USER_AGENT = "GQAndroid/1.0";
	private ActivityMaster parentActivity;
	
	
	/**
	 * constructor
	 */
	public ConnectionHandler(ActivityMaster parentAct) {
		parentActivity = parentAct;
		disconnected = false;
		
		
	}
	
	public void postLogout() {
		String token = parentActivity.getToken();
		if (token == null) {
			return;
		}
		String urlParameters = "device=android&token=" + token;
		String urlPath = "@string/str_LogoutURL";
		post(urlParameters, urlPath);
	}	
	
	public void postLogin(String email, String password) {
		if (email == null || password == null) {
			return;
		}
		String urlParameters = "email=" + email + "&losenord=" + password;
		String urlPath = "@string/str_LoginURL";
		post(urlParameters, urlPath);
	}
	
	public void postUpdateDraw(String email) {
		if (email == null) {
			return;
		}
		String urlParameters = "email=" + email;
		String urlPath = "@string/str_UpdateDrawURL";
		post(urlParameters, urlPath);
	}
	
	public void postToken(String token, String email) {
		String deviceName = android.os.Build.MODEL;
		if (deviceName == null || email == null || token == null) {
			return;
		}
		String urlParameters = "token=" + token + "&email=" + email + "&deviceName=" + deviceName;
		String urlPath = "@string/str_UpdateTokenURL";
		post(urlParameters, urlPath);
	}
	
	public void postRegister(String email, String firstname, String lastname, int gender, int yob, String country, String losenord, String secretq, String secret) {
		String urlParameters = "email=" + email + "&firstname=" + firstname + "&lastname=" + lastname + "&gender=" + gender + "&yob=" + yob + "&country=" + country + "&losenord=" + losenord + "&secretq=" + secretq + "&secret=" + secret;
		String urlPath = "@string/str_RegisterURL";
		post(urlParameters, urlPath);
	}
	
	private void post(String urlParameters, String urlPath) {

		String url = "@string/str_URL" + urlPath;
		URL obj = null;
		String returnString = null;
		try {
			obj = new URL("http://www.gameq.com");
			
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			
			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 
			
	 
			// Send post request
			// returns return string
			returnString = sendPost(con, urlParameters, url);
			
			
			
		} catch (MalformedURLException e) {
			System.out.println("URL Error (MalformedURLException) for " + url );
		} catch (IOException e) {
			System.out.println("URL Error (IOException) for " + url );
		}
		
		if (returnString == null) {
			System.out.println("URL Error (nullResponse) for " + url );
		} else {
			handleResponse(returnString);
		}
	}
	
	
	private String sendPost(HttpsURLConnection con, String urlParameters, String url) {
		StringBuffer response = new StringBuffer();
		
		try {
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
	 
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			
		} catch (MalformedURLException e) {
			System.out.println("URL Error (MalformedURLException) for url:" + url);
		} catch (IOException e) {
			System.out.println("URL Error (IOException) for url:" + url);
		}
		
		return response.toString();
	}
	
	private void handleResponse(String response) {
		System.out.println("response: " + response);
		
		if (response.length() >= 8) {
			if (response.substring(0, 8).equals("updating")) {
	            response = response.substring(8);
	            int items = Integer.parseInt(response.substring(0, 2));
	            if (items == 0) {
	                //TODO update DeviceList with null array
	                return;
	            }
	            
	            response = response.substring(2);
	            String [] deviceArray = new String[items];
	            for (int i = 0; i<items; i++) {
	            	int len = Integer.parseInt(response.substring(0,  2));
	                response = response.substring(2);
	                String itemString = response.substring(0,  len+2);
	                System.out.println(itemString);
	                deviceArray[1] = itemString;
	                
	                if (i == (items-1)) {
	                    response = null;
	                }
	                else {
	                    response = response.substring(len + 2);
	                }
	            }
	            //TODO send deviceArray to DeviceList
	            return;
	        }
	    }  
		
		if (response.equals("postedDevice")) {
	        return;
	    }
		
		// if sign in was successful
	    if (response.equals("sign in success"))
	    {
	    	parentActivity.setConnected();
	        return;
	    }
	    
	 // if sign in failed
	    if (response.equals("sign in failed"))
	    {
	        alert("You entered an invalid email/password combo");
	        return;
	    }
	    
	  //if you just logged out
	    if (response.equals("logged out"))
	    {
	        if (!disconnected)
	        {
	            //if you logged out manually
	            parentActivity.setDisconnected();
	            return;
	        }
	        else {
	            //if you got "badsession" or "no" (an alert has already been sent)
	            disconnected = false;
	        }
	        return;
	    }
	    
	    if (response.equals("signing up"))
	    {
	        alert("Welcome to GameQ, you should be able to log in immediatley with the password and username you provided");
	        return;
	    }
	    if (response.equals("duplicate"))
	    {
	    	alert("An account with that e-mail address already exists");
	    	return;
	    }
	    if (response.equals("badsession"))
	    {
	    	//session was broken
	        disconnected = true;
	        parentActivity.logout(null);
	        parentActivity.setDisconnected();
	       
	        alert("You were disconnected from the server, check your connection and try reconnecting!");
	        
	        return;
	    }
	    if (response.equals("no"))
	    {
	        //should be unreachable, disconnect the bastard!
	        disconnected = true;
	        parentActivity.logout(null);
	        parentActivity.setDisconnected();
	        
	        
	        // mobile alert
	        alert("Connection error, try again in a minute!");
	        
	    }
		
	}
	private void alert(String alert) {
		//TODO show an alert message 
	}
}
