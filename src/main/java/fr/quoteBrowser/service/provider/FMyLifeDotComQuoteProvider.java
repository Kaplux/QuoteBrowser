package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.Html;
import fr.quoteBrowser.Quote;

public class FMyLifeDotComQuoteProvider extends AbstractQuoteProvider {

	public static final String SOURCE = "fmylife.com";

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		return getQuotesFromURL("http://www.fmylife.com/?page=" + pageNumber);
	}

	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = getDocumentFromUrl(url);
		Elements quotesElts = doc.select("div.article");
		for (Element quotesElt : quotesElts) {
			int quoteId = Integer.valueOf(quotesElt.id());
			CharSequence quoteScore = Html.fromHtml(quotesElt.select(
					"span.dyn-vote-j-data").text());
			CharSequence quoteText = Html.fromHtml(quotesElt.select("p")
					.first().text());
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
		return false;
	}

	@Override
	public String getSource() {
		return SOURCE;
	}
}
