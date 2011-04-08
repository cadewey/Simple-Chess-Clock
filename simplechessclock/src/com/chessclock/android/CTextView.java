/*************************************************************************
 * File: CTextView.java
 * 
 * Implements a custom text view for drawing upside down.
 * 
 * Created: 6/24/2010
 * 
 * Author: Carter Dewey
 * 
 * OnDraw() code was seen on StackOverflow, originally posted by Ravedave:
 *     http://stackoverflow.com/users/92212/ravedave
 * 
 * Original thread: http://stackoverflow.com/questions/2558257/
 * 		how-can-you-display-upside-down-text-with-a-textview-in-android
 * 
 *************************************************************************
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 
 * 3.0 Unported License. To view a copy of this license, visit 
 * http://creativecommons.org/licenses/by-sa/3.0/ or send a letter to 
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 
 * 94105, USA.
 *
 *************************************************************************/

package com.chessclock.android;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

public class CTextView extends TextView {

    public CTextView(Context context) {
        super(context);
    }

    public CTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //Save the current canvas
        canvas.save(); 

        //Rotate the canvas (around the center of the text)
        float py = this.getHeight()/2.0f;
        float px = this.getWidth()/2.0f;
        canvas.rotate(180, px, py);
        
        super.onDraw(canvas); 

        //Restore the old canvas 
        canvas.restore(); 
    }
}