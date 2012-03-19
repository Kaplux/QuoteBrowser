package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.QuoteProvider;

public class QuoteIndexationService extends IntentService {
	public QuoteIndexationService() {
		super("QuoteProviderService");

	}

	private static String TAG = "quoteBrowser";
	private ExecutorService executor = Executors.newCachedThreadPool();

	private List<Quote> fetchQuotesFromPage(final int pageNumber,
			final QuoteProvider provider) throws IOException {
		Log.d(TAG, "fetching page " + pageNumber + " for provider "
				+ provider.getPreferencesDescription().getTitle());

		List<Quote> quotes = new ArrayList<Quote>();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		final boolean colorizeUsernames = prefs.getBoolean(
				"colorize_usernames_preference", true);

		List<Quote> newQuotes = provider.getQuotesFromPage(pageNumber);
		if (colorizeUsernames && provider.supportsUsernameColorization()) {
			newQuotes = QuoteUtils.colorizeUsernames(newQuotes);
		}
		quotes.addAll(newQuotes);

		Log.d(TAG, "done fetching page " + pageNumber + " for provider "
				+ provider.getPreferencesDescription().getTitle());
		return quotes;

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(TAG, "Starting quote indexing service");
		final DatabaseHelper databaseHelper = new DatabaseHelper(
				getApplicationContext(), "QUOTES.db", null, 1);
		int numberOfQuotesAdded = 0;
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		try {
			final List<Quote> loadedQuotes = DatabaseHelper.getQuotes(db);

			List<Future<Integer>> fetchResults = new ArrayList<Future<Integer>>();
			for (final QuoteProvider p : QuoteUtils.PROVIDERS) {
				executor.submit(new Callable<Integer>() {

					@Override
					public Integer call() throws Exception {
						return fetchQuotesFromProvider(databaseHelper,
								loadedQuotes, p);
					}
				});

				for (Future<Integer> fetchResult : fetchResults) {
					try {
						numberOfQuotesAdded += fetchResult.get();
					} catch (InterruptedException e) {
						Log.e(TAG, e.getMessage(), e);
					} catch (ExecutionException e) {
						Log.e(TAG, e.getMessage(), e);
					}
				}
			}
			Log.i(TAG, "Quote indexing service ended. Added "
					+ numberOfQuotesAdded + " quotes");
		} finally {
			db.close();
		}

	}

	protected int fetchQuotesFromProvider(DatabaseHelper databaseHelper,
			List<Quote> loadedQuotes, final QuoteProvider p) {
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		int numberOfQuotesAdded = 0;
		try {
			boolean databaseAlreadyContainsQuote = false;
			for (int i = 0; i < 1 && !databaseAlreadyContainsQuote; i++) {
				try {
					Log.d(TAG, "fetching quote page " + i + "from provider "
							+ p.getPreferencesDescription().getTitle());
					List<Quote> potentialQuotesToAdd = fetchQuotesFromPage(i, p);
					Log.d(TAG, potentialQuotesToAdd.size()
							+ " quotes fetched from provider");
					for (Quote q : potentialQuotesToAdd) {
						if (!quoteAlreadyInList(q, loadedQuotes)) {
							Log.d(TAG, "Adding new quote from page " + i
									+ " of provider "
									+ p.getPreferencesDescription().getTitle());
							DatabaseHelper.putQuote(db, q);
							numberOfQuotesAdded++;
						} else {
							databaseAlreadyContainsQuote = true;
							Log.d(TAG,
									"Quote database already contains quote from page "
											+ i
											+ " of provider "
											+ p.getPreferencesDescription()
													.getTitle());
						}
					}

				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		} finally {
			db.close();
		}
		return numberOfQuotesAdded;
	}

	private boolean quoteAlreadyInList(final Quote q, List<Quote> loadedQuotes) {
		return CollectionUtils.exists(loadedQuotes, new Predicate() {
			@Override
			public boolean evaluate(Object loadedQuote) {
				return q.getQuoteTextMD5().equals(
						((Quote) loadedQuote).getQuoteTextMD5());
			}
		});
	}
}
