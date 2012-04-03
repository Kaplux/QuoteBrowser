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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Pair;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.BashDotOrgQuoteProvider;
import fr.quoteBrowser.service.provider.FMyLifeDotComQuoteProvider;
import fr.quoteBrowser.service.provider.QdbDotUsQuoteProvider;
import fr.quoteBrowser.service.provider.QuoteProvider;
import fr.quoteBrowser.service.provider.SeenOnSlashDotComQuoteProvider;
import fr.quoteBrowser.service.provider.XKCDBDotComQuoteProvider;

public class QuoteUtils {
	
	private static String TAG = "quoteBrowser";

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
			newQuote.setQuoteId(quote.getQuoteId());
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
	
	public static void scheduleDatabaseUpdate(Context context,boolean replaceIfExists,boolean startNow) {
		Intent intent = getQuoteIndexerIntent(context,0, 10);
		if (replaceIfExists || PendingIntent.getBroadcast(context, 1, intent,
				PendingIntent.FLAG_NO_CREATE) == null) {
			PendingIntent sender = PendingIntent.getBroadcast(context, 1, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			long updateInterval = Preferences.getInstance(context)
					.getUpdateIntervalPreference();
			long startTime=System.currentTimeMillis();
			if (!startNow){
				startTime+=updateInterval;
			}
			Log.d(TAG, "creating update service interval : "+updateInterval);
			am.setRepeating(AlarmManager.RTC, startTime,
					updateInterval, sender);
		}else{
			Log.d(TAG, "update service already set");
		}

	}
	
	private static Intent getQuoteIndexerIntent(Context context,int startPage, int numberOfPages) {
		Intent intent = new Intent(context,
				PeriodicalQuoteUpdater.class);
		intent.putExtra(QuoteIndexationService.START_PAGE_KEY, startPage);
		intent.putExtra(QuoteIndexationService.NUMBER_OF_PAGES_KEY,
				numberOfPages);
		return intent;
	}


}
