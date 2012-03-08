package fr.quoteBrowser.activity;

import java.io.IOException;

import fr.quoteBrowser.BashDotOrgQuoteProvider;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.QuoteProvider;

public class RecentQuotesActivity extends AbstractQuoteListActivity {

	@Override
	protected Quote[] getQuotes() throws IOException {
		QuoteProvider qp=new BashDotOrgQuoteProvider();
		Quote[] quotes;
		quotes = qp.getRecentQuotes();
		return quotes;
	}

}
