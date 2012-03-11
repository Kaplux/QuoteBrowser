package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.List;

import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.QuoteProviderService;

public class LatestQuotesActivity extends AbstractQuoteListActivity {
	
	
	
	@Override
	protected List<Quote> getQuotes() throws IOException {
		return QuoteProviderService.getInstance(getApplicationContext()).getLatestQuotes();
	}
		

}
