package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.BashDotOrgQuoteProvider;
import fr.quoteBrowser.service.provider.FMyLifeDotComQuoteProvider;
import fr.quoteBrowser.service.provider.QuoteProvider;
import fr.quoteBrowser.service.provider.QuoteProviderPreferencesDescription;
import fr.quoteBrowser.service.provider.XKCDBQuoteProvider;

public class QuoteProviderService {
	private static String TAG = "quoteBrowser";
	private static final QuoteProvider[] providers = new QuoteProvider[] {
			new BashDotOrgQuoteProvider(), new XKCDBQuoteProvider(),
			new FMyLifeDotComQuoteProvider() };
	private Context context;
	private static QuoteProviderService instance = null;

	private QuoteProviderService(Context context) {
		super();
		this.context = context;
	}

	public static QuoteProviderService getInstance(Context context) {
		if (instance == null) {
			instance = new QuoteProviderService(context);
		}
		return instance;
	}

	public List<Quote> getQuotesFromPage(final int pageNumber) throws IOException {
		final List<Quote> quotes = Collections
				.synchronizedList(new ArrayList<Quote>());

		ArrayList<Thread> threads = new ArrayList<Thread>();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		for (final QuoteProvider provider : providers) {

			boolean providerEnabled = prefs.getBoolean(
					provider.getPreferencesDescription().getKey(), true);
			final boolean colorizeUsernames = prefs.getBoolean("colorize_usernames_preference", true);
			if (providerEnabled) {
				threads.add(new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							quotes.addAll(provider.getQuotesFromPage(pageNumber));
							if (colorizeUsernames && provider.supportsUsernameColorization()){
								QuoteProviderUtils.colorizeUsernames(quotes);
							}
						} catch (IOException e) {
							Log.e(TAG, e.getMessage());
						}

					}
				}));
			}
		}

		for (Thread t : threads) {
			t.start();
		}

		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		Collections.shuffle(quotes);
		return quotes;

	}
	
	public List<QuoteProviderPreferencesDescription >getQuoteProvidersPreferences(){
		List<QuoteProviderPreferencesDescription> result=new ArrayList<QuoteProviderPreferencesDescription>();
		for (QuoteProvider qp : providers){
			result.add(qp.getPreferencesDescription());
		}
		return result;
	}

}
