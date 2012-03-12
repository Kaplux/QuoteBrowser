package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import fr.quoteBrowser.Quote;

public class QuotePager {

	private static QuotePager instance = null;

	private int currentPage = -1;

	private QuoteProviderService service;

	private QuotePager(Context context) {
		super();
		service = QuoteProviderService.getInstance(context);
	}

	public static QuotePager getInstance(Context context) {
		if (instance == null) {
			instance = new QuotePager(context);
		}
		return instance;
	}
	
	public List<Quote> getNextQuotePage() throws IOException{
		List <Quote> quotes=service.getQuotesFromPage(currentPage+1);
		currentPage++;
		return quotes;
	}
	
	public List<Quote> getPreviousQuotePage() throws IOException{
		int previousPage=currentPage>0?currentPage-1:0;
		List <Quote> quotes=service.getQuotesFromPage(previousPage);
		currentPage=previousPage;
		return quotes;
	}

	public Collection<? extends Quote> reloadQuotePage() throws IOException {
		return service.getQuotesFromPage(currentPage);
	}

	public int getCurrentPage() {
		return currentPage;
	}

}
