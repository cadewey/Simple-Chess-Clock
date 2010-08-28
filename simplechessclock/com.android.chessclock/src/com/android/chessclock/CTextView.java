/*************************************************************************
 * File: CTextView.java
 * 
 * Implements a custom text view for drawing upside down.
 * 
 * Created: 6/24/2010
 * 
 * Author: Carter Dewey
 * 
 * Code was seen on StackOverflow, originally posted by Ravedave.
 * 
 * Original thread: http://stackoverflow.com/questions/2558257/
 * 		how-can-you-display-upside-down-text-with-a-textview-in-android
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