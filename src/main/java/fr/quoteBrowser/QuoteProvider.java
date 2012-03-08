package fr.quoteBrowser;

import java.io.IOException;
import java.util.List;

public interface QuoteProvider {
	
	List<Quote> getLatestQuotes() throws IOException;
	List<Quote> getRandomQuotes() throws IOException;
	List<Quote> getQuotesFromPage(int pageNumber) throws IOException;
	List<Quote> getTopQuotes() throws IOException;
}
