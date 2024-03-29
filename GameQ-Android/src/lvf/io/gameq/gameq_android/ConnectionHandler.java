package lvf.io.gameq.gameq_android;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EmptyStackException;

import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

public class ConnectionHandler {
	private boolean disconnected;
	private final String USER_AGENT = "GQAndroid/1.0";
	private SwipeActivity parentActivity;
	private ActivityMaster masterActivity;
	
	private static final String str_LogoutURL = "logging.php";
	private static final String str_LoginURL = "signing.php";
	private static final String str_UpdateDrawURL = "updateDraw.php";
	private static final String str_UpdateTokenURL = "upAndroidToken.php";
	private static final String str_RegisterURL = "regging.php";
	private static final String str_VersionURL = "versionControl.php";
	private static final String str_getMyGamesURL = "getMyGames.php";
	private static final String str_upMyGamesURL = "upMyGames.php";
	private static final String str_URL = "http://54.76.41.235/GameQ_Server_Code/";
	private static final String kVERSION = "1.0";
	private boolean master;
	
	private static final String alt0 = "0";
	private static final String alt1 = "1";
	private static final String alt2 = "2";
	private static final String altx = "error";
	public static String[] drawArray = null;
	
	
	
	
	
	static final String TAG = "GameQ-Android";
	
	/**
	 * constructor
	 */
	public ConnectionHandler(DeviceListActivity parentAct) {
		parentActivity = (SwipeActivity) parentAct.fa ;
		disconnected = false;
		master = false;
		
	}
	/**
	 * constructor
	 */
	public ConnectionHandler(SwipeActivity parentAct) {
		parentActivity = parentAct;
		disconnected = false;
		master = false;
		
	}
	
	/**
	 * constructor
	 */
	public ConnectionHandler(SwipeActivity.DeviceListActivity parentAct) {
		parentActivity = (SwipeActivity) parentAct.fa;
		disconnected = false;
		master = false;
		
	}
	
	/**
	 * constructor
	 */
	public ConnectionHandler(ActivityMaster parentAct) {
		masterActivity = parentAct;
		disconnected = false;
		master = true;
		
	}
	/**
	 * constructor for deviceListFragment
	 *//*
	public ConnectionHandler() {
		disconnected = false;
	}*/
	
	public void postLogout() {
		String token;
		if (master) {
			token = masterActivity.getToken();
		} else { 
			token = parentActivity.getToken();
		}
		
		if (token == null) {
			return;
		}
		String urlParameters = "device=android&token=" + token;
		String urlPath = str_LogoutURL;
		post(urlParameters, urlPath);
	}
	public String postVersion() {
		String urlParameters = "device=android";
		String urlPath = str_VersionURL;
		return post(urlParameters, urlPath);
	}
	
	
	public String postLogin(String email, String password) {
		if (email == null || password == null) {
			return alt0;
		}
		String losenord = Encryptor.hashSHA256(password);
		String urlParameters = "email=" + email + "&losenord=" + losenord;
		String urlPath = str_LoginURL;
		return post(urlParameters, urlPath);
	}
	
	public String postGetMyGames(String email) {
		if (email == null) {
			return alt0;
		}
		
		String urlParameters = "email=" + email;
		String urlPath = str_getMyGamesURL;
		return post(urlParameters, urlPath);
	}
	
	public String postUpMyGames(String email, String newGames) {
		if (email == null || newGames == null) {
			return alt0;
		}
		String vCode = "";
		if (master) {
			vCode = ActivityMaster.lastGames[1] + masterActivity.getEmail().toLowerCase() + ActivityMaster.lastGames[0];
		} else { 
			vCode = ActivityMaster.lastGames[1] + parentActivity.getEmail().toLowerCase() + ActivityMaster.lastGames[0];
		}
		vCode = Encryptor.hashSHA256(vCode);
		String urlParameters = "email=" + email + "&games=" + newGames + "&vCode=" + vCode;
		String urlPath = str_upMyGamesURL;
		return post(urlParameters, urlPath);
	}
	
	/**
	 * 
	 * @param email
	 * a return val of null == error,
	 * a return val @string/alt0 is an empty list
	 * 
	 */
	
	
	public String postUpdateDraw(String email) {
		if (email == null) {
			return alt0;
		}
		String urlParameters = "email=" + email;
		String urlPath = str_UpdateDrawURL;
		String returnString = post(urlParameters, urlPath);
		if (returnString == null || returnString.equals(alt0)) {
			return null;
		}
		String[] tempArray = returnString.split(":");
		String[] tempArray2;
		if (tempArray[(tempArray.length - 1)].equals("null")) {
			tempArray2 = new String[tempArray.length - 1];
			for (int i = 0; i < tempArray2.length; i++) {
				tempArray2[i] = tempArray[i];
			}
			ConnectionHandler.drawArray = tempArray;
		} else {
			ConnectionHandler.drawArray = tempArray;
		}
		
		return returnString;
	}
	
