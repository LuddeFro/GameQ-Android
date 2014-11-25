package lvf.io.gameq.gameq_android;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lvf.io.gameq.gameq_android.util.IabHelper;
import lvf.io.gameq.gameq_android.util.IabResult;
import lvf.io.gameq.gameq_android.util.Inventory;
import lvf.io.gameq.gameq_android.util.Purchase;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

@SuppressLint("CutPasteId")
public class ActivityMaster extends ActionBarActivity {
	
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    protected static final String PROPERTY_APP_VERSION = "appVersion";
    private final Activity thisActivity = this;
    protected Dialog dialog;
    protected IabHelper storeHelper;
    private static final String SALT = "iuyavos32bdf83ika";
    public static String[] lastGames;
    public static boolean showingStore = false;
    
    public static Boolean isPurchasing = false;
    public static Boolean menuEnabled = true;
    static final int dotaID = 202;
    static final int csgoID = 303;
    static final int honID = 101;
    public String[] prices = new String[4];
    private RelativeLayout rootLay;
    Context context;
    GoogleCloudMessaging gcm;
    String regid;
    //Project Number from GameQ @ https://console.developers.google.com/project/
    //project GameQ by GameQ, only accessible for GameQ under "projects"
    //also exists in DeviceListActivity
    protected String SENDER_ID = "773141536608";
    final static String SKU_dota2 = "io.gameq.w3_dota2";
	final static String SKU_hon = "io.gameq.w3_hon";
	final static String SKU_csgo = "io.gameq.w3_csgo";
    /**
     * Tag used on log messages.
     */
    static final String TAG = "GameQ-Android";
    
	public ConnectionHandler connectionsHandler;
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (storeHelper != null) storeHelper.dispose();
		storeHelper = null;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		context = getApplicationContext();
		
		connectionsHandler = new ConnectionHandler(this);

		String en = "gUbMTPS/LKeUK4yRC+QJLX7MU+SydkeFS2J9FX1tvgtRe3qSBDYVpKzKpN";
		String d = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoFrmv70ikX8cD";
		String va = "wC7+pNul6HWManVb6CyYg0SW2ur6nujeQRuM9tFhwtsDBXmpuriX2ZT+tB";
		String entva = "9Kcjf7IHSPCveqnsYfpY9S/4TL8COiRBWE9/cncAlBxM6pgwFPwkkoIht5+5";
		String gam = "g2cQdRxdbiO0zCRMn3wY91z8uka1cNnfQIDAQAB";
		String mf = "mhr4nFlcTmEJbxxV1fRFbQwPXF/P5VGPeFtxSRk3gmAflrmA3iqOcw61/wPMi";
		String g = "jCB2LTB+mfHjSlKEiP/6ohPR3Q5pFnvvRgXmOt8GeSQ2YZ1COoNnvqDz0xA";
		String pubKey = d + va + en + g + entva + mf + gam;
		//Log.d(TAG, "Problem setting up In-app Billing: " + pubKey);
		
		
		
		final IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
				   new IabHelper.OnConsumeFinishedListener() {
				   public void onConsumeFinished(Purchase purchase, IabResult result) {
				      if (result.isSuccess()) {
				         String gString = "";
				    	 if (purchase.getSku().equals(SKU_csgo)) {
				        	 gString = "3";
				         } else if (purchase.getSku().equals(SKU_dota2)) {
				        	 gString = "2";
				         } else if (purchase.getSku().equals(SKU_hon)) {
				        	 gString = "1";
				         }
				    	 connectionsHandler.postUpMyGames(getEmail(), gString);
				      }
				      else {
				         // handle error
				      }
				   }
				};
		
		final IabHelper.QueryInventoryFinishedListener mGotInventoryListener 
		   = new IabHelper.QueryInventoryFinishedListener() {
		   public void onQueryInventoryFinished(IabResult result,
		      Inventory inventory) {

		      if (result.isFailure()) {
		        // handle error here
		      }
		      else {
		    	  if (getBolIsLoggedIn()) {
		    		  if (inventory.hasPurchase(SKU_dota2)) {
		    			  storeHelper.consumeAsync(inventory.getPurchase(SKU_dota2), 
								   mConsumeFinishedListener);
		    		  } else if (inventory.hasPurchase(SKU_hon)) {
		    			  storeHelper.consumeAsync(inventory.getPurchase(SKU_hon), 
								   mConsumeFinishedListener);
		    		  } else if (inventory.hasPurchase(SKU_csgo)) {
		    			  storeHelper.consumeAsync(inventory.getPurchase(SKU_csgo), 
								   mConsumeFinishedListener);
		    		  }      
		    		  // update UI accordingly
		    	  }
		      }
		   }
		};
		
		
		
		
		storeHelper = new IabHelper(this, pubKey);
		
		
		
		storeHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			   public void onIabSetupFinished(IabResult result) {
			      if (!result.isSuccess()) {
			         // Oh noes, there was a problem.
			         Log.d(TAG, "Problem setting up In-app Billing: " + result);
			      }            
			         // Hooray, IAB is fully set up! 
			      	Log.d(TAG, "Set up In-app Billing: " + result);
			      	storeHelper.queryInventoryAsync(mGotInventoryListener);
			   }
			});
		
		
				
		
		
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    //if (!checkPlayServices()) {
	    	//Log.i(TAG, "@string/out_of_date_Google_Services_APK");
	    	//alert("@string/out_of_date_Google_Services_APK");
	    	
	    //}
	    if (!checkPlayServices()) {
			Log.i(TAG, "!checkPlayServices() returns true in onCreate() Activity Master");
	    	//Log.i(TAG, "@string/out_of_date_Google_Services_APK");
	    	//alert("@string/out_of_date_Google_Services_APK");
	    } else {
	    	gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
	    }
	}
	
	@Override
	protected void onStop() {
		if (dialog != null)
			dialog.dismiss();
		super.onStop();
	}
	
	@Override
	protected void onPause() {
		if (dialog != null)
			dialog.dismiss();
		super.onPause();
	}
	
	protected void alert(String message)
	{
		dialog = new AlertDialog.Builder(this)
	    .setTitle("GameQ")
	    .setMessage(message)
	    .setIcon(R.drawable.ic_stat_gcm)
	    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     })/*
	    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // do nothing
	        }
	     })*/
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .create();
		dialog.show();
	}
	
	
	
	
	
	
	
	public void logout(View view) {
		
		
		
		
		new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            	connectionsHandler.postLogout();
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	            
	        }
	    }.execute(null, null, null);
	    
	    
		
		setPassword(null);
		setEmail(null);
		setBolIsLoggedIn(false);
		setBolIsRegisteredForNotifications(false);
		setToken(null);
		
		//TODO unregister for push notifications
		showLogin(null, null);
	}
	
	public void updateStoreUI(List<String> theList){
		

		final RelativeLayout rootLayout = (RelativeLayout)findViewById(R.id.container);
		View.inflate(this, R.layout.fragment_store, rootLayout);
		rootLay = rootLayout;

		Button exitStoreButton = (Button) rootLayout.findViewById(R.id.exitStoreBtn);
		final Button btnForEnable = (Button) rootLayout.findViewById(R.id.exitStoreBtn);
		
		View.inflate(this, R.layout.store_layout, rootLayout);
		LinearLayout linLay = (LinearLayout) rootLayout.findViewById(R.id.linlayoutforscrollView);
		View.inflate(this, R.layout.storegame, linLay);
		RelativeLayout dotaLay = (RelativeLayout) rootLayout.findViewById(R.id.gameLayout);
		
		dotaLay.setId(dotaID);
		View.inflate(this, R.layout.storegame, linLay);
		RelativeLayout csgoLay = (RelativeLayout) rootLayout.findViewById(R.id.gameLayout);
		csgoLay.setId(csgoID);
		View.inflate(this, R.layout.storegame, linLay);
		RelativeLayout honLay = (RelativeLayout) rootLayout.findViewById(R.id.gameLayout);
		honLay.setId(honID);
		View.inflate(this, R.layout.fragment_addgames, rootLayout);
		exitStoreButton.setEnabled(false);
		exitStoreButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	hideStore();
            }
		});
		
		
		
		
		((TextView) csgoLay.findViewById(R.id.gameDescView)).setText(theList.get(7));
		((TextView) dotaLay.findViewById(R.id.gameDescView)).setText(theList.get(4));
		((TextView) honLay.findViewById(R.id.gameDescView)).setText(theList.get(1));
		
		((TextView) csgoLay.findViewById(R.id.gamePriceView)).setText(theList.get(8));
		((TextView) dotaLay.findViewById(R.id.gamePriceView)).setText(theList.get(5));
		((TextView) honLay.findViewById(R.id.gamePriceView)).setText(theList.get(2));
		
		((ImageView) csgoLay.findViewById(R.id.imageView1)).setImageResource(R.drawable.csgo);
		((ImageView) dotaLay.findViewById(R.id.imageView1)).setImageResource(R.drawable.dota);
		((ImageView) honLay.findViewById(R.id.imageView1)).setImageResource(R.drawable.hon);
		if (isPurchasing) {
			((Button) csgoLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_checkingAvailability));
			((Button) dotaLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_checkingAvailability));
			((Button) honLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_checkingAvailability));
			((Button) csgoLay.findViewById(R.id.button1)).setEnabled(false);
			((Button) dotaLay.findViewById(R.id.button1)).setEnabled(false);
			((Button) honLay.findViewById(R.id.button1)).setEnabled(false);
		} else {
			((Button) csgoLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_select));
			((Button) dotaLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_select));
			((Button) honLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_select));
			
			((TextView) csgoLay.findViewById(R.id.gamePriceView)).setText(getResources().getString(R.string.str_lbl_free));
			((TextView) dotaLay.findViewById(R.id.gamePriceView)).setText(getResources().getString(R.string.str_lbl_free));
			((TextView) honLay.findViewById(R.id.gamePriceView)).setText(getResources().getString(R.string.str_lbl_free));
		}

		((Button) csgoLay.findViewById(R.id.button1)).setAlpha(1);
		((Button) dotaLay.findViewById(R.id.button1)).setAlpha(1);
		((Button) honLay.findViewById(R.id.button1)).setAlpha(1);
		
		if (isPurchasing) {
			Button buyDota = (Button) dotaLay.findViewById(R.id.button1);
			
			buyDota.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	buyGame("2");
	            }
			});
			
			Button buyHon = (Button) honLay.findViewById(R.id.button1);
			
			buyHon.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	buyGame("1");
	            }
			});
			
			Button buyCsgo = (Button) csgoLay.findViewById(R.id.button1);
			
			buyCsgo.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	buyGame("3");
	            }
			});
		} else {
			Button buyDota = (Button) dotaLay.findViewById(R.id.button1);
			
			buyDota.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	new AsyncTask<Void, Void, String>() {
	    		        @Override
	    		        protected String doInBackground(Void... params) {
	    		            String msg = "";
	    	            	connectionsHandler.postUpMyGames(getEmail(), "2");
	    		            return msg;
	    		        }

	    		        @Override
	    		        protected void onPostExecute(String msg) {
	    		            
	    		        }
	    		    }.execute(null, null, null);
	            }
			});
			
			Button buyHon = (Button) honLay.findViewById(R.id.button1);
			
			buyHon.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	new AsyncTask<Void, Void, String>() {
	    		        @Override
	    		        protected String doInBackground(Void... params) {
	    		            String msg = "";
	    	            	connectionsHandler.postUpMyGames(getEmail(), "1");
	    		            return msg;
	    		        }

	    		        @Override
	    		        protected void onPostExecute(String msg) {
	    		            
	    		        }
	    		    }.execute(null, null, null);
	            }
			});
			
			Button buyCsgo = (Button) csgoLay.findViewById(R.id.button1);
			
			buyCsgo.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	new AsyncTask<Void, Void, String>() {
	    		        @Override
	    		        protected String doInBackground(Void... params) {
	    		            String msg = "";
	    	            	connectionsHandler.postUpMyGames(getEmail(), "3");
	    		            return msg;
	    		        }

	    		        @Override
	    		        protected void onPostExecute(String msg) {
	    		            
	    		        }
	    		    }.execute(null, null, null);
	            }
			});
		}
		
		if (isPurchasing){
			new AsyncTask<Void, Void, String>() {
		        @Override
		        protected String doInBackground(Void... params) {
		            String msg = "";
		            connectionsHandler.postGetMyGames(getEmail());
		            return msg;
		        }

		        @Override
		        protected void onPostExecute(String msg) {
		            
		        }
		    }.execute(null, null, null);
		}
		
	    final Activity tmpActtmp = this;
	    final class ReEnableBack implements Runnable {

	        public void run() {
	        	
	        		try {
	        			Thread.sleep(1000);
	        		} catch (InterruptedException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        		}
	        	
	        	tmpActtmp.runOnUiThread(new Runnable() {
	    		    public void run() {
	    		    	btnForEnable.setEnabled(true);
	    		    	
	    		    }
	    		});
	        }

	    }
	    (new Thread(new ReEnableBack())).start();
	     
		return;
		
	}
	
	
	public void buyGame(String game) {
		Log.d(TAG, "buying: " + game);
		
		String item = "";
		int purCode = 0;
		if (game.equals("1")) {
			item = SKU_hon;
			purCode = 10101;
		} else if (game.equals("2")) {
			item = SKU_dota2;
			purCode = 20202;
		} else if (game.equals("3")) {
			item = SKU_csgo;
			purCode = 30303;
		}
		final String hashish = Encryptor.hashSHA256(game + purCode + item);
		
		final IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
				   new IabHelper.OnConsumeFinishedListener() {
				   public void onConsumeFinished(Purchase purchase, IabResult result) {
				      if (result.isSuccess()) {
				         String gString = "";
				    	 if (purchase.getSku().equals(SKU_csgo)) {
				        	 gString = "3";
				         } else if (purchase.getSku().equals(SKU_dota2)) {
				        	 gString = "2";
				         } else if (purchase.getSku().equals(SKU_hon)) {
				        	 gString = "1";
				         }
				    	 connectionsHandler.postUpMyGames(getEmail(), gString);
				      }
				      else {
				         // handle error
				      }
				   }
				};
		
		IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener 
		   = new IabHelper.OnIabPurchaseFinishedListener() {
		   public void onIabPurchaseFinished(IabResult result, Purchase purchase) 
		   {
		      if (result.isFailure() || !purchase.getDeveloperPayload().equals(hashish)) {
		         Log.d(TAG, "Error purchasing: " + result);
		         return;
		      } else  {
				  storeHelper.consumeAsync(purchase, 
						   mConsumeFinishedListener);
			  }
		      
		   }
		};
		
		
		
		storeHelper.launchPurchaseFlow(this, item, purCode,   
				   mPurchaseFinishedListener, hashish);
		
		
		
		
	}
	
	public void updateStore() {
		if (rootLay == null) {
			return;
		}
		
		this.runOnUiThread(new Runnable() {
		    public void run() {
		    	
		    	RelativeLayout dotaLay = (RelativeLayout) rootLay.findViewById(dotaID);
				RelativeLayout csgoLay = (RelativeLayout) rootLay.findViewById(csgoID);
				RelativeLayout honLay = (RelativeLayout) rootLay.findViewById(honID);
				String[] games = lastGames[0].split(":");

				((Button) honLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_buy));
				((Button) honLay.findViewById(R.id.button1)).setEnabled(true);
				((TextView) honLay.findViewById(R.id.gamePriceView)).setText(prices[1]);
				((Button) honLay.findViewById(R.id.button1)).setAlpha(1);
			
				((Button) dotaLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_buy));
				((Button) dotaLay.findViewById(R.id.button1)).setEnabled(true);
				((TextView) dotaLay.findViewById(R.id.gamePriceView)).setText(prices[2]);
				((Button) dotaLay.findViewById(R.id.button1)).setAlpha(1);
			
				((Button) csgoLay.findViewById(R.id.button1)).setText(getResources().getString(R.string.str_btn_buy));
				((Button) csgoLay.findViewById(R.id.button1)).setEnabled(true);
				((TextView) csgoLay.findViewById(R.id.gamePriceView)).setText(prices[3]);
				((Button) csgoLay.findViewById(R.id.button1)).setAlpha(1);
				
				for (int i = 0; i < games.length; i++) {
					String game = games[i];
					if (game.equals("1")) {
						((Button) honLay.findViewById(R.id.button1)).setText("");
						((Button) honLay.findViewById(R.id.button1)).setAlpha(0);
						((Button) honLay.findViewById(R.id.button1)).setEnabled(false);
						((TextView) honLay.findViewById(R.id.gamePriceView)).setText(getResources().getString(R.string.str_lbl_aldreadyOwned));
						
					} else if (game.equals("2")){
						((Button) dotaLay.findViewById(R.id.button1)).setText("");
						((Button) dotaLay.findViewById(R.id.button1)).setAlpha(0);
						((Button) dotaLay.findViewById(R.id.button1)).setEnabled(false);
						((TextView) dotaLay.findViewById(R.id.gamePriceView)).setText(getResources().getString(R.string.str_lbl_aldreadyOwned));
						
					} else if (game.equals("3")){
						((Button) csgoLay.findViewById(R.id.button1)).setText("");
						((Button) csgoLay.findViewById(R.id.button1)).setAlpha(0);
						((Button) csgoLay.findViewById(R.id.button1)).setEnabled(false);
						((TextView) csgoLay.findViewById(R.id.gamePriceView)).setText(getResources().getString(R.string.str_lbl_aldreadyOwned));
						
					}
					
				}
		    	
		    }
		});
	}
	
	public void showStore(View view, Boolean firstTime) {
		
		Log.d(TAG, "ShowingStore!?!?!?!?!?!!?!??!?!?!?!?");
		showingStore = true;
		isPurchasing = !firstTime;
		menuEnabled = false;
		invalidateOptionsMenu();
		
		Log.d(TAG, "showingStore");
		
		List<String> additionalSkuList = new ArrayList<String>();
		additionalSkuList.add(SKU_dota2);
		additionalSkuList.add(SKU_hon);
		additionalSkuList.add(SKU_csgo);
				
		
		
		IabHelper.QueryInventoryFinishedListener 
		   mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
		   public void onQueryInventoryFinished(IabResult result, Inventory inventory)   
		   {
		      if (result.isFailure()) {
		         // handle error
		         return;
		       }

		       String dotaPrice = inventory.getSkuDetails(SKU_dota2).getPrice();
		       String honPrice = inventory.getSkuDetails(SKU_hon).getPrice();
		       String csgoPrice = inventory.getSkuDetails(SKU_csgo).getPrice();
		       
		       prices[0] = "nonsense";
		       prices[1] = honPrice;
		       prices[2] = dotaPrice;
		       prices[3] = csgoPrice;
		       
		       String dotaDesc =  inventory.getSkuDetails(SKU_dota2).getDescription();
		       String honDesc = inventory.getSkuDetails(SKU_hon).getDescription();
		       String csgoDesc = inventory.getSkuDetails(SKU_csgo).getDescription();
		       
		       String dotaName = inventory.getSkuDetails(SKU_dota2).getTitle();
		       String csgoName = inventory.getSkuDetails(SKU_csgo).getTitle();
		       String honName = inventory.getSkuDetails(SKU_hon).getTitle();
		       List<String> theList = new ArrayList<String>();
		       theList.add(honName);
		       Log.d(TAG, "honName: " + honName);
		       theList.add(honDesc);
		       theList.add("Price: " + honPrice);
		       theList.add(dotaName);
		       Log.d(TAG, "dotaName: " + dotaName);
		       theList.add(dotaDesc);
		       theList.add("Price: " + dotaPrice);
		       theList.add(csgoName);
		       Log.d(TAG, "csgoName: "+csgoName);
		       theList.add(csgoDesc);
		       theList.add("Price: " + csgoPrice);
		       updateStoreUI(theList);
		       
		       // update the UI 
		   }
		};
		
		storeHelper.queryInventoryAsync(true, additionalSkuList,
				 mQueryFinishedListener);
		
		
		
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && showingStore && isPurchasing) {
	        hideStore();
	        return true;
	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	
	
	public void hideStore() {
		this.runOnUiThread(new Runnable() {
		    public void run() {
		    	showingStore = false;
		    	if (rootLay != null) {
			    	rootLay.removeView(findViewById(R.id.backbutton));
			    	rootLay.removeView(findViewById(R.id.horizontalScrollView1));
			    	rootLay.removeView(findViewById(R.id.addGamesTxtView));
	            	menuEnabled = true;
					invalidateOptionsMenu();
		    	}
		       
		    }
		});
	}
	
	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		int siz = menu.size();
		if (menuEnabled) {
	    	for (int i = 0; i < siz; i++) {
	    		menu.getItem(i).setEnabled(true);
	    	}
	    } else {
	    	for (int i = 0; i < siz; i++) {
	    		menu.getItem(i).setEnabled(false);
	    	}
	    }
	    	
	    return true;
	    
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
		intent.putExtra(getResources().getString(R.string.str_email), email);
		intent.putExtra(getResources().getString(R.string.str_password), password);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
		//superfluous addition to preventing back button bug ?!
		//finish();
	}
	
	
	
	
	
	
	public String getToken() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String token = secureDataHandler.getString("@string/str_token");
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String token = dataGetter.getString("@string/str_token", null);*/
		Log.i(TAG, "got Token: " + token);
		return token;
	}
	public String getPassword() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String password = secureDataHandler.getString("@string/str_password");
		Log.i(TAG, "got password: " + password);
		return password;
	}
	public String getEmail() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String email = secureDataHandler.getString("@string/str_email");
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String email = dataGetter.getString("@string/str_email", null);*/
		Log.i(TAG, "got email: " + email);
		return email;
	}
	public boolean getBolIsLoggedIn() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String strbol = secureDataHandler.getString("@string/str_bolIsLoggedIn");
		if (strbol == null) {
			setBolIsLoggedIn(false);
			return false;
		}
		boolean bol;
		if (strbol.equals("0")) {
			bol = false;
		} else {
			bol = true;
		}/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		boolean bol = dataGetter.getBoolean("@string/str_bolIsLoggedIn", false);*/
		Log.i(TAG, "gotbolLoggedin: " + bol);
		return bol;
	}
	public boolean getBolIsRegisteredForNotifications() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String strbol = secureDataHandler.getString("@string/str_bolIsRegisteredForNotifications");
		boolean bol;
		if (strbol == null) {
			setBolIsRegisteredForNotifications(true);
			return true;
		}
			
		if (strbol.equals("0")) {
			bol = false;
		} else {
			bol = true;
		}/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		boolean bol = dataGetter.getBoolean("@string/str_bolIsRegisteredForNotifications", false);*/
		Log.i(TAG, "got bolRegistered: " + bol);
		return bol;
	}
	public String getFirstLogger() {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		String banana = secureDataHandler.getString("@string/str_bananana");
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		String token = dataGetter.getString("@string/str_token", null);*/
		Log.i(TAG, "got firstLogger: " + banana);
		return banana;
	}
	public void setFirstLogger(String banana) {
		
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_bananana", banana);
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_token", token);
		dataSetter.commit();*/
		Log.i(TAG, "set firstLogger: "+ banana);
		
	}
	public void setToken(String token) {
		
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_token", token);
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_token", token);
		dataSetter.commit();*/
		Log.i(TAG, "set Token: "+ token);
		
	}
	public void setPassword(String password) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_password", password);
		Log.i(TAG, "set password: " + password);
	}
	public void setEmail(String email) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		secureDataHandler.put("@string/str_email", email);
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putString("@string/str_email", email);
		dataSetter.commit();*/
		Log.i(TAG, "set email: "+ email);
	}
	public void setBolIsLoggedIn(boolean bol) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		if (bol) {
			secureDataHandler.put("@string/str_bolIsLoggedIn", "1");
		} else {
			secureDataHandler.put("@string/str_bolIsLoggedIn", "0");
		}
		/*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putBoolean("str_bolIsLoggedIn", bol);
		dataSetter.commit();*/
		Log.i(TAG, "set bolLoggedIn: "+ bol);
	}
	public void setBolIsRegisteredForNotifications(boolean bol) {
		SecurePreferences secureDataHandler = new SecurePreferences(getBaseContext(), "securePrefs", SALT, true);
		if (bol) {
			secureDataHandler.put("@string/str_bolIsRegisteredForNotifications", "1");
		} else {
			secureDataHandler.put("@string/str_bolIsRegisteredForNotifications", "0");
		} /*
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor dataSetter = dataGetter.edit();
		dataSetter.putBoolean("str_bolIsRegisteredForNotifications", bol);
		dataSetter.commit();*/
		Log.i(TAG, "set bolRegistered: " + bol);
	}
	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	protected boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    Log.i(TAG, "checkGooglePlayServicesAvailable, connectionStatusCode="
	    	    + resultCode);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	        	showGooglePlayServicesAvailabilityErrorDialog(resultCode);
	            //GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    //PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	private void showGooglePlayServicesAvailabilityErrorDialog(
		    final int connectionStatusCode) {
			this.runOnUiThread(new Runnable() {
		    public void run() {
		    dialog = GooglePlayServicesUtil.getErrorDialog(
		        connectionStatusCode, thisActivity,
		        0);
		        if (dialog == null) {
		                Log.e(TAG,
		                        "couldn't get GooglePlayServicesUtil.getErrorDialog");
		                Toast.makeText(context,
		                        "incompatible version of Google Play Services",
		                        Toast.LENGTH_LONG).show();
		            }
		            dialog.show();
		       }
		    });
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
	
	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 *
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	protected String getRegistrationId(Context context) {
	    String token = getToken();
		SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
	    if (token == null || token.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = dataGetter.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        this.setFirstLogger("");
	        return "";
	    }
	    return token;
	}
	
	protected static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	protected void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;
	                
	                connectionsHandler.postToken(regid, getEmail());
	                setToken(regid);
	                
	                SharedPreferences dataGetter = getPreferences(Context.MODE_PRIVATE);
	        		SharedPreferences.Editor dataSetter = dataGetter.edit();
	        		dataSetter.putInt(PROPERTY_APP_VERSION, getAppVersion(context));
	        		dataSetter.commit();
	                
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	            
	        }
	    }.execute(null, null, null);
	}
	
	
	
	
}
