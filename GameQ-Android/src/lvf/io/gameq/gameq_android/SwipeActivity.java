package lvf.io.gameq.gameq_android;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.datatype.DatatypeConstants.Field;

import lvf.io.gameq.gameq_android.StatFrag.OnFragmentInteractionListener;
import lvf.io.gameq.gameq_android.dummy.DummyContent;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class SwipeActivity extends ActivityMaster {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	public DeviceListActivity lf;
	public StatusFragment sf;
	
	
	
	public Context context;
	static final String TAG = "GameQ-Android";
    private static final String SALT = "iuyavos32bdf83ika";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    protected static final String PROPERTY_APP_VERSION = "appVersion";
    
    public ConnectionHandler connectionsHandler;
    //Project Number from GameQ @ https://console.developers.google.com/project/
    //project GameQ by GameQ, only accessible for GameQ under "projects"
    String SENDER_ID = "647277380052";
	
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        java.lang.reflect.Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        // Ignore
	    }
		setContentView(R.layout.activity_swipe);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		Intent intent = getIntent();
    	String fromLogin = null;
    	if (intent != null) {
    		fromLogin = intent.getStringExtra(getResources().getString(R.string.str_fromLogin));
    		//String message = intent.getStringExtra("message");
    	}
		Log.i(TAG, "Main being shown ------------------------");
		Log.i(TAG, "Main being shown ----GUSTAFHEJEHEJEHJEHEJEHEJ----");
		if (fromLogin == null) {
			
			boolean bolIsLoggedIn = getBolIsLoggedIn();
			String email = getEmail();
			String password = getPassword();
			
			if (password == null || email == null || bolIsLoggedIn == false) { 
				//not valid login, nullify data and show login screen
				logout(null);
			} else { // creds valid?! attemptlogin with email + password
				//showLogin(email, password);
				//assume login
			}
		}
		AppRater.app_launched(this);

	}
	
	
	public void initiation() {
    	if (connectionsHandler == null) {
        	connectionsHandler = new ConnectionHandler(this);
    	}
        	
    	
    }
	public SwipeActivity() {
		this.initiation();
		
		
	}
	
	
	
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.swipe, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			if (position == 0) {
				sf = new StatusFragment();
				sf.setContext(getApplicationContext());
				return sf;
			} else if (position == 1) {
				lf = DeviceListActivity.newInstance(position + 1);
				return lf;
			} else {
				lf = DeviceListActivity.newInstance(position + 1);
				return lf;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//----------------------------TableFragmentSection-------------------------------------------------
	
	public static class DeviceListActivity extends Fragment implements
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
		private static final String ARG_PARAM1 = "param1";
		protected Dialog dialog;
		GoogleCloudMessaging gcm;
		String regid;
		//Project Number from GameQ @ https://console.developers.google.com/project/
		//project GameQ by GameQ, only accessible for GameQ under "projects"
		String SENDER_ID = "647277380052";

		
		public static DeviceListActivity newInstance(int i) {
			DeviceListActivity fragment = new DeviceListActivity();
			Bundle args = new Bundle();
			args.putInt(ARG_PARAM1, i);
			fragment.setArguments(args);
			fragment.initiation();
			return fragment;
		}
		
		public void initiation() {
			if (connectionsHandler == null) {
		    	connectionsHandler = new ConnectionHandler(this);
		    	
			}
		}
		
		public Activity fa;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			fa = super.getActivity();
			
			
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

			return inflater.inflate(R.layout.activity_device_list, container, false);
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
		    super.onResume();
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
	
	
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------StatusFragmentSection----------------------------------------------
	
	@SuppressLint("ValidFragment")
	public class StatusFragment extends Fragment {
		// TODO: Rename parameter arguments, choose names that match
		// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
		private static final String ARG_PARAM1 = "param1";
		private Timer t2;
		// TODO: Rename and change types of parameters
		private int mParam1;
		private String[] strings = new String[3];
		private OnFragmentInteractionListener mListener;
		
		Context statContext;
		
		private View view;
		private TextView gameTextView;
		private TextView statusTextView;
		private TextView approxTextView;
		private TextView timeTextView;
		private ImageView imageView;
		private int lastImage;
		private int lastAlpha;
		
		
		
		
		public void setContext(Context context) {
			statContext = context;
			
		}
		
		
		@Override
		public void onResume() {
			t2 = new Timer();

			t2.scheduleAtFixedRate(
			    new TimerTask()
			    {
			        public void run()
			        {
			        	updateDraw(ConnectionHandler.drawArray);
			        }
			    },
			    1000,      // run first occurrence after 1000 ms
			    1000);
			super.onResume();
		}
		
		public void updateDraw(String[] arrayString) {
			
			
			
			
			if (arrayString == null) {
				runOnUiThread(new Runnable() {
				     @Override
				     public void run() {

				    	
						AlphaAnimation anim = new AlphaAnimation(lastAlpha, 0);
						lastAlpha = 0;
						anim.setDuration(1000);
						anim.setStartOffset(0);
						anim.setFillAfter(true);
						gameTextView.startAnimation(anim);
								
								
						statusTextView.setText(getResources().getString(R.string.status_disconnected));
						approxTextView.setText("");
						timeTextView.setText("");
						statusTextView.setTextColor(Color.parseColor("#FFFFFF") );

				    }
				});
				
				return;
			} 
			
			/*if (arrayString.equals(strings)) {
				return;
			} else {
				strings = arrayString;
			}*/
			int j = -1;
			int a = -1;
			long b = 0;
			int status;
			long qTime;
			int game;
			if (arrayString.length >= 1 && arrayString[0].length() >=18) {
				for (int i = 0; i < arrayString.length; i++) {
					if (arrayString[i].equals(null) || arrayString[i].equals("null")) {
						break;
					}
					int tempStatus = Integer.parseInt(arrayString[i].substring(2, 4));
					long tempQTime = Long.parseLong(arrayString[i].substring(arrayString[i].length()-14));
					if (tempStatus > j ) {
						j = tempStatus;
						a = i;
						b = tempQTime;
					} else if (tempStatus == j && b < tempQTime) { 
						j = tempStatus;
						a = i;
						b = tempQTime;
					}
				}
				status = Integer.parseInt(arrayString[a].substring(2, 4));
				qTime = Long.parseLong(arrayString[a].substring(arrayString[a].length()-14));
				game = Integer.parseInt(arrayString[a].substring(0, 2));
			} else {
				status = 4;
				qTime = 0;
				game = 0;
			}
			DummyContent.game = game;
			DummyContent.status = status;
			DummyContent.qTime = qTime;
			
			runOnUiThread(new Runnable() {
			     @Override
			     public void run() {
			    	 int innerGame = DummyContent.game;
			    	 int innerStatus = DummyContent.status;
			    	 long innerqTime = DummyContent.qTime;
			    	 int newImage = innerGame;
			    	 switch (innerGame) {
						case 0: //none
							
							Log.i(TAG, "0");
							break;
						case 1: //HoN
							gameTextView.setText("Heroes of Newerth");
							
							Log.i(TAG, "1");
							break;
						
						case 2: //Dota
							gameTextView.setText("Dota 2");
							
							Log.i(TAG, "2");
							break;
						
						case 3: //CSGO
							gameTextView.setText("Counter Strike: Global Offensive");
							
							Log.i(TAG, "3");
							break;
						case 4:
							gameTextView.setText("Smite");
							break;
						case 5:
							gameTextView.setText("Strife");
							break;	
						case 6:
							gameTextView.setText("StarCraft II");
							break;	
						case 7:
							gameTextView.setText("Team Fortress 2");
							break;	
						case 8:
							gameTextView.setText("Heroes of the Storm");
							break;	
						case 9:
							gameTextView.setText("Hearthstone");
							break;	
						
						default: 
							gameTextView.setText("Unknown Game");
							imageView.setImageResource(R.drawable.transgq);
							Log.i(TAG, "default");
							break;
						
					}
					switch (innerStatus) {
						case 0: //offline
							statusTextView.setText(getResources().getString(R.string.status_offline));
							approxTextView.setText("");
							timeTextView.setText("");
							newImage = 0;
							
							Log.i(TAG, "offline");
							break;
						
						case 1:  //online
							statusTextView.setText(getResources().getString(R.string.status_online));
							approxTextView.setText("");
							timeTextView.setText("");
							Log.i(TAG, "online");
							break;
						
						case 2:  //ingame
							statusTextView.setText(getResources().getString(R.string.status_ingame));
							approxTextView.setText("");
							timeTextView.setText("");
							Log.i(TAG, "ingame");
							break;
							
						case 3:  //not tracking
							statusTextView.setText(getResources().getString(R.string.status_nottracking));
							approxTextView.setText("");
							timeTextView.setText("");
							newImage = 0;
							Log.i(TAG, "disced");
							break;
							
						case 4:  //disconnected
							statusTextView.setText(getResources().getString(R.string.status_disconnected));
							approxTextView.setText("");
							timeTextView.setText("");
							newImage = 0;
							Log.i(TAG, "disced");
							break;
						
						default: 
							statusTextView.setText("Unknown Status");
							approxTextView.setText("");
							timeTextView.setText("");
							newImage = 0;
							Log.i(TAG, "unknown");
							break;
						
					}
					
					
					if (innerStatus != 0 && innerStatus < 4 && lastAlpha == 0) {
						AlphaAnimation animation1 = new AlphaAnimation(lastAlpha, 1);
						animation1.setDuration(1000);
						animation1.setStartOffset(0);
						animation1.setFillAfter(true);
						gameTextView.startAnimation(animation1);
						lastAlpha = 1;
					} else if (lastAlpha == 1  && (innerStatus == 0 || innerStatus > 3)) {
						AlphaAnimation animation1 = new AlphaAnimation(lastAlpha, 0);
						animation1.setDuration(1000);
						animation1.setStartOffset(0);
						animation1.setFillAfter(true);
						gameTextView.startAnimation(animation1);
						lastAlpha = 0;
					}
					
					if (lastImage == newImage ) {
						return;
					} else {
						lastImage = newImage;
						DummyContent.imageNumber = newImage;
						AlphaAnimation animation2 = new AlphaAnimation(1, 0);
						animation2.setDuration(1000);
						animation2.setStartOffset(0);
						animation2.setFillAfter(true);
						imageView.startAnimation(animation2);
						
						Timer aTimer = new Timer();
						aTimer.schedule(new TimerTask()
						    {
						        public void run()
						        {
						        	
						        	runOnUiThread(new Runnable() {
									     @Override
									     public void run() {
									    	 switch (DummyContent.imageNumber) {
												case 0: //None
													imageView.setImageResource(R.drawable.transgq);
													break;
											
												case 1:  //Hon
													imageView.setImageResource(R.drawable.hon);
													break;
											
												case 2:  //Dota
													imageView.setImageResource(R.drawable.dota);
													break;
											
												case 3:  //CSGO
													imageView.setImageResource(R.drawable.csgo);
													break;
												case 4:  //smite
													imageView.setImageResource(R.drawable.smitelogo);
													break;
												case 5:  //strife
													imageView.setImageResource(R.drawable.strifelogo);
													break;
												case 6:  //sc2
													imageView.setImageResource(R.drawable.sc2logo);
													break;
												case 7:  //tf2
													imageView.setImageResource(R.drawable.tf2logo);
													break;
												case 8:  //hots
													imageView.setImageResource(R.drawable.hotslogo);
													break;
												case 9:  //lol
													imageView.setImageResource(R.drawable.lollogo);
													break;
													
													
												
												default: 
													imageView.setImageResource(R.drawable.transgq);
													break;
											
								        	}
								        	AlphaAnimation animation3 = new AlphaAnimation(0, 1);
											animation3.setDuration(1000);
											animation3.setStartOffset(0);
											animation3.setFillAfter(true);
											imageView.startAnimation(animation3);
									     }
						        	});
						        	
						        }
						    }, 1000);
						
					}
					
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
					dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
					Date date = new Date();
					long nowTime = Long.parseLong(dateFormat.format(date)); //20140806155948
					if (nowTime - innerqTime <= 17 && innerGame == 3 /*CSGO*/) {
						approxTextView.setText(getResources().getString(R.string.approx_time));
						timeTextView.setText("" + (17 - (nowTime - innerqTime)));
					} else if (nowTime - innerqTime <= 45 && innerGame == 2 /*Dota2*/) {
						approxTextView.setText(getResources().getString(R.string.approx_time));
						timeTextView.setText("" + (45 - (nowTime - innerqTime)));
					}	else {
						approxTextView.setText("");
						timeTextView.setText("");
					} 

			    }
			});
			
		}
		
		@Override
		public void onPause() {
			t2.cancel();
			super.onPause();
		}
		
		
		public StatusFragment() {
			// Required empty public constructor
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			if (getArguments() != null) {
				mParam1 = getArguments().getInt(ARG_PARAM1);
			}
		}

		
		
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Inflate the layout for this fragment
			view = (View) LayoutInflater.from(statContext).inflate(R.layout.fragment_status, null);
			gameTextView = (TextView) view.findViewById(R.id.gameTextView1 );
			statusTextView = (TextView) view.findViewById(R.id.statusTextView1);
			approxTextView = (TextView) view.findViewById(R.id.approxTextView1);
			timeTextView = (TextView) view.findViewById(R.id.timeTextView1);
			imageView = (ImageView) view.findViewById(R.id.imageView1);
			return view;
			
			
			//return inflater.inflate(R.layout.fragment_status , container, false);
		}

		// TODO: Rename method, update argument and hook method into UI event
		public void onButtonPressed(Uri uri) {
			if (mListener != null) {
				mListener.onFragmentInteraction(uri);
			}
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);/*
			try {
				mListener = (OnFragmentInteractionListener) activity;
			} catch (ClassCastException e) {
				throw new ClassCastException(activity.toString()
						+ " must implement OnFragmentInteractionListener");
			}*/
		}

		@Override
		public void onDetach() {
			super.onDetach();
			mListener = null;
		}

		/**
		 * This interface must be implemented by activities that contain this
		 * fragment to allow an interaction in this fragment to be communicated to
		 * the activity and potentially other fragments contained in that activity.
		 * <p>
		 * See the Android Training lesson <a href=
		 * "http://developer.android.com/training/basics/fragments/communicating.html"
		 * >Communicating with Other Fragments</a> for more information.
		 */ /*
		public interface OnFragmentInteractionListener {
			// TODO: Update argument type and name
			public void onFragmentInteraction(Uri uri);
		}*/

	}
	
	
	
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	//-------------------------------------------------------------------------------------------
	

}
