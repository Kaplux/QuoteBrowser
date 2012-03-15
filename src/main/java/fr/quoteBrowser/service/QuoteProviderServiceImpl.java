package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.ArrayList;
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
import fr.quoteBrowser.service.provider.QdbDotUsQuoteProvider;
import fr.quoteBrowser.service.provider.QuoteProvider;
import fr.quoteBrowser.service.provider.QuoteProviderPreferencesDescription;
import fr.quoteBrowser.service.provider.SeenOnSlashDotComQuoteProvider;
import fr.quoteBrowser.service.provider.XKCDBDotComQuoteProvider;

public class QuoteProviderServiceImpl implements QuoteProviderService {
	private static String TAG = "quoteBrowser";
	private static final QuoteProvider[] providers = new QuoteProvider[] {
			new BashDotOrgQuoteProvider(),new QdbDotUsQuoteProvider(), new XKCDBDotComQuoteProvider(),
			new FMyLifeDotComQuoteProvider(),new SeenOnSlashDotComQuoteProvider() };
	private Context context;
	private static QuoteProviderService instance = null;
	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
			30);

	private QuoteProviderServiceImpl(Context context) {
		super();
		this.context = context;
	}

	public static QuoteProviderService getInstance(Context context) {
		if (instance == null) {
			instance = new QuoteProviderServiceImpl(context);
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.quoteBrowser.service.QuoteProviderService#getQuotesFromPage(int)
	 */
	@Override
	public List<Quote> getQuotesFromPage(final int pageNumber)
			throws IOException {
		Log.d(TAG, "loading page " + pageNumber);

		ArrayList<Future<List<Quote>>> futures = new ArrayList<Future<List<Quote>>>();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		for (final QuoteProvider provider : providers) {

			boolean providerEnabled = prefs.getBoolean(provider
					.getPreferencesDescription().getKey(), provider.getPreferencesDescription().isEnabledByDefault());
			final boolean colorizeUsernames = prefs.getBoolean(
					"colorize_usernames_preference", true);
			if (providerEnabled) {
				futures.add(executor.submit(new Callable<List<Quote>>() {
					@Override
					public List<Quote> call() throws Exception {
						List<Quote> newQuotes = provider
								.getQuotesFromPage(pageNumber);
						if (colorizeUsernames
								&& provider.supportsUsernameColorization()) {
							newQuotes = QuoteUtils
									.colorizeUsernames(newQuotes);
						}
						Log.d(TAG, "provider "
								+ provider.getPreferencesDescription()
										.getTitle() + " done loading page "
								+ pageNumber);
						return newQuotes;
					}
				}));
			}
		}

		final List<Quote> quotes = new ArrayList<Quote>();
		for (Future<List<Quote>> f : futures) {
			try {
				quotes.addAll(f.get());
			} catch (InterruptedException e) {
				throw new IOException(e);
			} catch (ExecutionException e) {
				throw new IOException(e);
			}
		}

		Log.d(TAG, "loaded page " + pageNumber);
		return quotes;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.quoteBrowser.service.QuoteProviderService#getQuoteProvidersPreferences
	 * ()
	 */
	@Override
	public List<QuoteProviderPreferencesDescription> getQuoteProvidersPreferences() {
		List<QuoteProviderPreferencesDescription> result = new ArrayList<QuoteProviderPreferencesDescription>();
		for (QuoteProvider qp : providers) {
			result.add(qp.getPreferencesDescription());
		}
		return result;
	}

}
