package fr.quoteBrowser.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.BashDotOrgQuoteProvider;
import fr.quoteBrowser.service.provider.FMyLifeDotComQuoteProvider;
import fr.quoteBrowser.service.provider.QdbDotUsQuoteProvider;
import fr.quoteBrowser.service.provider.QuoteProvider;
import fr.quoteBrowser.service.provider.SeenOnSlashDotComQuoteProvider;
import fr.quoteBrowser.service.provider.XKCDBDotComQuoteProvider;

public class QuoteUtils {

	final private static Integer[] colors = new Integer[] { Color.BLUE,
			Color.RED, Color.rgb(218, 112, 214), Color.rgb(135, 206, 250),
			Color.rgb(34, 139, 34), Color.rgb(255, 140, 0),
			Color.rgb(160, 82, 45) };

	public static final QuoteProvider[] PROVIDERS = new QuoteProvider[] {
			new BashDotOrgQuoteProvider(), new QdbDotUsQuoteProvider(),
			new XKCDBDotComQuoteProvider(), new FMyLifeDotComQuoteProvider(),
			new SeenOnSlashDotComQuoteProvider() };

	public static CharSequence colorizeUsernames(CharSequence quoteText) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(quoteText);
		LinkedList<Integer> availableColors = new LinkedList<Integer>();
		availableColors.addAll(Arrays.asList(colors));
		Collections.shuffle(availableColors);
		for (Map.Entry<String, List<Pair<Integer, Integer>>> usernameIndexesByUsername : getUsernamesIndexesFromQuote(
				quoteText.toString()).entrySet()) {
			Integer usernameColor = availableColors.poll();
			if (usernameColor != null) {
				for (Pair<Integer, Integer> usernameIndexes : usernameIndexesByUsername
						.getValue()) {
					ssb.setSpan(new ForegroundColorSpan(usernameColor),
							usernameIndexes.first, usernameIndexes.second, 0);

				}
			}
		}

		return ssb;
	}

	private static Map<String, List<Pair<Integer, Integer>>> getUsernamesIndexesFromQuote(
			String quoteText) {
		Map<String, List<Pair<Integer, Integer>>> usernamesIndexesByUsernames = new LinkedHashMap<String, List<Pair<Integer, Integer>>>();
		String[] lines = quoteText.split("\\n");
		// usernames are either <...> or ...:
		Pattern usernamePattern = Pattern.compile("(<[^>]*>)|([^:]*:)");
		int previousCharNumber = 0;
		for (String line : lines) {
			Matcher m = usernamePattern.matcher(line);
			if (m.lookingAt()) {
				String username = line.substring(m.start(), m.end());
				if (!usernamesIndexesByUsernames.containsKey(username)) {
					usernamesIndexesByUsernames.put(username,
							new ArrayList<Pair<Integer, Integer>>());
				}
				usernamesIndexesByUsernames.get(username).add(
						new Pair<Integer, Integer>(m.start()
								+ previousCharNumber, m.end()
								+ previousCharNumber));
			}
			previousCharNumber += (line + "\n").length();
		}

		return usernamesIndexesByUsernames;
	}

	public static synchronized List<Quote> colorizeUsernames(List<Quote> quotes) {
		ArrayList<Quote> result = new ArrayList<Quote>();
		for (Quote quote : quotes) {
			Quote newQuote = new Quote(colorizeUsernames(quote.getQuoteText()));
			newQuote.setQuoteScore(quote.getQuoteScore());
			newQuote.setQuoteSource(quote.getQuoteSource());
			newQuote.setQuoteTitle(quote.getQuoteTitle());
			newQuote.setQuoteTextMD5(Quote.computeMD5Sum(quote.getQuoteText()));
			result.add(newQuote);
		}
		return result;

	}

	public static QuoteProvider getProviderFromSource(CharSequence quoteSource) {
		QuoteProvider result = null;
		for (QuoteProvider p : PROVIDERS) {
			if (p.getSource().equals(quoteSource)) {
				result = p;
				break;
			}
		}
		return result;
	}

}