	public void postToken(String token, String email) {
		String deviceName = android.os.Build.MODEL;
		if (deviceName == null || email == null || token == null) {
			return;
		}
		String urlParameters = "token=" + token + "&email=" + email + "&deviceName=" + deviceName;
		String urlPath = str_UpdateTokenURL;
		post(urlParameters, urlPath);
	}
	
	public String postRegister(String email, String firstname, String lastname, int gender, int yob, String country, String losenord, String secretq, String secret) {
		losenord = Encryptor.hashSHA256(losenord);
		secret = Encryptor.hashSHA256(secret);
		String urlParameters = "email=" + email + "&firstname=" + firstname + "&lastname=" + lastname + "&gender=" + gender + "&yob=" + yob + "&country=" + country + "&losenord=" + losenord + "&secretq=" + secretq + "&secret=" + secret;
		String urlPath = str_RegisterURL;
		return post(urlParameters, urlPath);
	}
	
	private String post(String urlParameters, String urlPath) {
		

		String url = str_URL + urlPath;
		Log.i(TAG, "sending: " + urlParameters + ", to: " + url);
		URL obj = null;
		String returnString = null;
		
		
		try {
			
			
			//obj = new URL("http://www.gameq.com");
			obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
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
			return null;
		} else {
			return handleResponse(returnString);
		}
	}
	
	
	private String sendPost(HttpURLConnection con, String urlParameters, String url) {
		StringBuffer response = new StringBuffer();
		
		try {
			
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			byte[] buf = urlParameters.getBytes("UTF-8");
			wr.write(buf, 0, buf.length);
			//wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
	 
			int responseCode = con.getResponseCode();
			Log.i(TAG, "\nSending 'POST' request to URL : " + url);
			Log.i(TAG, "Post parameters : " + urlParameters);
			Log.i(TAG, "Response Code : " + responseCode);
	 
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
	 
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			
			
		} catch (MalformedURLException e) {
			Log.i(TAG, "URL Error (MalformedURLException) for url:" + url);
		} catch (IOException e) {
			Log.i(TAG, "URL Error (IOException) for url:" + url);
		}
		
		return response.toString();
	}
	
