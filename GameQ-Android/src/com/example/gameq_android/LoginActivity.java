package com.example.gameq_android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;
import android.graphics.Typeface;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends ActivityMaster{
	

	

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;
	private UserRegisterTask aAuthTask = null;
	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;
	private String mQuestion;
	private String mAnswer;
	private Boolean mDuplicate;
	private LoginActivity meMyselfAndI;
	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private EditText txtAnswer;
	private EditText txtQuestion;
	private Button btnBot;
	private Button btnTop;
	private static final String alt0 = "0";
	private static final String alt1 = "1";
	private static final String alt2 = "2";
	private static final String altx = "error";
	private boolean isRegging;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isRegging = false;
		setContentView(R.layout.activity_login);
		meMyselfAndI = this;
		txtAnswer = (EditText) findViewById(R.id.txtAnswer);
		txtAnswer.setTypeface(Typeface.DEFAULT);
		txtAnswer.setTransformationMethod(new PasswordTransformationMethod());
		
		txtQuestion = (EditText) findViewById(R.id.txtQuestion);
		txtAnswer.setAlpha(0);
		txtQuestion.setAlpha(0);
		txtAnswer.setEnabled(false);
		txtQuestion.setEnabled(false);
		// Set up the login form.
		

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setTypeface(Typeface.DEFAULT);
		mPasswordView.setTransformationMethod(new PasswordTransformationMethod());
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
		btnBot = (Button) findViewById(R.id.btnBot);
		btnTop = (Button) findViewById(R.id.sign_in_button);
		btnTop.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						clickTop();
					}
				});
		btnBot.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						clickBot();
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
	
	private void clickTop() {
		if (isRegging) {
			attemptRegister();
		} else {
			attemptLogin();
		}
	}
	
	private void clickBot() {
		if (isRegging) {
			setupNothing();
		} else {
			setupRegging();
		}
	}
	
	private void setupRegging() {
		
		isRegging = true;
		setupShit(isRegging);
	}
	
	private void setupNothing() {
		
		isRegging = false;
		setupShit(isRegging);
	}
	
	private void setupShit(final boolean settingUpRegging) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
				// for very easy animations. If available, use these APIs to fade-in
				// the progress spinner.
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					int shortAnimTime = getResources().getInteger(
							android.R.integer.config_shortAnimTime);

					txtAnswer.setVisibility(View.VISIBLE);
					txtAnswer.animate().setDuration(shortAnimTime)
							.alpha(settingUpRegging ? 1 : 0)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									txtAnswer.setVisibility(settingUpRegging ? View.VISIBLE
											: View.GONE);
								}
							});
					txtQuestion.setVisibility(View.VISIBLE);
					txtQuestion.animate().setDuration(shortAnimTime)
							.alpha(settingUpRegging ? 1 : 0)
							.setListener(new AnimatorListenerAdapter() {
								@Override
								public void onAnimationEnd(Animator animation) {
									txtQuestion.setVisibility(settingUpRegging ? View.VISIBLE
											: View.GONE);
								}
							});

					
				} else {
					// The ViewPropertyAnimator APIs are not available, so simply show
					// and hide the relevant UI components.
					txtQuestion.setVisibility(settingUpRegging ? View.VISIBLE : View.GONE);
					txtAnswer.setVisibility(settingUpRegging ? View.GONE : View.VISIBLE);
				}
				
				if (settingUpRegging) {
					btnBot.setText(R.string.cancel_button);
					btnTop.setText(R.string.sign_up_button);
					txtAnswer.setEnabled(true);
					txtQuestion.setEnabled(true);
				} else {
					btnTop.setText(R.string.sign_in_button);
					btnBot.setText(R.string.join_button);
					txtAnswer.setEnabled(false);
					txtQuestion.setEnabled(false);
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
	
	private void attemptRegister() {
		if (aAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		txtQuestion.setError(null);
		txtAnswer.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mQuestion = txtQuestion.getText().toString();
		mAnswer = txtAnswer.getText().toString();

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
		
		if (TextUtils.isEmpty(mQuestion)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(mAnswer)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.sign_up_progress);
			showProgress(true);
			aAuthTask = new UserRegisterTask();
			aAuthTask.execute((Void) null);
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
				Intent intent = new Intent(LoginActivity.this, DeviceListActivity.class);
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
	
	//----- Register task:
public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			String loginString = connectionsHandler.postRegister(mEmail, "", "", 1, 0, "", mPassword, mQuestion, mAnswer);
			mDuplicate = false;
			if (loginString == null) {
				cancel(false);
				return false;
			} else if (loginString.equals(alt1)) { //
				return true;
			} else if (loginString.equals(alt0)) { //mismatch info
				mDuplicate = true;
				return false;
			} else if (loginString.equals(altx)) { //connection error 
				cancel(true);
				return false;
			} else { //should be unreachable !! MFunreachable!!
				return false;
			}
			
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			aAuthTask = null;
			showProgress(false);

			if (success) {
				meMyselfAndI.runOnUiThread(new Runnable()
				{
					public void run() 
					{
						
						
						AlertDialog.Builder dialog = new AlertDialog.Builder(meMyselfAndI);
						dialog
					    .setTitle("GameQ")
					    .setMessage(getString(R.string.welcome))
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
				meMyselfAndI.mPasswordView.setText("");
				meMyselfAndI.setupNothing();
				
				
			} else {
				if (mDuplicate) {
					mEmailView
					.setError(getString(R.string.email_exists));
					mEmailView.requestFocus();
				} else {
					mPasswordView
					.setError(getString(R.string.error_connection));
					mPasswordView.requestFocus();
				}
				
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
			aAuthTask = null;
			showProgress(false);
		}
	}
}
