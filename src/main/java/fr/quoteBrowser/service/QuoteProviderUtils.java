package fr.quoteBrowser.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.quoteBrowser.Quote;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

class QuoteProviderUtils {
	
	final private static Integer[] colors = new Integer[] { Color.BLUE, Color.RED,
			Color.MAGENTA, Color.CYAN, Color.DKGRAY, Color.GRAY };
	
	
	public static CharSequence colorizeUsernames(CharSequence quoteText) {
		SpannableStringBuilder ssb = new SpannableStringBuilder(quoteText);
		LinkedList<Integer> availableColors = new LinkedList<Integer>();
		availableColors.addAll(Arrays.asList(colors));
		Collections.shuffle(availableColors);
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
	

	private static Map<String, List<Integer>> getUsernamesIndexesFromQuote(
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


	public static synchronized List<Quote> colorizeUsernames(List<Quote> quotes) {
		ArrayList<Quote> result=new ArrayList<Quote>();
		for (Quote quote:quotes){
			Quote newQuote=new Quote(colorizeUsernames(quote.getQuoteText()));
			newQuote.setQuoteScore(quote.getQuoteScore());
			newQuote.setQuoteSource(quote.getQuoteSource());
			newQuote.setQuoteTitle(quote.getQuoteTitle());
			result.add(newQuote);
		}
		return result;
		
	}

}
