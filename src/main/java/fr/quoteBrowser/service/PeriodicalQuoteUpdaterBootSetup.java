package fr.quoteBrowser.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PeriodicalQuoteUpdaterBootSetup extends BroadcastReceiver {
	private static String TAG = "quoteBrowser";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			QuoteUtils.scheduleDatabaseUpdate(context,false,true);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);

		}

	}

}
