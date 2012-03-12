package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.List;

import fr.quoteBrowser.Quote;

interface QuoteProvider {

	List<Quote> getLatestQuotes() throws IOException;

	List<Quote> getRandomQuotes() throws IOException;

	List<Quote> getQuotesFromPage(int pageNumber) throws IOException;

	List<Quote> getTopQuotes() throws IOException;

	QuoteProviderPreferencesDescription getPreferencesDescription();

	boolean supportsUsernameColorization();
}
