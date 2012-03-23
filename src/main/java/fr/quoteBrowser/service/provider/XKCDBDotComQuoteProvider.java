package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.Html;
import fr.quoteBrowser.Quote;

public class XKCDBDotComQuoteProvider extends AbstractQuoteProvider {

	public static final String SOURCE = "xkcdb.com";
	private static final int START_PAGE = 1;

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		return getQuotesFromURL("http://www.xkcdb.com/?&page="
				+ (pageNumber + START_PAGE));
	}

	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = getDocumentFromUrl(url);

		Elements quotesElts = doc.select("p.quoteblock");
		for (Element quotesElt : quotesElts) {
			int quoteId = Integer.valueOf(quotesElt.select("a.idlink").text()
					.replaceAll("#", ""));
			CharSequence quoteScore = StringUtils.substringBetween(quotesElt
					.select("span.quotehead").first().html(), "+", "/");
			CharSequence quoteText = quotesElt.select("span.quote").first()
					.html();
			Quote quote = new Quote(quoteText);
			quote.setQuoteId(quoteId);
			quote.setQuoteSource(SOURCE);
			quote.setQuoteScore(quoteScore);
			quotes.add(quote);
		}
		return quotes;
	}

	@Override
	public boolean supportsUsernameColorization() {
		return true;
	}

	@Override
	public String getSource() {
		return SOURCE;
	}

}
