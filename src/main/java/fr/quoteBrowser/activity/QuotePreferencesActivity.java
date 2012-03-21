package fr.quoteBrowser.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import fr.quoteBrowser.R;

public class QuotePreferencesActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}
}
