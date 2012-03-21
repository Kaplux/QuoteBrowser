package fr.quoteBrowser.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import fr.quoteBrowser.R;
import fr.quoteBrowser.activity.BrowseQuotesActivity;
import fr.quoteBrowser.service.QuoteIndexer.FetchType;

public class QuoteIndexationService extends IntentService {

	public QuoteIndexationService() {
		super("QuoteProviderService");
	}

	private static String TAG = "quoteBrowser";

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Starting quote indexing service");
		int nbQuotesAdded = new QuoteIndexer(getApplicationContext())
				.index(FetchType.INCREMENTAL);
		if (Preferences.getInstance(getApplicationContext())
				.databaseNotificationEnabled()) {
			sendNotification(nbQuotesAdded);
		}
		Log.i(TAG, "Quote indexing service ended.");
	}

	private void sendNotification(int nbQuotesAdded) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		int icon = R.drawable.icon;
		CharSequence tickerText = "Quote database updated. " + nbQuotesAdded
				+ " quotes added.";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		Context context = getApplicationContext();
		CharSequence contentTitle = "Quote Database updated";
		CharSequence contentText = nbQuotesAdded
				+ " quotes added. Touch to open Quote Browser";
		Intent notificationIntent = new Intent(this, BrowseQuotesActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		int notificationId = 1;

		mNotificationManager.notify(notificationId, notification);
	}

}