	private String handleResponse(String response) {
		Log.i(TAG, "response: " + response);
		
		if (response.length() >= 8) {
			if (response.substring(0, 8).equals("updating")) {
	            response = response.substring(8);
	            int items = Integer.parseInt(response.substring(0, 2));
	            if (items == 0) {
	                return alt0;
	            }
	            
	            response = response.substring(2);
	            String[] deviceArray = new String[items];
	            for (int i = 0; i<items; i++) {
	            	
	            		
	            		int len = Integer.parseInt(response.substring(0,  2));
		                response = response.substring(2);
		                String itemString = response.substring(0,  len+4+14);
		                System.out.println(itemString);
		                deviceArray[i] = itemString;
		                
		                if (i == (items-1)) {
		                    break;
		                } else {
		                    response = response.substring(len + 4 + 14);
		                }
	            	
	            }
	            String backToString = deviceArray[0];
	            for (int i = 1; i<items; i++) {
	            	backToString = backToString + ":" + deviceArray[i];
	            }
	            return backToString;
	        }
	    }  
		if (response.length() >= 7) {
			if (response.substring(0, 7).equals("version")) {
	            response = response.substring(7);
	            if (response.equals(kVERSION)) {
	            	return alt1;
	            } else {
	            	return alt0;
	            }
	        }
	    }  
		
		if (response.length() >= 7) {
			if (response.substring(0, 7).equals("mygames")) {
				response = response.substring(7);
				String[] subStrings = response.split(";");
				ActivityMaster.lastGames = subStrings;
				if (subStrings[0].equals("0")) {
					
					if (master) {
						masterActivity.runOnUiThread(new Runnable() {
						    public void run() {
						    	((ActivityMaster) masterActivity).showStore(null, true);
						    }
						});
					} else { 
						parentActivity.runOnUiThread(new Runnable() {
						    public void run() {
						    	((ActivityMaster) parentActivity).showStore(null, true);
						    }
						});
					}
					
					
					
					
				} else {
					ActivityMaster.menuEnabled = true;
					if (parentActivity != null) {
						parentActivity.invalidateOptionsMenu();
						((ActivityMaster) parentActivity).updateStore();
					} else if (masterActivity != null ){
						masterActivity.invalidateOptionsMenu();
						masterActivity.updateStore();
					}
					
				}
				
			}
		}
		
		if(response.equals("gamesUp")) {
			if (ActivityMaster.isPurchasing) {
				if (parentActivity != null) {
					parentActivity.connectionsHandler.postGetMyGames(parentActivity.getEmail());
				} else if (masterActivity != null ){
					masterActivity.connectionsHandler.postGetMyGames(parentActivity.getEmail());
				}
				
			} else {
				if (parentActivity != null) {
					parentActivity.hideStore();
					ActivityMaster.menuEnabled = true;
					parentActivity.invalidateOptionsMenu();
				} else if (masterActivity != null ){
					masterActivity.hideStore();
					ActivityMaster.menuEnabled = true;
					masterActivity.invalidateOptionsMenu();
				}
				
			}
		}
	    if(response.equals("upGameFail")) {
	    	return alt0;
	    }
	    if(response.equals("piracy")) {
	    	this.alert("Something went wrong, contact GameQ support at: support@gameq.io");
	    }
	    
	    
	    
	    
		
		
		
		if (response.equals("postedDevice")) {
	        return alt1;
	    }
		
		// if sign in was successful
	    if (response.equals("sign in success"))
	    {
	    	//parentActivity.setConnected();
	        return alt1;
	    }
	    
	 // if sign in failed
	    if (response.equals("sign in failed"))
	    {
	        //alert("You entered an invalid email/password combo");
	        return alt0;
	    }
	    
	  //if you just logged out
	    if (response.equals("logged out"))
	    {
	        if (!disconnected)
	        {
	            //if you logged out manually
	            //parentActivity.setDisconnected();
	            return alt1;
	        }
	        else {
	            //if you got "badsession" or "no" (an alert has already been sent)
	            disconnected = false;
	        }
	        return alt0;
	    }
	    // registration successful
	    if (response.equals("signing up"))
	    {
	        return alt1;
	    }
	    
	 	    if (response.equals("signing upmailerr"))
	    {
	        return alt1;
	    }
	 	    
	 	   
	 	    
	 	    if (response.equals("mailerr"))
	    {
	        return altx;
	    }
	    // user already exists, registration failed
	    if (response.equals("duplicate"))
	    {
	    	alert("An account with that e-mail address already exists");
	    	return alt0;
	    }
	    // session was broken / corrupted
	    if (response.equals("badsession"))
	    {
	    	//session was broken
	        disconnected = true;
	        if (master) {
				masterActivity.logout(null);
			} else { 
		        parentActivity.logout(null);
			}
	        //parentActivity.setDisconnected();
	       
	        alert("You were disconnected from the server, check your connection and try reconnecting!");
	        
	        return altx;
	    }
	    // server reached unreachable code
	    if (response.equals("no"))
	    {
	        //should be unreachable, disconnect the bastard!
	        disconnected = true;
	        if (master) {
				masterActivity.logout(null);
			} else { 
		        parentActivity.logout(null);
			}
	        //parentActivity.setDisconnected();
	        
	        
	        // mobile alert
	        alert("Connection error, try again in a minute!");
	        return altx;
	        
	    }
		return null;
	}
	private void alert(final String message) {
		if (master) {
			masterActivity.runOnUiThread(new Runnable()
			{
				public void run() 
				{
					
					
					AlertDialog.Builder dialog = new AlertDialog.Builder(masterActivity);
					dialog
				    .setTitle("GameQ - Alert")
				    .setMessage(message)
				    .setCancelable(false)
				    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            dialog.cancel();
				        }
				     })/*
				    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            // do nothing
				        }
				     })*/
				    .setIcon(android.R.drawable.ic_dialog_alert)
				     .show();
					
					
					
					
				}
			});
		} else { 
			parentActivity.runOnUiThread(new Runnable()
			{
				public void run() 
				{
					
					
					AlertDialog.Builder dialog = new AlertDialog.Builder(parentActivity);
					dialog
				    .setTitle("GameQ - Alert")
				    .setMessage(message)
				    .setCancelable(false)
				    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            dialog.cancel();
				        }
				     })/*
				    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            // do nothing
				        }
				     })*/
				    .setIcon(android.R.drawable.ic_dialog_alert)
				     .show();
					
					
					
					
				}
			});
		}
		
		
	}
}
