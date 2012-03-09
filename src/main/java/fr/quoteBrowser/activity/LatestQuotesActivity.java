package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.List;

import fr.quoteBrowser.FuckMyLifeDotComQuoteProvider;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.QuoteProvider;

public class LatestQuotesActivity extends AbstractQuoteListActivity {

	@Override
	protected List<Quote> getQuotes() throws IOException {
		QuoteProvider qp=new FuckMyLifeDotComQuoteProvider();
		return qp.getLatestQuotes();
	}

}
