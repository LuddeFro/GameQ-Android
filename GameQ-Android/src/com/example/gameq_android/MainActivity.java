package com.example.gameq_android;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActivityMaster {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		String fromLogin = intent.getStringExtra(getResources().getString(R.string.str_fromLogin));
		String message = intent.getStringExtra("message");
		Log.i(TAG, "Main being shown ------------------------");
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
		
		
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		if (message != null) {
			
			alert(message);
		}
		
		
		
		
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	
	

}
