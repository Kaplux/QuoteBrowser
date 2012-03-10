package fr.quoteBrowser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.Html;

public class XKCDBQuoteProvider implements QuoteProvider {

	@Override
	public List<Quote> getLatestQuotes() throws IOException {
		return getQuotesFromURL("http://www.xkcdb.com/?recent");
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
		Document doc = Jsoup.connect(url) .data("query", "Java")
				  .userAgent("Mozilla")
				  .cookie("auth", "token")
				  .timeout(3000)
				  .post();
		
		Elements quotesElts = doc.select("p.quoteblock");
		for (Element quotesElt : quotesElts) {
			CharSequence quoteTitle = Html.fromHtml(quotesElt.select("a.idlink").text());
			CharSequence quoteScore= Html.fromHtml(quotesElt.select("a.uplink").text());
			CharSequence quoteText = Html.fromHtml(quotesElt.select("span.quote").first().html());
			Quote quote = new Quote(quoteText);
			quote.setQuoteTitle(quoteTitle);
			quote.setQuoteSource("xkcdb.com");
			quote.setQuoteScore(quoteScore);
			quotes.add(quote);
		}
		return quotes;
	}


}
