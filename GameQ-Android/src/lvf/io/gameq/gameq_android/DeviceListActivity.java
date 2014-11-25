package lvf.io.gameq.gameq_android;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * An activity representing a list of Devices. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link DeviceDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link DeviceListFragment} and the item details (if present) is a
 * {@link DeviceDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link DeviceListFragment.Callbacks} interface to listen for item selections.
 */
public class DeviceListActivity extends Fragment implements
		DeviceListFragment.Callbacks {
	public ConnectionHandler connectionsHandler;
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	Context context;
	static final String TAG = "GameQ-Android";
    private static final String SALT = "iuyavos32bdf83ika";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    protected static final String PROPERTY_APP_VERSION = "appVersion";
    private final Fragment thisActivity = this;
    protected Dialog dialog;
    GoogleCloudMessaging gcm;
    String regid;
    //Project Number from GameQ @ https://console.developers.google.com/project/
    //project GameQ by GameQ, only accessible for GameQ under "projects"
    String SENDER_ID = "773141536608";
	
    public void initiation() {
    	if (connectionsHandler == null) {
        	connectionsHandler = new ConnectionHandler(this);
        	
    	}
    }
    private LinearLayout ll;
    public Activity fa;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	fa = super.getActivity();
    	ll = (LinearLayout) inflater.inflate(R.layout.activity_device_list, container, false);
    	
    	context = fa.getApplicationContext();
    	
		initiation();
		
		/*TODO next version
		if (findViewById(R.id.device_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;
			
			
			*//* not in this app vvv
			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((DeviceListFragment) getSupportFragmentManager().findFragmentById(
					R.id.device_list)).setActivateOnItemClick(true);
					*//*
		}*/ mTwoPane = false;

    	return ll;
    }
    
	
	

	/**
	 * Callback method from {@link DeviceListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {/*TODO next version
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(DeviceDetailFragment.ARG_ITEM_ID, id);
			DeviceDetailFragment fragment = new DeviceDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.device_detail_container, fragment).commit();*/

		} else {
			//In single-pane mode, do nothing
			/*
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, DeviceDetailActivity.class);
			detailIntent.putExtra(DeviceDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);*/
		}
	}
	
	
	public String getToken() {
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
		String token = secureDataHandler.getString("@string/str_token");
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String token = dataGetter.getString("@string/str_token", null);*/
		Log.i(TAG, "got Token: " + token);
		return token;
	}
	public String getPassword() {
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
		String password = secureDataHandler.getString("@string/str_password");
		Log.i(TAG, "got password: " + password);
		return password;
	}
	public String getEmail() {
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
		String email = secureDataHandler.getString("@string/str_email");
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String email = dataGetter.getString("@string/str_email", null);*/
		Log.i(TAG, "got email: " + email);
		return email;
	}
	public boolean getBolIsLoggedIn() {
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
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
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
		String strbol = secureDataHandler.getString("@string/str_bolIsRegisteredForNotifications");
		boolean bol;
		if (strbol == null) {
			setBolIsRegisteredForNotifications(true);
			return true;
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
		
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_token", token);
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_token", token);
		dataSetter.commit();*/
		Log.i(TAG, "set Token: "+ token);
		
	}
	public void setPassword(String password) {
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_password", password);
		Log.i(TAG, "set password: " + password);
	}
	public void setEmail(String email) {
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_email", email);
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_email", email);
		dataSetter.commit();*/
		Log.i(TAG, "set email: "+ email);
	}
	public void setBolIsLoggedIn(boolean bol) {
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
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
		SecurePreferences secureDataHandler = new SecurePreferences(fa.getBaseContext(), "securePrefs", SALT, true);
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
	
	
	protected void showLogin(String email, String password) {
		Intent intent = new Intent(fa, LoginActivity.class);
		//make sure back button cannot take user back to being logged in
		intent.putExtra(getResources().getString(R.string.str_email), email);
		intent.putExtra(getResources().getString(R.string.str_password), password);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		//superfluous addition to preventing back button bug ?!
		//finish();
	}
	
	
	@Override
	public void onResume() {
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
	    	gcm = GoogleCloudMessaging.getInstance(fa);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
	    }
	}
	

	@Override
	public void onStop() {
		if (dialog != null)
			dialog.dismiss();
		super.onStop();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	protected void alert(String message)
	{
		new AlertDialog.Builder(fa)
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
		Intent intent = new Intent(fa, DeviceListActivity.class);
		startActivity(intent);
	}
	
	public void showSettings(View view) {
		Intent intent = new Intent(fa, SettingsActivity.class);
		startActivity(intent);
	}
	
	public void showAbout(View view) {
		Intent intent = new Intent(fa, AboutActivity.class);
		startActivity(intent);
	}
	

	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	protected boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(fa);
	    Log.i(TAG, "checkGooglePlayServicesAvailable, connectionStatusCode="
	    	    + resultCode);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	        	showGooglePlayServicesAvailabilityErrorDialog(resultCode);
	            //GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    //PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            fa.finish();
	        }
	        return false;
	    }
	    return true;
	}
	private void showGooglePlayServicesAvailabilityErrorDialog(
		    final int connectionStatusCode) {
			fa.runOnUiThread(new Runnable() {
		    public void run() {
		    dialog = GooglePlayServicesUtil.getErrorDialog(
		        connectionStatusCode, fa,
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
		SharedPreferences dataGetter = fa.getPreferences(Context.MODE_PRIVATE);
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
	                
	                SharedPreferences dataGetter = fa.getPreferences(Context.MODE_PRIVATE);
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
