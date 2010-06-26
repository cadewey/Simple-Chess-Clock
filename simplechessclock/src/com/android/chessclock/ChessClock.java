/***************************************************
 * File: Main.java
 * 
 * Implements the main form/class for Chess Clock.
 * 
 * Created: 6/22/2010
 * 
 * Author: Carter Dewey
 * 
 ***************************************************/

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
    