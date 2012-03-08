package fr.quoteBrowser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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

	final Integer[] colors = new Integer[] { Color.BLUE, Color.RED, Color.GREEN,
			Color.MAGENTA, Color.CYAN, Color.YELLOW, Color.GRAY };

	@Override
	public List<Quote> getLatestQuotes() throws IOException {
		return getQuotesFromURL("http://bash.org/?latest");
	}

	@Override
	public List<Quote> getRandomQuotes() throws IOException {
		return getQuotesFromURL("http://bash.org/?random");
	}

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		return getQuotesFromURL("http://bash.org/?browse&p=" + pageNumber);
	}

	@Override
	public List<Quote> getTopQuotes() throws IOException {
		return getQuotesFromURL("http://bash.org/?top2");
	}

	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = Jsoup.connect(url).get();
		Elements quotesElts = doc.select("p.quote");
		for (Element quotesElt : quotesElts) {
			CharSequence quoteText = Html.fromHtml(new TextNode(quotesElt.select("p.qt")
					.html(), "").getWholeText());
			quotes.add(new Quote(colorizeUsernames(quoteText)));
		}
		return quotes;
	}

	private CharSequence colorizeUsernames(CharSequence quoteText) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(quoteText);
		Queue<Integer> availableColors = new LinkedList<Integer>();
		availableColors.addAll(Arrays.asList(colors));
		for (Map.Entry<String, List<Integer>> usernameIndexesByUsername : getUsernamesIndexesFromQuote(
				quoteText.toString()).entrySet()) {
			Integer usernameColor = availableColors.poll();
			if (usernameColor != null) {
				for (int indexBaliseOuvrante : usernameIndexesByUsername
						.getValue()) {
					int indexBaliseFermante = quoteText.toString().indexOf(">",
							indexBaliseOuvrante);
					ssb.setSpan(new ForegroundColorSpan(usernameColor),
							indexBaliseOuvrante, indexBaliseFermante + 1, 0);

				}
			}
		}

		return ssb;
	}

	private Map<String, List<Integer>> getUsernamesIndexesFromQuote(
			String quoteText) {
		Map<String, List<Integer>> usernamesIndexesByUsernames = new LinkedHashMap<String, List<Integer>>();
		int currentIndex = 0;
		while (currentIndex < quoteText.length()) {
			int indexBaliseOuvrante = quoteText.toString().indexOf("<",
					currentIndex);
			int indexBaliseFermante = quoteText.toString().indexOf(">",
					indexBaliseOuvrante);
			if (indexBaliseOuvrante > -1 && indexBaliseFermante > -1) {
				String username = quoteText.substring(indexBaliseOuvrante,
						indexBaliseFermante);
				if (!usernamesIndexesByUsernames.containsKey(username)) {
					usernamesIndexesByUsernames.put(username,
							new ArrayList<Integer>());
				}
				usernamesIndexesByUsernames.get(username).add(
						indexBaliseOuvrante);
				currentIndex = indexBaliseFermante + 1;
			} else
				currentIndex = quoteText.length() + 1;

		}

		return usernamesIndexesByUsernames;
	}

}
