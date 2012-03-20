package fr.quoteBrowser.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class QuoteIndexationService extends IntentService {


	public QuoteIndexationService() {
		super("QuoteProviderService");
	}

	private static String TAG = "quoteBrowser";


	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Starting quote indexing service");
		new QuoteIndexer(getApplicationContext()).index();
		Log.i(TAG, "Quote indexing service ended.");
	}


}
