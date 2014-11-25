package lvf.io.gameq.gameq_android;



import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class GcmIntentService extends IntentService {
	
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    static final String TAG = "GameQ-Android";

    private static final String SALT = "iuyavos32bdf83ika";
    
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        //GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        
        //String messageType = gcm.getMessageType(intent);

        Log.i(TAG, "PUSH RECEIVED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        
        
        
        
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
        	/**
        	 * code from google, Server doesn't recognize types of messages
        	 * but sends a message to be displayed (plain and simple). 
        	 */
        	Log.i(TAG, "extras is not empty");
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
        	/*
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                sendNotification("Received: " + extras.toString());
                Log.i(TAG, "Received: " + extras.toString());
            }*/
        	
        	
        	/**
        	 *  data set on server as 
        	 *  data {
        	 *  	message:messageHere
        	 *  }
        	 */
        	
        	Log.i(TAG, "message: " + extras.getString("message"));
        	sendNotification(extras.getString("message"));
        } else {
        	Log.i(TAG, "extras is empty");
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
    	
    	
    	SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String strbol = secureDataHandler.getString("@string/str_bolIsRegisteredForNotifications");
		boolean bol;
		if (strbol == null) {
			bol = false;
			secureDataHandler.put("@string/str_bolIsRegisteredForNotifications", "0");
		} else if (strbol.equals("0")) {
			bol = false;
		} else {
			bol = true;
		}
		if (bol) {
			Log.i(TAG, "is registered for notifications!!!");
			mNotificationManager = (NotificationManager)
	                this.getSystemService(Context.NOTIFICATION_SERVICE);

	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	                new Intent(this, DeviceListActivity.class), 0);

	        NotificationCompat.Builder mBuilder =
	                new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.ic_stat_gcm)
	        .setContentTitle("GameQ Notification")
	        .setStyle(new NotificationCompat.BigTextStyle()
	     // Vibrate if vibrate is enabled
	        .bigText(msg))
	        .setContentText(msg);
	        mBuilder.setVibrate(new long[] { 1000, 1000});
	        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        mBuilder.setSound(uri);
	        mBuilder.setLights(0xffffffff, 1000, 1000);
	        mBuilder.setContentIntent(contentIntent);
	        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
	        
	        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	        
	        
	        
	        /* tror att foljande kod forsoker oppna appen nar man far notification, vet inte om det funkar
	        Intent myIntent = new Intent(getBaseContext().getApplicationContext(), SwipeActivity.class);
	        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        Bundle bundle = new Bundle();
	        bundle.putString("message", msg);
	        myIntent.putExtras(bundle);
	        getBaseContext().getApplicationContext().startActivity(myIntent);
	        */
		} 
    }
}