package fr.quoteBrowser;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

public class MockQuoteProvider implements QuoteProvider {

	@Override
	public Quote[] getRecentQuotes() {
		SpannableStringBuilder ssb = new SpannableStringBuilder("premiere quote");
		ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);
		
		return new Quote[]{new Quote(ssb),new Quote("deuxieme quote")};
	}

}
