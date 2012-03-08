package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.List;

import fr.quoteBrowser.BashDotOrgQuoteProvider;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.QuoteProvider;

public class BrowseQuotesActivity extends AbstractQuoteListActivity {

	private int quotePage=0;
	
	@Override
	protected List<Quote> getQuotes() throws IOException {
		QuoteProvider qp=new BashDotOrgQuoteProvider();
		return qp.getQuotesFromPage(quotePage);
	}

}
