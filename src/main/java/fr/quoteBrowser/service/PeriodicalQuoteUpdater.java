package fr.quoteBrowser.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PeriodicalQuoteUpdater extends BroadcastReceiver {
	private static String TAG = "quoteBrowser";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Intent newIntent = new Intent(context, QuoteIndexationService.class);
			context.startService(newIntent);
		} catch (Exception e) {
			Toast.makeText(
					context,
					"There was an error somewhere, but we still received an alarm",
					Toast.LENGTH_SHORT).show();
			Log.e(TAG, e.getMessage(), e);

		}

	}

}
