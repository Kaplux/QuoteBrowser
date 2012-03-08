package fr.quoteBrowser;

import java.io.IOException;

public interface QuoteProvider {
	
	Quote[]getRecentQuotes() throws IOException;
	Quote[]getRandomQuotes() throws IOException;
	Quote[]getQuotesFromPage(int pageNumber) throws IOException;
	
}
