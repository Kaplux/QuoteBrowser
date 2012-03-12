package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.List;

import fr.quoteBrowser.Quote;

public interface QuoteProvider {

	List<Quote> getQuotesFromPage(int pageNumber) throws IOException;

	QuoteProviderPreferencesDescription getPreferencesDescription();

	boolean supportsUsernameColorization();

}
