package com.android.chessclock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.widget.Toast;
 
public class Prefs extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    Log.v("INFO", "INFO: Read prefs.xml");
	    addPreferencesFromResource(R.xml.preferences);
	    Log.v("INFO", "INFO: Finished onCreate");
	}

}
