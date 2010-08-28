/*************************************************************************
 * File: ChessClock.java
 * 
 * Implements the main form/class for Chess Clock.
 * 
 * Created: 6/22/2010
 * 
 * Author: Carter Dewey
 * 
 *************************************************************************
 *
 *   This file is part of Simple Chess Clock (SCC).
 *    
 *   SCC is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   SCC is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with SCC.  If not, see <http://www.gnu.org/licenses/>.
 *
 *************************************************************************/

package com.android.chessclock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChessClock extends Activity {
	
	/**-----------------------------------
	 *            CONSTANTS
	 *-----------------------------------*/
	/** Version info and debug tag constants */
	public static final String TAG = "INFO";
	public static final String V_MAJOR = "0";
	public static final String V_MINOR = "6";
	public static final String V_MINI = "0";

	/** Constants for the dialog windows */
	private static final int SETTINGS = 0;
	private static final int RESET = 1;
	private static final int ABOUT = 2;
	
	/** Time control values */
	private static String NO_DELAY = "None";
	private static String FISCHER = "Fischer";
	private static String BRONSTEIN = "Bronstein";
	private String delay = NO_DELAY;
	
	/**-----------------------------------
	 *     CHESSCLOCK CLASS MEMBERS
	 *-----------------------------------*/
	/** Objects/Classes */
	private Handler myHandler = new Handler();
	private DialogFactory DF = new DialogFactory();
	private PowerManager pm;
	private WakeLock wl;
	
	/** ints/longs */
	private int time;
	private long t_P1;
	private long t_P2;
	private long delay_time;
	private int onTheClock = 0;
	private int savedOTC = 0;
	
	/** booleans */
	private boolean blink = false;
	private boolean timeup = false;
	private boolean prefmenu = false;
	private boolean delayed = false;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        
        /** Get rid of the status bar */
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        						WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        /** Create a PowerManager object so we can get the wakelock */
        pm = (PowerManager) getSystemService(ChessClock.POWER_SERVICE);  
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "ChessWakeLock");
        
        setContentView(R.layout.main);
        
        SetUpGame();       
    }
    
    @Override
    public void onPause() {
    	wl.release();
    	super.onPause();
    }
    
    @Override
    public void onDestroy() {
    	wl.release();
    	super.onDestroy();
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
    	prefmenu = true;
    	PauseGame();
    	return true;
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, SETTINGS, 0, "Settings").setIcon(R.drawable.settings);
		menu.add(0, RESET, 0, "Reset Clocks").setIcon(R.drawable.refresh);
		menu.add(0, ABOUT, 0, "About").setIcon(R.drawable.about);
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch( item.getItemId() ) {
			case SETTINGS:
				showPrefs();
				Log.v(TAG, "INFO: Trying to create Preferences screen");
				return true;
			case RESET:
				showDialog(RESET);
				return true;
			case ABOUT:
				showDialog(ABOUT);
				return true;
		}
		
		return false;
	}
	
	public void onWindowFocusChanged(boolean b) {
		if ( !prefmenu ) {
			CheckForNewPrefs();
		} else {
			prefmenu = false;
		}
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new Dialog(this);
		switch ( id ) {
			case ABOUT:
				dialog = DF.AboutDialog(this, V_MAJOR, V_MINOR, V_MINI);
				break;
			case RESET:
				dialog = ResetDialog();
				break;
		}
		
		return dialog;
	}
	
	/** Click handler for player 1's clock. */
	public OnClickListener P1ClickHandler = new OnClickListener() {
		public void onClick(View v) {
			P1Click();
		}
	};
	
	/** Click handler for player 2's clock */
	public OnClickListener P2ClickHandler = new OnClickListener() {
		public void onClick(View v) {
			P2Click();
		}
	};
	
	/** Click handler for the pause button */
	public OnClickListener PauseListener = new OnClickListener() {
		public void onClick(View v) {
			PauseToggle();
		}
	};
	
	/** Starts the Preferences menu intent */
	private void showPrefs() {
		Intent prefsActivity = new Intent(ChessClock.this, Prefs.class);
		startActivity(prefsActivity);
	}
	
	/** 
	 * Checks for changes to the current preferences. We only want
	 * to re-create the game if something has been changed, so we
	 * check for differences any time onWindowFocusChanged() is called.
	 */
	public void CheckForNewPrefs() {
		SharedPreferences prefs = PreferenceManager
    	.getDefaultSharedPreferences(this);
		
		/** Check for a new delay style */
		String new_delay = prefs.getString("prefDelay","None");
		if ( new_delay != delay ) {
			SetUpGame();
		}
		
		/** Check for a new game time setting */
		int new_time = Integer.parseInt( prefs.getString("prefTime", "10") );
		if ( new_time != time ) {
			SetUpGame();
		}
		
		/** Check for a new delay time */
		int new_delay_time = Integer.parseInt( prefs.getString("prefDelayTime", "0" ) );
		if ( new_delay_time != delay_time ) {
			SetUpGame();
		}
	}
	
	/** Creates and displays the "Reset Clocks" alert dialog */
	private Dialog ResetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Reset both clocks?")
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   	SetUpGame();
		                dialog.dismiss();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();

		return alert;
	}
	
	/** Called when P1ClickHandler registers a click/touch event */
	private void P1Click() {
		/** Un-dim the screen */
		PowerManager pm = (PowerManager)getBaseContext().getSystemService(
                Context.POWER_SERVICE);
		pm.userActivity(1, true);
		
		/** Check if this is valid (i.e. if our time is running */
		if ( onTheClock == 1 )
			return;
		
		/** 
		 * Register that our time is running now 
		 * and that we haven't yet received our delay
		 */
		onTheClock = 1;
		delayed = false;
		
		/** 
		 * Make the other player's button green and our
		 * button and the pause button gray.
		 */
		Button p2_button = (Button)findViewById(R.id.Player2);
		p2_button.setBackgroundColor(Color.GREEN);
			   
		Button p1_button = (Button)findViewById(R.id.Player1);
		p1_button.setBackgroundColor(Color.LTGRAY);
			   
		Button pp = (Button)findViewById(R.id.Pause);
		pp.setBackgroundColor(Color.LTGRAY);
			   
		/** 
		 * Unregister the handler from player 2's clock and 
		 * create a new one which we register with this clock.
		 */
		myHandler.removeCallbacks(mUpdateTimeTask);
		myHandler.removeCallbacks(mUpdateTimeTask2);
		myHandler.postDelayed(mUpdateTimeTask, 100);
	}
		
	/** Handles the "tick" event for Player 1's clock */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			
			/** Check for delays and apply them */
			if ( delay.equals(FISCHER) && !delayed ) {
				delayed = true;
				t_P1 += delay_time * 1000 + 100;
			} else if ( delay == BRONSTEIN ) {
				// TODO: Add Bronstein delay handling
			}
			
			/** Deduct 0.1s from P1's clock */
			t_P1 -= 100;
			long timeLeft = t_P1;
				 
			/** Format for display purposes */
			int secondsLeft = (int) (timeLeft / 1000);
			int minutesLeft = secondsLeft / 60;
			secondsLeft     = secondsLeft % 60;
			       
			TextView p1 = (TextView)findViewById(R.id.t_Player1);
			
			/** Did we run out of time? */
			if ( timeLeft == 0 ) {
				timeup = true;
				Button b1 = (Button)findViewById(R.id.Player1);
				Button b2 = (Button)findViewById(R.id.Player2);
				Button pp = (Button)findViewById(R.id.Pause);
				
				/** Set P1's button and clock text to red */
				p1.setTextColor(Color.RED);
				b2.setBackgroundColor(Color.RED);
				
				b1.setClickable(false);
				b2.setClickable(false);
				pp.setClickable(false);
				
				/** Blink the clock display */
				myHandler.removeCallbacks(mUpdateTimeTask2);
				myHandler.postDelayed(Blink, 500);
				return;
				
			}
			
			/** 
			 * Make the time display more accurately. Adding the offset
			 * ensures that, for example, when the clock displays zero
			 * the player actually has zero seconds left. Otherwise, due
			 * to int truncation, the clock says 0 at 0.9 seconds.
			 */
			int offset = timeLeft == time ? 0 : 1;
			secondsLeft += offset;
			       		
			/** Color clock yellow if we're under 1 minute */
			if ( timeLeft < 60000 ) {
				p1.setTextColor(Color.YELLOW);
			}
			
			/** Display the time, omitting leading 0's for times < 10 minutes */
			if (secondsLeft < 10) {
			    p1.setText("" + minutesLeft + ":0" + secondsLeft);
			} else {
			    p1.setText("" + minutesLeft + ":" + secondsLeft);            
			}
			     
			/** Re-post the handler so it fires in another 0.1s */
			myHandler.postDelayed(this, 100);
		}
	};
	
	/** Called when P2ClickHandler registers a click/touch event */
	private void P2Click() {
		/** Un-dim the screen */
		PowerManager pm = (PowerManager)getBaseContext().getSystemService(
                Context.POWER_SERVICE);
		pm.userActivity(1, true);
		
		/** Check if this is valid (i.e. if our time is running */
		if ( onTheClock == 2 )
			return;
				 
		/** 
		 * Register that our time is running now 
		 * and that we haven't yet received our delay
		 */
		onTheClock = 2;
		delayed = false;
		
		/** 
		 * Make the other player's button green and our
		 * button and the pause button gray.
		 */
		Button p1_button = (Button)findViewById(R.id.Player1);
		p1_button.setBackgroundColor(Color.GREEN);
		
		Button p2_button = (Button)findViewById(R.id.Player2);
		p2_button.setBackgroundColor(Color.LTGRAY);
		
		Button pp = (Button)findViewById(R.id.Pause);
		pp.setBackgroundColor(Color.LTGRAY);
		
		/** 
		 * Unregister the handler from player 1's clock and 
		 * create a new one which we register with this clock.
		 */
		myHandler.removeCallbacks(mUpdateTimeTask);
		myHandler.removeCallbacks(mUpdateTimeTask2);
		myHandler.postDelayed(mUpdateTimeTask2, 100);
	}
				
	/** Handles the "tick" event for Player 2's clock */
	private Runnable mUpdateTimeTask2 = new Runnable() {
		public void run() {
			
			/** Check for delays and apply them */
			if ( delay.equals(FISCHER) && !delayed ) {
				t_P2 += delay_time * 1000 + 100;
				delayed = true;
			} else if ( delay == BRONSTEIN ) {
				// TODO: Add Bronstein delay handling
			}
			
			/** Deduct 0.1s from P2's clock */
			t_P2 -= 100;
			long timeLeft = t_P2;
					
			/** Format for display purposes */
			int secondsLeft = (int) (timeLeft / 1000);
			int minutesLeft = secondsLeft / 60;
			secondsLeft     = secondsLeft % 60;
					       
			TextView p2 = (TextView)findViewById(R.id.t_Player2);
			
			/** Did we run out of time? */
			if ( timeLeft == 0 ) {
				timeup = true;
				Button b1 = (Button)findViewById(R.id.Player1);
				Button b2 = (Button)findViewById(R.id.Player2);
				Button pp = (Button)findViewById(R.id.Pause);
				
				/** Set P1's button and clock text to red */
				p2.setTextColor(Color.RED);
				b1.setBackgroundColor(Color.RED);
				
				b1.setClickable(false);
				b2.setClickable(false);
				pp.setClickable(false);
				
				/** Blink the clock display */
				myHandler.removeCallbacks(mUpdateTimeTask2);
				myHandler.postDelayed(Blink2, 500);
				return;		
			}
				
			/** 
			 * Make the time display more accurately. Adding the offset
			 * ensures that, for example, when the clock displays zero
			 * the player actually has zero seconds left. Otherwise, due
			 * to int truncation, the clock says 0 at 0.9 seconds.
			 */
			int offset = timeLeft == time ? 0 : 1;
			secondsLeft += offset;
			
			/** Color clock yellow if we're under 1 minute */
			if ( timeLeft < 60000) {
				p2.setTextColor(Color.YELLOW);
			}
			
			/** Display the time, omitting leading 0's for times < 10 minutes */
			if (secondsLeft < 10) {
				p2.setText("" + minutesLeft + ":0" + secondsLeft);
			} else {
				p2.setText("" + minutesLeft + ":" + secondsLeft);            
			}
					     
			/** Re-post the handler so it fires in another 0.1s */
			myHandler.postDelayed(this, 100);
		}
	};
	
	/** Blinks the clock text if Player 1's time hits 0:00 */
	private Runnable Blink = new Runnable() {
		public void run() {
			TextView p1 = (TextView)findViewById(R.id.t_Player1);
			
			/**
			 * Display the clock if it's blank, or blank it if
			 * it's currently displayed.
			 */
			if ( !blink ) {
				blink = true;
				p1.setText("");
			} else {
				blink = false;
				p1.setText("0:00");
			}
			
			/** Register a handler to fire again in 0.5s */
			myHandler.postDelayed(this, 500);	
		}
	};
	
	/** Blinks the clock text if Player 2's time hits 0:00 */
	private Runnable Blink2 = new Runnable() {
		public void run() {
			TextView p2 = (TextView)findViewById(R.id.t_Player2);
			
			/**
			 * Display the clock if it's blank, or blank it if
			 * it's currently displayed.
			 */
			if ( !blink ) {
				blink = true;
				p2.setText("");
			} else {
				blink = false;
				p2.setText("0:00");
			}
			
			/** Register a handler to fire again in 0.5s */
			myHandler.postDelayed(this, 500);	
		}
	};
	
	/** 
	 * Pauses both clocks. This is called when the options
	 * menu is opened, since the game needs to pause
	 * but not un-pause, whereas PauseToggle() will switch
	 * back and forth between the two.
	 *  */
	private void PauseGame() {
		Button p1 = (Button)findViewById(R.id.Player1);
		Button p2 = (Button)findViewById(R.id.Player2);
		Button pp = (Button)findViewById(R.id.Pause);
		
		/** Save the currently running clock, then pause */
		if ( ( onTheClock != 0 ) && ( !timeup ) ) {
			savedOTC = onTheClock;
			onTheClock = 0;
			
			p1.setBackgroundColor(Color.LTGRAY);
			p2.setBackgroundColor(Color.LTGRAY);
			pp.setBackgroundColor(Color.BLUE);
		
			myHandler.removeCallbacks(mUpdateTimeTask);
			myHandler.removeCallbacks(mUpdateTimeTask2);
		}
	}
	
	/** Called when the pause button is clicked */
	private void PauseToggle() {
		Button p1 = (Button)findViewById(R.id.Player1);
		Button p2 = (Button)findViewById(R.id.Player2);
		Button pp = (Button)findViewById(R.id.Pause);
		
		/** Figure out if we need to pause or unpause */
		if ( onTheClock != 0 ) {
			savedOTC = onTheClock;
			onTheClock = 0;
			
			p1.setBackgroundColor(Color.LTGRAY);
			p2.setBackgroundColor(Color.LTGRAY);
			pp.setBackgroundColor(Color.BLUE);
		
			myHandler.removeCallbacks(mUpdateTimeTask);
			myHandler.removeCallbacks(mUpdateTimeTask2);
		} else {
			Log.v(TAG, "Info: Unpausing.");
			if ( savedOTC == 1 ) {
				P1Click();
			} else if ( savedOTC == 2 ) {
				P2Click();
			} else {
				return;
			}
		}
	}
	
	/** Set up (or refresh) all game parameters */
	private void SetUpGame() {				

		/** Get the wakelock */
	    wl.acquire();
	    
	    /** Load all stored preferences */
	    SharedPreferences prefs = PreferenceManager
    	.getDefaultSharedPreferences(this);
	    
		delay = prefs.getString("prefDelay","None");
		Log.v("INFO", "INFO: Got preference (" + delay + ").");

		time = Integer.parseInt( prefs.getString("prefTime", "10") );
		Log.v("INFO", "INFO: Got preference (" + time + ").");
		
		delay_time = Integer.parseInt( prefs.getString("prefDelayTime", "0") );
		Log.v("INFO", "INFO: Got preference (" + delay_time + ").");
		
		/** Set time equal to minutes * ms per minute */
		t_P1 = time * 60000;
		t_P2 = time * 60000;
		
		/** Set up the buttons */
		TextView p1 = (TextView)findViewById(R.id.t_Player1);
        TextView p2 = (TextView)findViewById(R.id.t_Player2);
        p1.setTextColor(Color.LTGRAY);
        p2.setTextColor(Color.LTGRAY);

        Button p1_button = (Button)findViewById(R.id.Player1);
        Button p2_button = (Button)findViewById(R.id.Player2);
        
        Button pause = (Button)findViewById(R.id.Pause);
        pause.setBackgroundColor(Color.LTGRAY);
        
        p1_button.setBackgroundColor(Color.LTGRAY);
        p2_button.setBackgroundColor(Color.LTGRAY);
               
        /** Format and display the clocks */
        int secondsLeft = (int) (t_P1 / 1000);
	    int minutesLeft = secondsLeft / 60;
	    secondsLeft     = secondsLeft % 60;
	    
	    int secondsLeft2 = (int) (t_P2 / 1000);
	    int minutesLeft2 = secondsLeft2 / 60;
	    secondsLeft2     = secondsLeft2 % 60;
	    
	    if (secondsLeft < 10) {
	        p1.setText("" + minutesLeft + ":0" + secondsLeft);
	    } else {
	        p1.setText("" + minutesLeft + ":" + secondsLeft);            
	    }
	    
	    if (secondsLeft < 10) {
	        p2.setText("" + minutesLeft2 + ":0" + secondsLeft2);
	    } else {
	        p2.setText("" + minutesLeft2 + ":" + secondsLeft2);            
	    }	    
	     
	    /** 
	     * Register the click listeners and unregister any
	     * text blinking timers that may exist.
	     */
        p1_button.setOnClickListener(P1ClickHandler);
        p2_button.setOnClickListener(P2ClickHandler);
        pause.setOnClickListener(PauseListener);
        myHandler.removeCallbacks(Blink);
        myHandler.removeCallbacks(Blink2);
        
	}
}
    