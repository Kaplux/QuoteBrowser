package fr.quoteBrowser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import android.graphics.Color;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class BashDotOrgQuoteProvider implements QuoteProvider {

	@Override
	public Quote[] getLatestQuotes() throws IOException {
		return getQuotesFromURL("http://bash.org/?latest");
	}
	
	@Override
	public Quote[] getRandomQuotes() throws IOException {
		return getQuotesFromURL("http://bash.org/?random");
	}

	@Override
	public Quote[] getQuotesFromPage(int pageNumber) throws IOException {
		return getQuotesFromURL("http://bash.org/?browse&p="+pageNumber);
	}
	
	@Override
	public Quote[] getTopQuotes() throws IOException {
		return getQuotesFromURL("http://bash.org/?top2");
	}

	private Quote[] getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes=new ArrayList<Quote>();
		Document doc = Jsoup.connect(url).get();
		Elements quotesElts = doc.select("p.qt");
		for (Element quotesElt : quotesElts) {
			  CharSequence quoteText = Html.fromHtml(new TextNode(quotesElt.html(),"").getWholeText());
			  quotes.add(new Quote(colorizeUsernames(quoteText)));
			}
		return quotes.toArray(new Quote[quotes.size()]);
	}

	private CharSequence colorizeUsernames(CharSequence quoteText) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(quoteText);
		
		for (Map.Entry<String, List<Integer>> usernameIndexesByUsername:getUsernamesIndexesFromQuote(quoteText.toString()).entrySet()){
			for (int indexBaliseOuvrante:usernameIndexesByUsername.getValue()){
				int indexBaliseFermante=quoteText.toString().indexOf(">",indexBaliseOuvrante);
				ssb.setSpan(new ForegroundColorSpan(Color.BLUE), indexBaliseOuvrante, indexBaliseFermante+1, 0);
				
			}
		}
	
		return ssb;
	}
	
	private Map<String,List<Integer>> getUsernamesIndexesFromQuote(String quoteText){
		 Map<String,List<Integer>>  usernamesIndexesByUsernames=new HashMap<String,List<Integer>>();
		int currentIndex=0;
		while(currentIndex<quoteText.length()){
			int indexBaliseOuvrante=quoteText.toString().indexOf("<",currentIndex);
			int indexBaliseFermante=quoteText.toString().indexOf(">",indexBaliseOuvrante);
			if (indexBaliseOuvrante>-1 && indexBaliseFermante>-1){
				String username=quoteText.substring(indexBaliseOuvrante, indexBaliseFermante);
				if(!usernamesIndexesByUsernames.containsKey(username)){
					usernamesIndexesByUsernames.put(username,new ArrayList<Integer>());	
				}
				usernamesIndexesByUsernames.get(username).add(indexBaliseOuvrante);
				currentIndex=indexBaliseFermante+1;
			}else currentIndex=quoteText.length()+1;
			
		}
		
		return usernamesIndexesByUsernames;
	}



	

}
