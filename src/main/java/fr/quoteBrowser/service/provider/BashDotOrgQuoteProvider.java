package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import fr.quoteBrowser.Quote;

public class BashDotOrgQuoteProvider extends AbstractQuoteProvider{

	public static final String SOURCE = "bash.org";
	private Integer mostRecentQuotePage=null;
	
	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		if (mostRecentQuotePage==null){
		 mostRecentQuotePage=getMostRecentQuotePage();	
		}
		return getQuotesFromURL("http://bash.org/?browse&p=" + (mostRecentQuotePage-pageNumber));
	}

	private Integer getMostRecentQuotePage() throws IOException {
		Document doc = getDocumentFromUrl("http://bash.org/?browse");
		
		String endPageUrl = doc.select("a:containsOwn(end)").first().attr("href");
		return Integer.valueOf(endPageUrl.substring(endPageUrl.lastIndexOf("=")+1));
	}

	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = getDocumentFromUrl(url);
		Elements quotesElts = doc.select("p.quote");
		for (Element quotesElt : quotesElts) {
			int quoteId = Integer.valueOf(quotesElt
					.select("b").html().replaceAll("#",""));
			CharSequence quoteScore = quotesElt.childNode(3).toString().replace("(","").replace(")","");
			CharSequence quoteText = new TextNode(quotesElt
					.nextElementSibling().html(), "").getWholeText();
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
