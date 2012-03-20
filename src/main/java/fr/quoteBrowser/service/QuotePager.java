package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import fr.quoteBrowser.Quote;

public class QuotePager {

	private static QuotePager instance = null;

	private DatabaseHelper databaseHelper;

	private List<Quote> quotes;

	private static final String TAG = "quoteBrowser";

	private int currentPage = FIRST_PAGE_INDEX -1;

	public static final int NUMBER_OF_QUOTES_PER_PAGE = 25;
	
	public static final int FIRST_PAGE_INDEX=1;

	private QuotePager(Context context) {
		super();
		databaseHelper = new DatabaseHelper(context, "QUOTES.db", null, 1);
		
	}

	public static QuotePager getInstance(Context context) {
		if (instance == null) {
			instance = new QuotePager(context);
		}
		return instance;
	}

	public List<Quote> getNextQuotePage() throws IOException {
		loadQuotes();
		int targetPage=currentPage+1;
		List<Quote> result= getQuotePage(targetPage);
		currentPage=targetPage;
		return result;
	}
	
	public List<Quote> getPreviousQuotePage() throws IOException {
		loadQuotes();
		int targetPage = currentPage > FIRST_PAGE_INDEX ? currentPage - 1 : FIRST_PAGE_INDEX;
		List<Quote> result= getQuotePage(targetPage);
		currentPage=targetPage;
		return result;
	}

	protected List<Quote> getQuotePage(int targetPage) {
		Log.d(TAG, "trying to display page " +targetPage);
	
		int startIndex=(quotes.size()-1)-(targetPage*NUMBER_OF_QUOTES_PER_PAGE);
		if (startIndex<0){
			startIndex=0;
		}
		int endIndex=startIndex+NUMBER_OF_QUOTES_PER_PAGE*targetPage;
		if (endIndex>quotes.size()-1){
			endIndex=quotes.size()-1;
		}
		if (quotes.size()==0){
			return quotes;
		}
		return quotes.subList(startIndex, endIndex);
	}
	
	private void loadQuotes(){
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		try {
			quotes =DatabaseHelper.getQuotes(databaseHelper
					.getReadableDatabase());
		} finally {
			db.close();
		}
	}

	public Collection<? extends Quote> reloadQuotePage() throws IOException {
		Log.d(TAG, "trying to reload " + currentPage);
		return null;

	}

	public int getCurrentPage() {
		return currentPage;
	}

}
