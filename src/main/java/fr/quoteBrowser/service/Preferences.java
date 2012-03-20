package fr.quoteBrowser.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
	
	public static void saveDisplayPreference(Context context,String value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		prefs.edit().putString("display_preference",value).commit();
	}
	
	public static String getDisplayPreference(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		return prefs.getString("display_preference", "all");
	}

}
