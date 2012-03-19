package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import fr.quoteBrowser.Quote;

public class SeenOnSlashDotComQuoteProvider extends AbstractQuoteProvider {

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		return getQuotesFromURL("http://seenonslash.com/node?page="+pageNumber);
	}

	@Override
	public QuoteProviderPreferencesDescription getPreferencesDescription() {
		return new QuoteProviderPreferencesDescription("seenonslashdotcom_preference",
				"seenonslash.com", "Enable seeonslash.com provider",false);
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
			String titleLink=quotesElt.select("h1.title>a").first().attr("href");
			CharSequence quoteTitle = Html.fromHtml(titleLink.substring(titleLink.lastIndexOf("/")+1));
			CharSequence quoteScore="";
			SpannableStringBuilder quoteText = new SpannableStringBuilder();
			quoteText.append(Html.fromHtml("<div>"+quotesElt.select("h1.title>a").html()+"</div>"));
			quoteText.setSpan(new StyleSpan(Typeface.BOLD),
					0, quoteText.length(), 0);
			quoteText.append(Html.fromHtml(quotesElt.select("div.content").html()));
			Quote quote = new Quote(quoteText);
			quote.setQuoteTitle(quoteTitle);
			quote.setQuoteSource("seenonslash.com");
			quote.setQuoteScore(quoteScore);
			quote.setQuoteTextMD5(Quote.computeMD5Sum(quote.getQuoteText()));
			quotes.add(quote);
		}
		return quotes;
	}

}
