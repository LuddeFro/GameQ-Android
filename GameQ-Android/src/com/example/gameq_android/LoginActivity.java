package com.example.gameq_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends ActivityMaster{
	

	

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	private static final String alt0 = "0";
	private static final String alt1 = "1";
	private static final String alt2 = "2";
	private static final String altx = "error";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
	
		
		

		// Set up the login form.
		

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		// attempt login instantaneously if intent extras != null
		Intent intent = getIntent();
		String email = intent.getStringExtra(getResources().getString(R.string.str_email));
		String password = intent.getStringExtra(getResources().getString(R.string.str_email));
		if (!(email == null || password == null)) {
			
			mEmailView = (EditText) findViewById(R.id.email);
			mEmailView.setText(email);
			
			mPasswordView = (EditText) findViewById(R.id.password);
			mPasswordView.setText(password);
			
			attemptLogin();
		} else {
			mEmailView = (EditText) findViewById(R.id.email);
			mPasswordView = (EditText) findViewById(R.id.password);
			//do nothing, one value is null
		}
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 6) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			String loginString = connectionsHandler.postLogin(mEmail, mPassword);
			
			if (loginString == null) {
				cancel(false);
				return false;
			} else if (loginString.equals(alt1)) { //
				return true;
			} else if (loginString.equals(alt0)) { //mismatch info
				return false;
			} else if (loginString.equals(altx)) { //connection error 
				cancel(true);
				return false;
			} else { //should be unreachableunreachable
				return false;
			}
			
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				intent.putExtra(getResources().getString(R.string.str_fromLogin), "yes");
				setEmail(mEmail);
				Log.i(TAG, "setting email:" + mEmail);
				setPassword(mPassword);
				Log.i(TAG, "setting password:" + mPassword);
				setBolIsLoggedIn(true);
				Log.i(TAG, "setting email: true");
				startActivity(intent);
				String token = getToken();
				if (token != null) {
					
					new AsyncTask<Void, Void, String>() {
				        @Override
				        protected String doInBackground(Void... params) {
				            String msg = "";
				            connectionsHandler.postToken(getToken(), mEmail);
				            return msg;
				        }

				        @Override
				        protected void onPostExecute(String msg) {
				            
				        }
				    }.execute(null, null, null);
					
				}
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		/**
		 * nonFatalError is true if there was a connection error
		 * is false if something went truly wrong
		 */
		@Override
		protected void onCancelled() {
			boolean nonFatalError = isCancelled();
			View focusView = null;
			if (nonFatalError) {
				mEmailView.setError(getString(R.string.error_connection));
				focusView = mEmailView;
			} else {
				mEmailView.setError(getString(R.string.error_fatal));
				focusView = mEmailView;
			}
			focusView.requestFocus();
			mAuthTask = null;
			showProgress(false);
		}
	}
}
