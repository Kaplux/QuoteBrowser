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
import android.util.Log;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.QuoteProvider;

public class QuoteIndexer {

	private static final int NUMBER_OF_PAGES_TO_FETCH = 5;
	private static String TAG = "quoteBrowser";

	private ExecutorService executor = Executors.newCachedThreadPool();
	private Context context;

	public enum FetchType {
		COMPLETE, INCREMENTAL
	};

	public QuoteIndexer(Context context) {
		this.context = context;
	}

	public void index(final FetchType fetchType) {

		DatabaseHelper db = DatabaseHelper.connect(context);
		final List<Quote> loadedQuotes = new ArrayList<Quote>();
		try {
			loadedQuotes.addAll(db.getQuotes());
		} finally {
			db.release();
		}
		List<Future<List<Quote>>> fetchResults = new ArrayList<Future<List<Quote>>>();
		for (final QuoteProvider p : QuoteUtils.PROVIDERS) {
			fetchResults.add(executor.submit(new Callable<List<Quote>>() {
				@Override
				public List<Quote> call() throws Exception {
					switch (fetchType) {
					case COMPLETE:
						return simultaneouslyFetchQuotesFromProvider(
								loadedQuotes, p);
					case INCREMENTAL:
						return incrementallyFetchQuotesFromProvider(
								loadedQuotes, p);
					default:
						return null;
					}
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

		db = DatabaseHelper.connect(context);
		try {
			db.putQuotes(results);
			Log.d(TAG, "Added " + results.size() + " quotes");
		} finally {
			db.release();
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

	/**
	 * fetch quotes from provider {@link QuoteIndexer#NUMBER_OF_PAGES_TO_FETCH}
	 * at a time
	 * 
	 * @param loadedQuotes
	 * @param p
	 * @return
	 */
	private List<Quote> simultaneouslyFetchQuotesFromProvider(
			List<Quote> loadedQuotes, final QuoteProvider p) {
		List<Quote> result = new ArrayList<Quote>();
		List<Future<List<Quote>>> futures = new ArrayList<Future<List<Quote>>>();
		for (int i = 0; i < NUMBER_OF_PAGES_TO_FETCH; i++) {
			final int pageNumber = i;
			futures.add(executor.submit(new Callable<List<Quote>>() {
				@Override
				public List<Quote> call() throws Exception {
					return fetchQuotesFromPage(pageNumber, p);
				}
			}));
		}
		for (Future<List<Quote>> pageresult : futures) {
			try {
				for (Quote q : pageresult.get()) {
					if (!quoteAlreadyInList(q, loadedQuotes)) {
						result.add(q);
					}
				}
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (ExecutionException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}

		return result;
	}

	/**
	 * Fetch quotes page after page and stop if a quote already exists in
	 * database
	 * 
	 * @param loadedQuotes
	 * @param p
	 * @return
	 */
	private List<Quote> incrementallyFetchQuotesFromProvider(
			List<Quote> loadedQuotes, final QuoteProvider p) {
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
