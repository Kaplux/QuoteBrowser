package fr.quoteBrowser;

import java.io.IOException;

public interface QuoteProvider {
	
	Quote[]getLatestQuotes() throws IOException;
	Quote[]getRandomQuotes() throws IOException;
	Quote[]getQuotesFromPage(int pageNumber) throws IOException;
	Quote[]getTopQuotes() throws IOException;
}
