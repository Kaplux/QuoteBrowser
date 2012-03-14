package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.Html;
import fr.quoteBrowser.Quote;

public class SeenOnSlashDotComQuoteProvider extends AbstractQuoteProvider {

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		return getQuotesFromURL("http://seenonslash.com/node?page="+pageNumber);
	}

	@Override
	public QuoteProviderPreferencesDescription getPreferencesDescription() {
		return new QuoteProviderPreferencesDescription("seenonslashdotcom_preference",
				"seenonslash.com", "Enable seeonslash.com provider");
	}

	@Override
	public boolean supportsUsernameColorization() {
		return false;
	}
	
	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = getDocumentFromUrl(url);
	
		Elements quotesElts = doc.select("div.node");
		for (Element quotesElt : quotesElts) {
			CharSequence quoteTitle = "";
			CharSequence quoteScore="";
			CharSequence quoteText = Html.fromHtml(quotesElt.select("div.content").first().text());
			Quote quote = new Quote(quoteText);
			quote.setQuoteTitle(quoteTitle);
			quote.setQuoteSource("seenonslash.com");
			quote.setQuoteScore(quoteScore);
			quotes.add(quote);
		}
		return quotes;
	}

}
