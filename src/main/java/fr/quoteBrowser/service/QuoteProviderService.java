package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.List;

import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.QuoteProviderPreferencesDescription;

public interface QuoteProviderService {

	public abstract List<Quote> getQuotesFromPage(final int pageNumber)
			throws IOException;

	public abstract List<QuoteProviderPreferencesDescription> getQuoteProvidersPreferences();

}