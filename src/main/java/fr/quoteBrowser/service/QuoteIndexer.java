package fr.quoteBrowser.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import android.content.Context;
import android.util.Log;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.QuoteProvider;

public class QuoteIndexer {

	private static String TAG = "quoteBrowser";
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	private ExecutorService executor = Executors.newCachedThreadPool();
	private Context context;

	public enum FetchType {
		COMPLETE, INCREMENTAL
	};

	public QuoteIndexer(Context context) {
		this.context = context;
	}

	public int index(final FetchType fetchType, final int startPage,
			final int numberOfPages) {
		Log.i(TAG,
				"indexing quotes fetch mode = " + fetchType + " at "
						+ sdf.format(System.currentTimeMillis()));
		DatabaseHelper db = DatabaseHelper.connect(context);
		final List<Quote> loadedQuotes = new ArrayList<Quote>();
		try {
			loadedQuotes.addAll(db.getQuotes());
		} finally {
			db.release();
		}
		Log.d(TAG, "start fetching " + sdf.format(System.currentTimeMillis()));
		List<Future<Integer>> fetchResults = new ArrayList<Future<Integer>>();
		for (final QuoteProvider p : QuoteUtils.PROVIDERS) {
			fetchResults.add(executor.submit(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					switch (fetchType) {
					case COMPLETE:
						return simultaneouslyFetchQuotesFromProvider(
								loadedQuotes, p, startPage, numberOfPages);
					case INCREMENTAL:
						return incrementallyFetchQuotesFromProvider(
								loadedQuotes, p, startPage, numberOfPages);
					default:
						return null;
					}
				}
			}));
		}

		int nbQuotesAdded = 0;
		for (Future<Integer> fetchResult : fetchResults) {
			try {
				nbQuotesAdded += fetchResult.get();
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (ExecutionException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}

		Log.i(TAG,
				"done quotes fetch mode = " + fetchType + " at "
						+ sdf.format(System.currentTimeMillis()));
		return nbQuotesAdded;
	}

	private List<Quote> fetchQuotesFromPage(final int pageNumber,
			final QuoteProvider provider) throws IOException {
		Log.d(TAG,
				"fetching page " + pageNumber + " for provider "
						+ provider.getSource() + " at "
						+ sdf.format(System.currentTimeMillis()));

		List<Quote> quotes = new ArrayList<Quote>();

		List<Quote> newQuotes = provider.getQuotesFromPage(pageNumber);
		quotes.addAll(newQuotes);

		Log.d(TAG,
				"done fetching page " + pageNumber + " for provider "
						+ provider.getSource() + " at "
						+ sdf.format(System.currentTimeMillis()));

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
	private int simultaneouslyFetchQuotesFromProvider(List<Quote> loadedQuotes,
			final QuoteProvider p, int startPage, int numberOfPages) {
		int numberOfQuotesAdded = 0;
		List<Future<List<Quote>>> futures = new ArrayList<Future<List<Quote>>>();
		for (int i = startPage; i < numberOfPages; i++) {
			final int pageNumber = i;
			futures.add(executor.submit(new Callable<List<Quote>>() {
				@Override
				public List<Quote> call() throws Exception {
					return fetchQuotesFromPage(pageNumber, p);
				}
			}));
		}
		@SuppressWarnings("unchecked")
		Collection<String> uniqueIdsOfExistingQuotes = CollectionUtils.collect(
				loadedQuotes, new Transformer() {

					@Override
					public Object transform(Object quote) {
						return ((Quote) quote).getUniqueId();
					}
				});
		for (Future<List<Quote>> pageresult : futures) {
			try {
				List<Quote> quotes = pageresult.get();
				DatabaseHelper dbHelper = DatabaseHelper.connect(context);
				try {
					for (Quote q : quotes) {
						if (!quoteAlreadyInList(q, uniqueIdsOfExistingQuotes)) {
							dbHelper.putQuote(q);
							numberOfQuotesAdded++;
						}
					}
				} finally {
					dbHelper.release();
				}
			} catch (InterruptedException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (ExecutionException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return numberOfQuotesAdded;
	}

	/**
	 * Fetch quotes page after page and stop if a quote already exists in
	 * database
	 * 
	 * @param loadedQuotes
	 * @param p
	 * @return
	 */
	private int incrementallyFetchQuotesFromProvider(List<Quote> loadedQuotes,
			final QuoteProvider p, int startPage, int numberOfPages) {
		int numberOfQuotesAdded = 0;
		boolean databaseAlreadyContainsQuote = false;
		@SuppressWarnings("unchecked")
		Collection<String> uniqueIdsOfExistingQuotes = CollectionUtils.collect(
				loadedQuotes, new Transformer() {

					@Override
					public Object transform(Object quote) {
						return ((Quote) quote).getUniqueId();
					}
				});
		List<Quote> potentialQuotesToAdd = new ArrayList<Quote>();
		try {
			for (int i = startPage; i < numberOfPages
					&& !databaseAlreadyContainsQuote; i++) {
				potentialQuotesToAdd.addAll(fetchQuotesFromPage(i, p));
			}
			DatabaseHelper db = DatabaseHelper.connect(context);
			try {
				for (Quote q : potentialQuotesToAdd) {
					if (!quoteAlreadyInList(q, uniqueIdsOfExistingQuotes)) {
						db.putQuote(q);
						numberOfQuotesAdded++;
					} else {
						databaseAlreadyContainsQuote = true;
					}
				}
			} finally {
				db.release();
			}

		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return numberOfQuotesAdded;
	}

	private boolean quoteAlreadyInList(final Quote q,
			Collection<String> uniqueIds) {
		return (uniqueIds.contains(q.getUniqueId()));
	}

}
