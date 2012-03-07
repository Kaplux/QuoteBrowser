package fr.quoteBrowser;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BashDotOrgQuoteProvider implements QuoteProvider {

	@Override
	public Quote[] getRecentQuotes() throws IOException {
		ArrayList<Quote> quotes=new ArrayList<Quote>();
		Document doc = Jsoup.connect("http://bash.org/?latest").get();
		Elements quotesElts = doc.select("p.qt");
		for (Element quotesElt : quotesElts) {
			  CharSequence quoteText = quotesElt.text();
			  quotes.add(new Quote(quoteText));
			}
		return quotes.toArray(new Quote[quotes.size()]);
	}

}
