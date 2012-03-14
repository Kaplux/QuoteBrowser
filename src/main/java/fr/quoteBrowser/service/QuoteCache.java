package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import android.content.Context;
import android.util.Log;
import fr.quoteBrowser.Quote;

public class QuoteCache {

	private static final String TAG = "quoteBrowser";

	private static final int PAGE_CACHE_SIZE = 10;

	// Number of pages to prefetch before and after current page while loading
	// it
	private static final int PAGE_PREFETCH_NUMBER = 4;

	private static final int MAX_CONCURRENT_PAGE_FETCH = 3;

	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
			MAX_CONCURRENT_PAGE_FETCH);

	private Map<Integer, Future<List<Quote>>> pageCache = Collections
			.synchronizedMap(new HashMap());

	private QuoteProviderService service;

	public QuoteCache(Context context) {
		service = QuoteProviderServiceImpl.getInstance(context);
	}

	public List<Quote> getQuotePageFromCache(int pageNumber) throws IOException {
		cachePage(pageNumber);
		for (int i = 1; i <= PAGE_PREFETCH_NUMBER; i++) {
			cachePage(pageNumber + i);
			cachePage(pageNumber - i);
		}

		try {
			return pageCache.get(pageNumber).get();
		} catch (InterruptedException e) {
			throw new IOException(e);
		} catch (ExecutionException e) {
			throw new IOException(e);
		}
	}

	public void invalidateCache() {
		pageCache.clear();
	}

	private void cachePage(final int pageNumber) {
		if (pageNumber >= 0) {

			boolean pageNeedToBeCached = false;
			if (!pageCache.containsKey(pageNumber)) {
				pageNeedToBeCached = true;
			} else {
				try {
					pageCache.get(pageNumber).get();
				} catch (Exception e) {
					pageNeedToBeCached = true;
				}
			}

			if (pageNeedToBeCached) {

				Callable<List<Quote>> quotePageRequest = new Callable<List<Quote>>() {

					@Override
					public List<Quote> call() throws Exception {
						return service.getQuotesFromPage(pageNumber);
					}
				};
				pageCache.put(pageNumber, executor.submit(quotePageRequest));
			}
		}
	}

	public void remove(int pageNumber) {
		pageCache.remove(pageNumber);

	}

}
