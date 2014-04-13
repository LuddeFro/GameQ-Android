package com.example.gameq_android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

import com.example.gameq_android.dummy.DummyContent;

/**
 * A fragment representing a single Device detail screen. This fragment is
 * either contained in a {@link DeviceListActivity} in two-pane mode (on
 * tablets) or a {@link DeviceDetailActivity} on handsets.
 */
public class DeviceDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public DeviceDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_device_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			RelativeLayout rel = ((RelativeLayout) rootView.findViewById(R.id.device_detail));
			ImageView iv = (ImageView)rel.findViewWithTag("@string/imgStatus");
			switch (mItem.status) {
				case 0:
					iv.setImageResource(R.drawable.red_light);
					break;
				case 1:
					iv.setImageResource(R.drawable.yellow_light);
					break;
				case 2:
					iv.setImageResource(R.drawable.green_light);
					break;
				case 3:
					iv.setImageResource(R.drawable.grey_light);
					break;
				case 4:
					iv.setImageResource(R.drawable.grey_light);
					break;				
				default:
					iv.setImageResource(R.drawable.blue_light);
					break;
			}
			TextView tv1 = (TextView)rel.findViewWithTag("@string/txtStatusText");
			TextView tv2 = (TextView)rel.findViewWithTag("@string/txtDeviceName");
			tv1.setText(mItem.statusText);
			tv2.setText(mItem.deviceName);
			
		}

		return rootView;
	}
}
