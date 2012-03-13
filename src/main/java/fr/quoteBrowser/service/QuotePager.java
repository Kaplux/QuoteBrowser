package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;
import fr.quoteBrowser.Quote;

public class QuotePager {

	private static QuotePager instance = null;

	private static final String TAG = "quoteBrowser";

	private int currentPage = -1;
	
	private QuoteCache cache;

	private QuotePager(Context context) {
		super();
		cache=new QuoteCache(context);
	}

	public static QuotePager getInstance(Context context) {
		if (instance == null) {
			instance = new QuotePager(context);
		}
		return instance;
	}

	public List<Quote> getNextQuotePage() throws IOException {
		Log.d(TAG,"trying to display page "+(currentPage+1));
		List<Quote> quotes = cache.getQuotePageFromCache(currentPage + 1);
		currentPage++;
		return quotes;
	}

	public List<Quote> getPreviousQuotePage() throws IOException {
		int previousPage = currentPage > 0 ? currentPage - 1 : 0;
		Log.d(TAG,"trying to display page "+previousPage);
		List<Quote> quotes = cache.getQuotePageFromCache(previousPage);
		currentPage = previousPage;
		return quotes;
	}

	public Collection<? extends Quote> reloadQuotePage() throws IOException {
		Log.d(TAG,"trying to reload "+currentPage);
		cache.remove(currentPage);
		return cache.getQuotePageFromCache(currentPage);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void reset() {
		cache.invalidateCache();
		currentPage=0;
	}

}
