/*************************************************************************
 * File: Main.java
 * 
 * Implements the main form/class for Chess Clock.
 * 
 * Created: 6/22/2010
 * 
 * Author: Carter Dewey
 * 
 *************************************************************************
 *
 *    This file is part of Simple Chess Clock (SCC).
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
	
	public static final String TAG = "INFO";
	public static final String V_MAJOR = "0";
	public static final String V_MINOR = "5";
	public static final String V_MINI = "1";

	private static final int SETTINGS = 0;
	private static final int RESET = 1;
	private static final int ABOUT = 2;
	
	private long t_P1;
	private long t_P2;
	private int onTheClock = 0;
	private int savedOTC = 0;
	private boolean blink = false;
	private boolean timeup = false;
	private Handler myHandler = new Handler();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        						WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.main);
        
        SetUpGame();
        
    }
    
    public boolean onPrepareOptionsMenu(Menu menu) {
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
	
	private void showPrefs() {
		Intent prefsActivity = new Intent(ChessClock.this, Prefs.class);
		startActivity(prefsActivity);
	}
	
	public void onWindowFocusChanged(boolean b) {
		if (b)
			SetUpGame();
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = new Dialog(this);
		switch ( id ) {
			case ABOUT:
				dialog = AboutDialog();
				break;
			case RESET:
				dialog = ResetDialog();
				break;
		}
		
		return dialog;
	}
	
	private Dialog AboutDialog() {
		Dialog d = new Dialog(this);
		
		d.setContentView(R.layout.about_dialog);
		d.setTitle("Simple Chess Clock (SCC) v"
				+ V_MAJOR + "."
				+ V_MINOR + "."
				+ V_MINI);

		TextView text = (TextView) d.findViewById(R.id.text);
		text.setText("Design/Coding: Carter Dewey\n"
				+ "Copyright (c) Carter Dewey, 2010\n\n"
				+ "SCC is free software licensed under the "
				+ "GNU GPLv3. You can view the GPLv3 at\n"
				+ "http://www.gnu.org/licenses/gpl-3.0.html\n\n"
				+ "To report bugs or view source code, visit:\n"
				+ "http://code.google.com/p/simplechessclock/");
		
		return d;
	}
	
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
	
	public OnClickListener P1ClickHandler = new OnClickListener() {
		public void onClick(View v) {
			P1Click();
		}
	};
	
	private void P1Click() {
		Log.v(TAG, "Info: Got click.");
		if ( onTheClock == 1 )
			return;
			   
		onTheClock = 1;
		Button p2_button = (Button)findViewById(R.id.Player2);
		p2_button.setBackgroundColor(Color.GREEN);
			   
		Button p1_button = (Button)findViewById(R.id.Player1);
		p1_button.setBackgroundColor(Color.LTGRAY);
			   
		Button pp = (Button)findViewById(R.id.Pause);
		pp.setBackgroundColor(Color.LTGRAY);
			   
		myHandler.removeCallbacks(mUpdateTimeTask);
		myHandler.removeCallbacks(mUpdateTimeTask2);
		myHandler.postDelayed(mUpdateTimeTask, 100);
	}
		
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			t_P1 -= 100;
			long timeLeft = t_P1;
				    
			int secondsLeft = (int) (timeLeft / 1000);
			int minutesLeft = secondsLeft / 60;
			secondsLeft     = secondsLeft % 60;
			       
			TextView p1 = (TextView)findViewById(R.id.t_Player1);
			
			if ( timeLeft == 0 ) {
				timeup = true;
				Button b1 = (Button)findViewById(R.id.Player1);
				Button b2 = (Button)findViewById(R.id.Player2);
				Button pp = (Button)findViewById(R.id.Pause);
				
				p1.setTextColor(Color.RED);
				b2.setBackgroundColor(Color.RED);
				
				b1.setClickable(false);
				b2.setClickable(false);
				pp.setClickable(false);
				
				myHandler.removeCallbacks(mUpdateTimeTask2);
				myHandler.postDelayed(Blink, 500);
				return;
				
			}
			       		
			if ( timeLeft <= 60000 ) {
				p1.setTextColor(Color.YELLOW);
			}
			
			if (secondsLeft < 10) {
			    p1.setText("" + minutesLeft + ":0" + secondsLeft);
			} else {
			    p1.setText("" + minutesLeft + ":" + secondsLeft);            
			}
			     
			myHandler.postDelayed(this, 100);
		}
	};
			
	public OnClickListener P2ClickHandler = new OnClickListener() {
		public void onClick(View v) {
			P2Click();
		}
	};
	
	private Runnable Blink = new Runnable() {
		public void run() {
			TextView p1 = (TextView)findViewById(R.id.t_Player1);
			
			if ( !blink ) {
				blink = true;
				p1.setText("");
			} else {
				blink = false;
				p1.setText("0:00");
			}
			
			myHandler.postDelayed(this, 500);	
		}
	};
	
	private Runnable Blink2 = new Runnable() {
		public void run() {
			TextView p2 = (TextView)findViewById(R.id.t_Player2);
			
			if ( !blink ) {
				blink = true;
				p2.setText("");
			} else {
				blink = false;
				p2.setText("0:00");
			}
			
			myHandler.postDelayed(this, 500);	
		}
	};
	
	private void P2Click() {
		Log.v(TAG, "Info: Got click.");
		if ( onTheClock == 2 )
			return;
				   
		onTheClock = 2;
		Button p1_button = (Button)findViewById(R.id.Player1);
		p1_button.setBackgroundColor(Color.GREEN);
		
		Button p2_button = (Button)findViewById(R.id.Player2);
		p2_button.setBackgroundColor(Color.LTGRAY);
		
		Button pp = (Button)findViewById(R.id.Pause);
		pp.setBackgroundColor(Color.LTGRAY);
		
		myHandler.removeCallbacks(mUpdateTimeTask);
		myHandler.removeCallbacks(mUpdateTimeTask2);
		myHandler.postDelayed(mUpdateTimeTask2, 100);
	}
				
	private Runnable mUpdateTimeTask2 = new Runnable() {
		public void run() {
			t_P2 -= 100;
			long timeLeft = t_P2;
						   
			int secondsLeft = (int) (timeLeft / 1000);
			int minutesLeft = secondsLeft / 60;
			secondsLeft     = secondsLeft % 60;
					       
			TextView p2 = (TextView)findViewById(R.id.t_Player2);
			
			if ( timeLeft == 0 ) {
				timeup = true;
				Button b1 = (Button)findViewById(R.id.Player1);
				Button b2 = (Button)findViewById(R.id.Player2);
				Button pp = (Button)findViewById(R.id.Pause);
				
				p2.setTextColor(Color.RED);
				b1.setBackgroundColor(Color.RED);
				
				b1.setClickable(false);
				b2.setClickable(false);
				pp.setClickable(false);
				
				myHandler.removeCallbacks(mUpdateTimeTask2);
				myHandler.postDelayed(Blink2, 500);
				return;		
			}
					       
			if ( timeLeft <= 60000) {
				p2.setTextColor(Color.YELLOW);
			}
			
			if (secondsLeft < 10) {
				p2.setText("" + minutesLeft + ":0" + secondsLeft);
			} else {
				p2.setText("" + minutesLeft + ":" + secondsLeft);            
			}
					     
			myHandler.postDelayed(this, 100);
		}
	};
	
	public OnClickListener PauseListener = new OnClickListener() {
		public void onClick(View v) {
			PauseToggle();
		}
	};
	
	private void PauseGame() {
		Button p1 = (Button)findViewById(R.id.Player1);
		Button p2 = (Button)findViewById(R.id.Player2);
		Button pp = (Button)findViewById(R.id.Pause);
		
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
	
	private void PauseToggle() {
		Button p1 = (Button)findViewById(R.id.Player1);
		Button p2 = (Button)findViewById(R.id.Player2);
		Button pp = (Button)findViewById(R.id.Pause);
		
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
	
	private void SetUpGame() {
		String timePref;
		int start_time;
		
		SharedPreferences prefs = PreferenceManager
        	.getDefaultSharedPreferences(this);
		
		timePref = prefs.getString("prefTime", "10");
		Log.v("INFO", "INFO: Got preference (" + timePref + ").");

		start_time = Integer.parseInt(timePref);
		
		t_P1 = start_time * 60000;
		t_P2 = start_time * 60000;
		
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
	     
        p1_button.setOnClickListener(P1ClickHandler);
        p2_button.setOnClickListener(P2ClickHandler);
        pause.setOnClickListener(PauseListener);
        myHandler.removeCallbacks(Blink);
        myHandler.removeCallbacks(Blink2);
        
	}
}
    