package fr.quoteBrowser.service;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

	private Context context;

	private SharedPreferences prefs;

	private static Preferences instance;

	public static Preferences getInstance(Context context) {
		if (instance==null){
			instance=new Preferences(context);
		}
		return instance;
	}

	private Preferences(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public void saveDisplayPreference(String value) {
		prefs.edit().putString("display_preference", value).commit();
	}

	public String getDisplayPreference() {
		return prefs.getString("display_preference", "all");
	}

	public void saveUpdateIntervalPreference(long value) {
		prefs.edit().putLong("update_period_interval", value).commit();
	}

	public long getUpdateIntervalPreference() {
		return prefs.getLong("update_period_interval", AlarmManager.INTERVAL_HALF_DAY);
	}

}
