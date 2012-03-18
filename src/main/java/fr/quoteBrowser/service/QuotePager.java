package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;
import fr.quoteBrowser.Quote;

public class QuotePager {

	private static QuotePager instance = null;
	
	private QuoteProviderService service;

	private static final String TAG = "quoteBrowser";

	private int currentPage = 0;
	
	private static final int NUMBER_OF_QUOTES_PER_PAGE=25;
	
	private QuotePager(Context context) {
		super();
		service=QuoteProviderServiceImpl.getInstance(context);
	}

	public static QuotePager getInstance(Context context) {
		if (instance == null) {
			instance = new QuotePager(context);
		}
		return instance;
	}

	public List<Quote> getNextQuotePage() throws IOException {
		Log.d(TAG,"trying to display page "+(currentPage+1));
		List<Quote> quotes=service.getQuotes((currentPage+1)*50,NUMBER_OF_QUOTES_PER_PAGE);
		currentPage++;
		return quotes;
	}

	public List<Quote> getPreviousQuotePage() throws IOException {
		int previousPage = currentPage > 1 ? currentPage - 1 : 1;
		Log.d(TAG,"trying to display page "+previousPage);
		List<Quote> quotes=service.getQuotes((previousPage)*50,NUMBER_OF_QUOTES_PER_PAGE);
		currentPage=previousPage;
		return quotes;
	}

	public Collection<? extends Quote> reloadQuotePage() throws IOException {
		Log.d(TAG,"trying to reload "+currentPage);
		return null;
		
	}

	public int getCurrentPage() {
		return currentPage;
	}


}
