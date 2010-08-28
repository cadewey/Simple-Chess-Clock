/*************************************************************************
 * File: Prefs.java
 * 
 * Implements the Preferences dialog.
 * 
 * Created: 6/23/2010
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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
 
public class Prefs extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    Log.v("INFO", "INFO: Read prefs.xml");
	    addPreferencesFromResource(R.xml.preferences);
	    Log.v("INFO", "INFO: Finished onCreate");
	}

}
