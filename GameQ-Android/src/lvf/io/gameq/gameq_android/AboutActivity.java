package lvf.io.gameq.gameq_android;

import lvf.io.gameq.gameq_android.R;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.os.Build;

public class AboutActivity extends ActivityMaster {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		//
		Switch notSwitch = (Switch) findViewById(R.id.switch1Settings); 
		notSwitch.setChecked(getBolIsRegisteredForNotifications());
		
		
		
		OnCheckedChangeListener listenerNots = new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        setBolIsRegisteredForNotifications(isChecked);
		    }
		};
		Button twitterButton = (Button) this.findViewById(R.id.btnTwitter);
		Button facebookButton = (Button) this.findViewById(R.id.btnFacebook);
		Button websiteButton = (Button) this.findViewById(R.id.button1);
		twitterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.str_twitter)));
        		startActivity(browserIntent);
            }
		});
		
		facebookButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.str_facebook)));
        		startActivity(browserIntent);
            }
		});
		
		websiteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.str_website)));
        		startActivity(browserIntent);
            }
		});
		
		notSwitch.setOnCheckedChangeListener(listenerNots);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_about,
					container, false);
			return rootView;
		}
	}
	

}
