package lvf.io.gameq.gameq_android;

import lvf.io.gameq.gameq_android.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
	static final String TAG = "GameQ-Android";

	public ListAdapter(Context context, String[] values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
	    this.values = values;
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
	    TextView statusTextView = (TextView) rowView.findViewById(R.id.secondLine);
	    TextView deviceTextView = (TextView) rowView.findViewById(R.id.firstLine);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    String item = values[position];
	    Log.i(TAG, "tem: " + item);
	    if (item.equals(null) || item.length() <= 3) {
	    	imageView.setImageResource(R.drawable.transparent);
	    	statusTextView.setText("For info on connecting other devices, visit GameQ.io");
	    	deviceTextView.setText("No devices detected");
	    	return rowView;
	    }
	    String statusCode = item.substring(2, 4);
	    String gameCode = item.substring(0, 2);
	    int codeStatus = Integer.parseInt(statusCode);
	    int codeGame = Integer.parseInt(gameCode);
	    String devName = item.substring(4, item.length()-14);
	    deviceTextView.setText(devName);
	    
	    
	    String game;
	    String status = "";
	    
	    switch (codeGame) {
			case 0:
				game = "none";
				break;
			case 1:
				game = "Heroes of Newerth";
				break;
			case 2:
				game = "Dota 2";
				break;
			case 3:
				game = "CS:GO";
				break;				
			default:
				game = "unknown game";
				break;
	    }
	    switch (codeStatus) {
		case 0:
			imageView.setImageResource(R.drawable.red_light);
			status = getContext().getResources().getString(R.string.status0);
			break;
		case 1:
			imageView.setImageResource(R.drawable.yellow_light);
			status = getContext().getResources().getString(R.string.status1) + " "+ game;
			break;
		case 2:
			imageView.setImageResource(R.drawable.green_light);
			status = getContext().getResources().getString(R.string.status2) + " " + game;
			break;
		case 4:
			imageView.setImageResource(R.drawable.grey_light);
			status = getContext().getResources().getString(R.string.status3);
			break;				
		default:
			imageView.setImageResource(R.drawable.blue_light);
			status = getContext().getResources().getString(R.string.status4);
			break;
    }
	    statusTextView.setText(status);
	    
	    

	    return rowView;
	  }

}
