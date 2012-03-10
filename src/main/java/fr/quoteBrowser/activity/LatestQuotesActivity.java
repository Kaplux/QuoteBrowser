package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.quoteBrowser.BashDotOrgQuoteProvider;
import fr.quoteBrowser.FuckMyLifeDotComQuoteProvider;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.QuoteProvider;
import fr.quoteBrowser.XKCDBQuoteProvider;

public class LatestQuotesActivity extends AbstractQuoteListActivity {

	@Override
	protected List<Quote> getQuotes() throws IOException {
		ArrayList<Quote> result=new ArrayList<Quote>();
		QuoteProvider fmylifeQP=new FuckMyLifeDotComQuoteProvider();
		QuoteProvider bashQP=new BashDotOrgQuoteProvider();
		QuoteProvider xkcdbQP=new XKCDBQuoteProvider();
		
		result.addAll(xkcdbQP.getLatestQuotes());
		result.addAll(fmylifeQP.getLatestQuotes());
		result.addAll(bashQP.getLatestQuotes());
		Collections.shuffle(result);
		return result;
	}

}
