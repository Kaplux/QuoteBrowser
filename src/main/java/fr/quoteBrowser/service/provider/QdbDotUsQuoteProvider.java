package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.Html;
import fr.quoteBrowser.Quote;

public class QdbDotUsQuoteProvider extends AbstractQuoteProvider {
	public static final String SOURCE = "qdb.us";
	private static final int START_PAGE = 1;

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {

		return getQuotesFromURL("http://qdb.us/latest/"
				+ (pageNumber + START_PAGE));
	}

	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = getDocumentFromUrl(url);
		Elements quotesElts = doc.select("td.q");
		for (Element quotesElt : quotesElts) {
			int quoteId = Integer.valueOf((quotesElt.select("a.ql")
					.first().ownText().replaceAll("#", "")));
			CharSequence quoteScore = Html.fromHtml(quotesElt.select("span")
					.first().ownText());
			CharSequence quoteText = quotesElt.select("span.qt").first().html();
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
