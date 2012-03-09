package fr.quoteBrowser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import android.text.Html;

public class FuckMyLifeDotComQuoteProvider implements QuoteProvider {

	@Override
	public List<Quote> getLatestQuotes() throws IOException {
		return getQuotesFromURL("http://www.fmylife.com/tops");
	}

	@Override
	public List<Quote> getRandomQuotes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Quote> getTopQuotes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = Jsoup.connect(url).get();
		Elements quotesElts = doc.select("div");
		for (Element quotesElt : quotesElts) {
			CharSequence quoteTitle = "";
			CharSequence quoteScore= "";
			CharSequence quoteText = Html.fromHtml(new TextNode(quotesElt
					.html(), "").getWholeText());
			Quote quote = new Quote(quoteText);
			quote.setQuoteTitle(quoteTitle);
			quote.setQuoteSource("fmylife.com");
			quote.setQuoteScore(quoteScore);
			quotes.add(quote);
		}
		return quotes;
	}

}
