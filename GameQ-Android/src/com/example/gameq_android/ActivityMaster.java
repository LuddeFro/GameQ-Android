package com.example.gameq_android;


import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

public class ActivityMaster extends ActionBarActivity {

	protected void showWebsite(View view) {
		//TODO
	}
	
	public void logout(View view) {
		//TODO post logout
		showLogin();
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
	
	private void showLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		//make sure back button cannot take user back to being logged in
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		//superfluous addition to preventing back button bug ?!
		finish();
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
