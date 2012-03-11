package fr.quoteBrowser;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class QuotePreferencesActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	 
	addPreferencesFromResource(R.xml.preferences);
	}
	
}
