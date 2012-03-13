package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
			30);

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

	public List<Quote> getQuotesFromPage(final int pageNumber)
			throws IOException {
		Log.d(TAG, "loading page " + pageNumber);
		final List<Quote> quotes = Collections
				.synchronizedList(new ArrayList<Quote>());

		ArrayList<Future<Void>> futures = new ArrayList<Future<Void>>();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		for (final QuoteProvider provider : providers) {

			boolean providerEnabled = prefs.getBoolean(provider
					.getPreferencesDescription().getKey(), true);
			final boolean colorizeUsernames = prefs.getBoolean(
					"colorize_usernames_preference", true);
			if (providerEnabled) {
				futures.add(executor.submit(new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						List<Quote> newQuotes = provider
								.getQuotesFromPage(pageNumber);
						if (colorizeUsernames
								&& provider.supportsUsernameColorization()) {
							quotes.addAll(QuoteProviderUtils
									.colorizeUsernames(newQuotes));
						} else {
							quotes.addAll(newQuotes);
						}
						Log.d(TAG, "provider "
								+ provider.getPreferencesDescription()
										.getTitle() + " done loading page "
								+ pageNumber);
						return null;
					}
				}));
			}
		}

		for (Future<Void> f : futures) {
			try {
				f.get();
			} catch (InterruptedException e) {
				throw new IOException(e);
			} catch (ExecutionException e) {
				throw new IOException(e);
			}
		}
		
		Log.d(TAG, "loaded page " + pageNumber);
		return quotes;

	}

	public List<QuoteProviderPreferencesDescription> getQuoteProvidersPreferences() {
		List<QuoteProviderPreferencesDescription> result = new ArrayList<QuoteProviderPreferencesDescription>();
		for (QuoteProvider qp : providers) {
			result.add(qp.getPreferencesDescription());
		}
		return result;
	}

}
