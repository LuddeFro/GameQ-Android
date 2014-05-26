package com.example.gameq_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gameq_android.LoginActivity.UserLoginTask;
import com.example.gameq_android.dummy.DummyContent;

/**
 * A list fragment representing a list of Devices. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link DeviceDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class DeviceListFragment extends ListFragment {

	static final String TAG = "GameQ-Android";
	private static final String SALT = "iuyavos32bdf83ika";
	private PostTask mAuthTask;
	public String mEmail;
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DeviceListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		/*
		setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, DummyContent.ITEMS));
				*/
	}
	
	@Override
	public void onResume() {
		super.onResume();

		
		mEmail = getEmail(); 
		if (mEmail != null) {
			mAuthTask = new PostTask();
			mAuthTask.execute((Void) null);
		}
		/*
		String listString = ((DeviceListActivity) getActivity()).connectionsHandler.postUpdateDraw(email);
		String[] arrayString =listString.split(":");
		setListAdapter(new ListAdapter(getActivity(), arrayString));
		((BaseAdapter)getListAdapter()).notifyDataSetChanged();*/
		/*
		setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, DummyContent.ITEMS));
				*/
	}
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}
	
	public String getEmail() {
		SecurePreferences secureDataHandler = new SecurePreferences(getActivity().getBaseContext(), "securePrefs", SALT, true);
		String email = secureDataHandler.getString("@string/str_email");
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String email = dataGetter.getString("@string/str_email", null);*/
		Log.i(TAG, "got email: " + email);
		return email;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		TextView statusTextView = (TextView) view.findViewById(R.id.secondLine);
	    TextView deviceTextView = (TextView) view.findViewById(R.id.firstLine);
	    int status;
	    if (statusTextView == null)
	    	return;
	    if (statusTextView.getText() == null)
	    	return;
	    String statusString = statusTextView.getText().toString();
	    if (statusString.contains(getResources().getString(R.string.status0))) {
	    	status = 0;
	    } else if (statusString.contains(getResources().getString(R.string.status1))) {
	    	status = 1;
	    } else if (statusString.contains(getResources().getString(R.string.status2))) {
	    	status = 2;
	    } else if (statusString.contains(getResources().getString(R.string.status3))) {
	    	status = 3;
	    } else {
	    	status = 4;
	    }
		DummyContent.addItem("" + position, deviceTextView.getText().toString(), statusString, status);
		mCallbacks.onItemSelected(DummyContent.ITEMS.get(position).id);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class PostTask extends AsyncTask<Void, Void, Boolean> {
		
		@Override
		protected Boolean doInBackground(Void... params) {
			((DeviceListActivity) getActivity()).initiation();
			String listString = ((DeviceListActivity) getActivity()).connectionsHandler.postUpdateDraw(mEmail);
			final String[] arrayString = listString.split(":");
			getActivity().runOnUiThread(new Runnable() {
			    public void run() {
			    	setListAdapter(new ListAdapter(getActivity(), arrayString));
					((BaseAdapter)getListAdapter()).notifyDataSetChanged();
			    }
			});
			
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
				
			} else {
				
			}
		}

		/**
		 * nonFatalError is true if there was a connection error
		 * is false if something went truly wrong
		 */
		@Override
		protected void onCancelled() {
			boolean nonFatalError = isCancelled();
			if (nonFatalError) {
				
			} else {
				
			}
			mAuthTask = null;
		}
	}

	
}
