package fr.quoteBrowser.activity;

import java.io.IOException;

import fr.quoteBrowser.BashDotOrgQuoteProvider;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.QuoteProvider;

public class BrowseQuotesActivity extends AbstractQuoteListActivity {

	private int quotePage=0;
	
	@Override
	protected Quote[] getQuotes() throws IOException {
		QuoteProvider qp=new BashDotOrgQuoteProvider();
		Quote[] quotes;
		quotes = qp.getQuotesFromPage(0);
		return quotes;
	}

}
