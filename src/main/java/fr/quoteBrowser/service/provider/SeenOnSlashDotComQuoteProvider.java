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

	public static final String SOURCE = "seenonslash.com";

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		return getQuotesFromURL("http://seenonslash.com/node?page="
				+ pageNumber);
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
			String titleLink = quotesElt.select("h1.title>a").first()
					.attr("href");
			CharSequence quoteTitle = Html.fromHtml(titleLink
					.substring(titleLink.lastIndexOf("/") + 1));
			CharSequence quoteScore = "";
			StringBuilder quoteText = new StringBuilder();
			quoteText.append("<div><b>" + quotesElt.select("h1.title>a").html()
					+ "</b></div>");
			quoteText.append(quotesElt.select("div.content").html());
			Quote quote = new Quote(quoteText.toString());
			quote.setQuoteTitle(quoteTitle);
			quote.setQuoteSource(SOURCE);
			quote.setQuoteScore(quoteScore);
			quote.setQuoteTextMD5(Quote.computeMD5Sum(quote.getQuoteText()));
			quotes.add(quote);
		}
		return quotes;
	}

	@Override
	public String getSource() {
		return SOURCE;
	}

}
