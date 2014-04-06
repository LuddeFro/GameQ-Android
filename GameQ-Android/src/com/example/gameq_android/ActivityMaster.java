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

	public ConnectionHandler connectionsHandler;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		connectionsHandler = new ConnectionHandler(this);
		
	}
	
	
	
	protected void showWebsite(View view) {
		//TODO
	}
	
	public void logout(View view) {
		connectionsHandler.postLogout();
		
		setPassword(null);
		setEmail(null);
		setBolIsLoggedIn(false);
		setBolIsRegisteredForNotifications(false);
		
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
		intent.putExtra("@string/str_email", email);
		intent.putExtra("@string/str_password", password);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		//superfluous addition to preventing back button bug ?!
		//finish();
	}
	
	
	public void setConnected() {
		//TODO
		//will only ever be called in LoginActivity but requires definition here
	}
	public void setDisconnected() {
		//TODO
	}
	
	
	
	public String getToken() {
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String token = dataGetter.getString("@string/str_token", null);
		return token;
	}
	public String getPassword() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", "@string/salt", true);
		String password = secureDataHandler.getString("@string/str_password");
		return password;
	}
	public String getEmail() {
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String email = dataGetter.getString("@string/str_email", null);
		return email;
	}
	public boolean getBolIsLoggedIn() {
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		boolean bol = dataGetter.getBoolean("@string/str_bolIsLoggedIn", false);
		return bol;
	}
	public boolean getBolIsRegisteredForNotifications() {
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		boolean bol = dataGetter.getBoolean("@string/str_bolIsRegisteredForNotifications", false);
		return bol;
	}
	public void setToken(String token) {
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_token", null);
		dataSetter.commit();
	}
	public void setPassword(String password) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", "@string/salt", true);
		secureDataHandler.put("@string/str_password", password);
	}
	public void setEmail(String email) {
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_email", email);
		dataSetter.commit();
	}
	public void setBolIsLoggedIn(boolean bol) {
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putBoolean("str_bolIsLoggedIn", bol);
		dataSetter.commit();
	}
	public void setBolIsRegisteredForNotifications(boolean bol) {
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putBoolean("str_bolIsRegisteredForNotifications", bol);
		dataSetter.commit();
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
