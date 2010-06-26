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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChessClock extends Activity {
	
	public static final String TAG = "INFO";
	
	private long t_P1 = 600000;
	private long t_P2 = 600000;
	private int onTheClock = 0;
	private int savedOTC = 0;
	private boolean blink = false;
	private Handler myHandler = new Handler();
	
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
		Log.v(TAG, "Made handler.");
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
		Log.v(TAG, "Made handler (P2).");
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
			
			Button p1 = (Button)findViewById(R.id.Player1);
			Button p2 = (Button)findViewById(R.id.Player2);
			Button pp = (Button)findViewById(R.id.Pause);
			
			if ( onTheClock != 0 ) {
				Log.v(TAG, "Info: Pausing.");
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
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        
        setContentView(R.layout.main);
        
        TextView p1 = (TextView)findViewById(R.id.t_Player1);
        TextView p2 = (TextView)findViewById(R.id.t_Player2);
        
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
        
        Log.v(TAG, "Info: End of onCreate");
        
    }
}
    