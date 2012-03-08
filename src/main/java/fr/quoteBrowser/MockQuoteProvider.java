package fr.quoteBrowser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class MockQuoteProvider implements QuoteProvider {

	@Override
	public List<Quote> getLatestQuotes()throws IOException {
		SpannableStringBuilder ssb = new SpannableStringBuilder("premiere quote");
		ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);
		
		return Arrays.asList(new Quote[]{new Quote(ssb),new Quote("deuxieme quote")});
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

}
