package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.quoteBrowser.Quote;

import android.text.Html;

public class XKCDBQuoteProvider implements QuoteProvider {

	private static final int START_PAGE=1;
	
	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		return getQuotesFromURL("http://www.xkcdb.com/?&page="+pageNumber+START_PAGE);
	}

	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = Jsoup.connect(url).data("query", "Java")
				.userAgent("Mozilla").cookie("auth", "token").timeout(3000)
				.post();

		Elements quotesElts = doc.select("p.quoteblock");
		for (Element quotesElt : quotesElts) {
			CharSequence quoteTitle = Html.fromHtml(quotesElt
					.select("a.idlink").text());
			CharSequence quoteScore = Html.fromHtml(quotesElt
					.select("span.quotehead").first().ownText());
			CharSequence quoteText = Html.fromHtml(quotesElt
					.select("span.quote").first().html());
			Quote quote = new Quote(quoteText);
			quote.setQuoteTitle(quoteTitle);
			quote.setQuoteSource("xkcdb.com");
			quote.setQuoteScore(quoteScore);
			quotes.add(quote);
		}
		return quotes;
	}

	@Override
	public QuoteProviderPreferencesDescription getPreferencesDescription() {
		return new QuoteProviderPreferencesDescription(
				"xkcdbdotcom_preference", "xkcdb.com",
				"Enable xkcdb.com provider");
	}

	@Override
	public boolean supportsUsernameColorization() {
		return true;
	}


}
