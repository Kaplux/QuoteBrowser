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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.QuoteProvider;

public class QuoteIndexer {

	private static final int NUMBER_OF_PAGES_TO_FETCH = 1;
	private static String TAG = "quoteBrowser";
	

	private ExecutorService executor = Executors.newCachedThreadPool();
	private Context context;

	public QuoteIndexer(Context context){
		this.context=context;
	}
	
	public void index() {
		final DatabaseHelper databaseHelper = new DatabaseHelper(context, "QUOTES.db", null, 1);

		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		final List<Quote> loadedQuotes = new ArrayList<Quote>();
		try {
			loadedQuotes.addAll(DatabaseHelper.getQuotes(db));
		} finally {
			db.close();
		}
		List<Future<List<Quote>>> fetchResults = new ArrayList<Future<List<Quote>>>();
		for (final QuoteProvider p : QuoteUtils.PROVIDERS) {
			fetchResults.add(executor.submit(new Callable<List<Quote>>() {
				@Override
				public List<Quote> call() throws Exception {
					return fetchQuotesFromProvider(loadedQuotes, p);
				}
			}));
		}
		List<Quote> results = new ArrayList<Quote>();
		for (Future<List<Quote>> fetchResult : fetchResults) {
			try {
				results.addAll(fetchResult.get());
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (ExecutionException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}

		db = databaseHelper.getWritableDatabase();
		try {
			DatabaseHelper.putQuotes(db, results);
			Log.d(TAG,"Added " + results.size()
			+ " quotes");
		} finally {
			db.close();
		}

	}

	private List<Quote> fetchQuotesFromPage(final int pageNumber,
			final QuoteProvider provider) throws IOException {
		Log.d(TAG, "fetching page " + pageNumber + " for provider "
				+ provider.getPreferencesDescription().getTitle());

		List<Quote> quotes = new ArrayList<Quote>();

		List<Quote> newQuotes = provider.getQuotesFromPage(pageNumber);
		quotes.addAll(newQuotes);

		Log.d(TAG, "done fetching page " + pageNumber + " for provider "
				+ provider.getPreferencesDescription().getTitle());
		return quotes;

	}

	protected List<Quote> fetchQuotesFromProvider(List<Quote> loadedQuotes,
			final QuoteProvider p) {
		List<Quote> result = new ArrayList<Quote>();
		boolean databaseAlreadyContainsQuote = false;
		for (int i = 0; i < NUMBER_OF_PAGES_TO_FETCH
				&& !databaseAlreadyContainsQuote; i++) {
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
						result.add(q);
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

		return result;
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
