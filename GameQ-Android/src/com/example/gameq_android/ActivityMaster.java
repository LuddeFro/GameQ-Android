package com.example.gameq_android;


import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class ActivityMaster extends ActionBarActivity {
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    protected static final String PROPERTY_APP_VERSION = "appVersion";
    private final Activity thisActivity = this;
    protected Dialog dialog;
    private static final String SALT = "iuyavos32bdf83ika";
    Context context;
    GoogleCloudMessaging gcm;
    String regid;
    //Project Number from GameQ @ https://console.developers.google.com/project/
    //project GameQ by GameQ, only accessible for GameQ under "projects"
    String SENDER_ID = "647277380052";
    
    /**
     * Tag used on log messages.
     */
    static final String TAG = "GameQ-Android";

	public ConnectionHandler connectionsHandler;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		
		connectionsHandler = new ConnectionHandler(this);
		
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    //if (!checkPlayServices()) {
	    	//Log.i(TAG, "@string/out_of_date_Google_Services_APK");
	    	//alert("@string/out_of_date_Google_Services_APK");
	    	
	    //}
	    if (!checkPlayServices()) {
			Log.i(TAG, "!checkPlayServices() returns true in onCreate() Activity Master");
	    	//Log.i(TAG, "@string/out_of_date_Google_Services_APK");
	    	//alert("@string/out_of_date_Google_Services_APK");
	    } else {
	    	gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
	    }
	}
	
	@Override
	protected void onStop() {
		if (dialog != null)
			dialog.dismiss();
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	protected void alert(String message)
	{
		new AlertDialog.Builder(this)
	    .setTitle("GameQ - Alert")
	    .setMessage(message)
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
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
	
	
	
	
	
	protected void showWebsite(View view) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.str_website)));
		startActivity(browserIntent);
	}
	
	public void logout(View view) {
		
		
		
		
		new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            	connectionsHandler.postLogout();
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	            
	        }
	    }.execute(null, null, null);
	    
	    
		
		setPassword(null);
		setEmail(null);
		setBolIsLoggedIn(false);
		setBolIsRegisteredForNotifications(false);
		setToken(null);
		
		//TODO unregister for push notifications
		showLogin(null, null);
	}
	
	public void showDevices(View view) {
		Intent intent = new Intent(this, DeviceListActivity.class);
		startActivity(intent);
	}
	
	public void showSettings(View view) {
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void showAbout(View view) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
	/**
	 * set password + username to null if you do not wish to attempt a login 
	 * immediately when the view is presented with a preset username/pass.
	 * @param password
	 * @param username
	 */
	protected void showLogin(String email, String password) {
		Intent intent = new Intent(this, LoginActivity.class);
		//make sure back button cannot take user back to being logged in
		intent.putExtra(getResources().getString(R.string.str_email), email);
		intent.putExtra(getResources().getString(R.string.str_password), password);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		//superfluous addition to preventing back button bug ?!
		//finish();
	}
	
	
	
	
	
	
	public String getToken() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String token = secureDataHandler.getString("@string/str_token");
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String token = dataGetter.getString("@string/str_token", null);*/
		Log.i(TAG, "got Token: " + token);
		return token;
	}
	public String getPassword() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String password = secureDataHandler.getString("@string/str_password");
		Log.i(TAG, "got password: " + password);
		return password;
	}
	public String getEmail() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String email = secureDataHandler.getString("@string/str_email");
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String email = dataGetter.getString("@string/str_email", null);*/
		Log.i(TAG, "got email: " + email);
		return email;
	}
	public boolean getBolIsLoggedIn() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String strbol = secureDataHandler.getString("@string/str_bolIsLoggedIn");
		if (strbol == null) {
			setBolIsLoggedIn(false);
			return false;
		}
		boolean bol;
		if (strbol.equals("0")) {
			bol = false;
		} else {
			bol = true;
		}/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		boolean bol = dataGetter.getBoolean("@string/str_bolIsLoggedIn", false);*/
		Log.i(TAG, "gotbolLoggedin: " + bol);
		return bol;
	}
	public boolean getBolIsRegisteredForNotifications() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String strbol = secureDataHandler.getString("@string/str_bolIsRegisteredForNotifications");
		boolean bol;
		if (strbol == null) {
			setBolIsRegisteredForNotifications(false);
			return false;
		}
			
		if (strbol.equals("0")) {
			bol = false;
		} else {
			bol = true;
		}/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		boolean bol = dataGetter.getBoolean("@string/str_bolIsRegisteredForNotifications", false);*/
		Log.i(TAG, "got bolRegistered: " + bol);
		return bol;
	}
	public void setToken(String token) {
		
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_token", token);
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_token", token);
		dataSetter.commit();*/
		Log.i(TAG, "set Token: "+ token);
		
	}
	public void setPassword(String password) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_password", password);
		Log.i(TAG, "set password: " + password);
	}
	public void setEmail(String email) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_email", email);
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_email", email);
		dataSetter.commit();*/
		Log.i(TAG, "set email: "+ email);
	}
	public void setBolIsLoggedIn(boolean bol) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		if (bol) {
			secureDataHandler.put("@string/str_bolIsLoggedIn", "1");
		} else {
			secureDataHandler.put("@string/str_bolIsLoggedIn", "0");
		}
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putBoolean("str_bolIsLoggedIn", bol);
		dataSetter.commit();*/
		Log.i(TAG, "set bolLoggedIn: "+ bol);
	}
	public void setBolIsRegisteredForNotifications(boolean bol) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		if (bol) {
			secureDataHandler.put("@string/str_bolIsRegisteredForNotifications", "1");
		} else {
			secureDataHandler.put("@string/str_bolIsRegisteredForNotifications", "0");
		} /*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putBoolean("str_bolIsRegisteredForNotifications", bol);
		dataSetter.commit();*/
		Log.i(TAG, "set bolRegistered: " + bol);
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	protected boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    Log.i(TAG, "checkGooglePlayServicesAvailable, connectionStatusCode="
	    	    + resultCode);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	        	showGooglePlayServicesAvailabilityErrorDialog(resultCode);
	            //GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    //PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	private void showGooglePlayServicesAvailabilityErrorDialog(
		    final int connectionStatusCode) {
			this.runOnUiThread(new Runnable() {
		    public void run() {
		    dialog = GooglePlayServicesUtil.getErrorDialog(
		        connectionStatusCode, thisActivity,
		        0);
		        if (dialog == null) {
		                Log.e(TAG,
		                        "couldn't get GooglePlayServicesUtil.getErrorDialog");
		                Toast.makeText(context,
		                        "incompatible version of Google Play Services",
		                        Toast.LENGTH_LONG).show();
		            }
		            dialog.show();
		       }
		    });
		}
	
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            showSettings(null);
	            return true;
	        case R.id.action_about:
	            showAbout(null);
	            return true;
	        case R.id.action_devices:
	            showDevices(null);
	            return true;
	        case R.id.action_logout:
	            logout(null);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	protected String getRegistrationId(Context context) {
	    String token = getToken();
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
	    if (token == null || token.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = dataGetter.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return token;
	}
	
	protected static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	protected void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;
	                
	                connectionsHandler.postToken(regid, getEmail());
	                setToken(regid);
	                
	                SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
	        		SharedPreferences.Editor dataSetter = dataGetter.edit();
	        		dataSetter.putInt(PROPERTY_APP_VERSION, getAppVersion(context));
	        		dataSetter.commit();
	                
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	            
	        }
	    }.execute(null, null, null);
	}
	
	
	
	
}
