package fr.quoteBrowser;

import java.io.IOException;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class MockQuoteProvider implements QuoteProvider {

	@Override
	public Quote[] getLatestQuotes() {
		SpannableStringBuilder ssb = new SpannableStringBuilder("premiere quote");
		ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);
		
		return new Quote[]{new Quote(ssb),new Quote("deuxieme quote")};
	}

	@Override
	public Quote[] getRandomQuotes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quote[] getQuotesFromPage(int pageNumber) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quote[] getTopQuotes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}
