package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import fr.quoteBrowser.Quote;

public class QuotePager {

	private static QuotePager instance = null;
	
	private DatabaseHelper databaseHelper;
	
	private List<Quote> quotes;

	private static final String TAG = "quoteBrowser";

	private int currentPage = 0;
	
	private static final int NUMBER_OF_QUOTES_PER_PAGE=25;
	
	private QuotePager(Context context) {
		super();
		databaseHelper = new DatabaseHelper(context,
				"QUOTES.db", null, 1);
		Intent intent = new Intent(context, QuoteProviderService.class);
		context.startService(intent);
		
	}

	public static QuotePager getInstance(Context context) {
		if (instance == null) {
			instance = new QuotePager(context);
		}
		return instance;
	}

	public List<Quote> getNextQuotePage() throws IOException {
		Log.d(TAG,"trying to display page "+(currentPage+1));
		quotes= DatabaseHelper.getQuotes(databaseHelper.getReadableDatabase());
		return quotes;
	}

	public List<Quote> getPreviousQuotePage() throws IOException {
		int previousPage = currentPage > 1 ? currentPage - 1 : 1;
		Log.d(TAG,"trying to display page "+previousPage);
		quotes= DatabaseHelper.getQuotes(databaseHelper.getReadableDatabase());
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
