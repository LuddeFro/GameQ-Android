package com.example.gameq_android;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.gameq_android.MainActivity.PlaceholderFragment;

public class ActivityMaster extends ActionBarActivity {


	
protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		
		
		
		/* possible initiation of DataHandlers:
		//init DataHandlers
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", "aow6ešmvb83amas2", true);
		
		dataSetter.putString("@string/str_email", "bajs");
		dataSetter.commit();
		String abc = dataGetter.getString("@string/str_email", null);
		System.out.println(abc);
		
		
		
		// Put (all puts are automatically committed)
		secureDataHandler.put("password", "User1234Fagaloosh");
		// Get
		String user = secureDataHandler.getString("userIdFaggot");
		System.out.println(user);
		*/
		
	}
	
	
	
	protected void showWebsite(View view) {
		//TODO
	}
	
	public void logout(View view) {
		//init DataHandlers
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", "aow6ešmvb83amas2", true);
				
		secureDataHandler.put("password", null);
		dataSetter.putString("@string/str_email", null);
		dataSetter.putBoolean("str_bolIsLoggedIn", false);
		dataSetter.putBoolean("str_bolIsRegisteredForNotifications", false);
		dataSetter.commit();
		//TODO unregister for push notifications
		//TODO post logout
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
		intent.putExtra("@string/str_email", email);
		intent.putExtra("@string/str_password", password);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		//superfluous addition to preventing back button bug ?!
		//finish();
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
}
