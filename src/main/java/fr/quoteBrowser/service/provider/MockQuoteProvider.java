package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import fr.quoteBrowser.Quote;

public class MockQuoteProvider implements QuoteProvider {

	
	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		SpannableStringBuilder ssb = new SpannableStringBuilder("premiere quote");
		ssb.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);
		
		return Arrays.asList(new Quote[]{new Quote(ssb),new Quote("deuxieme quote")});
	
	}
	
	@Override
	public QuoteProviderPreferencesDescription getPreferencesDescription() {
		return new QuoteProviderPreferencesDescription("mock_preference",
				"mock.com", "Enable mock provider",false);
	}
	
	@Override
	public boolean supportsUsernameColorization() {
		return true;
	}


}
