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

	public List<Quote> getQuotePageFromCache(int pageNumber)
			throws IOException {
		cachePage(pageNumber);
		for (int i = 0; i < PAGE_PREFETCH_NUMBER; i++) {
			cachePage(pageNumber + i);
			cachePage(pageNumber - i);
		}

		try {
			return pageCache.get(pageNumber).get();
		} catch (InterruptedException e) {
			Log.e(TAG,e.getMessage(),e);
		} catch (ExecutionException e) {
			Log.e(TAG,e.getMessage(),e);
		}
		return new ArrayList<Quote>();
	}

	public void invalidateCache() {
		pageCache.clear();
	}

	private void cachePage(final int pageNumber) {

		if (pageNumber >= 0 && !pageCache.containsKey(pageNumber)) {

			Callable<List<Quote>> quotePageRequest = new Callable<List<Quote>>() {

				@Override
				public List<Quote> call() throws Exception {
					return service.getQuotesFromPage(pageNumber);
				}
			};
			pageCache.put(pageNumber, executor.submit(quotePageRequest));

		}
	}

	public void remove(int pageNumber) {
		pageCache.remove(pageNumber);
		
	}

}
