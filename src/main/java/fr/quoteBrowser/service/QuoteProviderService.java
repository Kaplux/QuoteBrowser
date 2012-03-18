package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.List;

import fr.quoteBrowser.Quote;
import fr.quoteBrowser.service.provider.QuoteProviderPreferencesDescription;

public interface QuoteProviderService {


	public abstract List<QuoteProviderPreferencesDescription> getQuoteProvidersPreferences();

	public abstract List<Quote> getQuotes(int i, int quotesPerPage) throws IOException;

}