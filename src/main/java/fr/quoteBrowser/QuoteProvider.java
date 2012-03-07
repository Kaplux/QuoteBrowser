package fr.quoteBrowser;

import java.io.IOException;

public interface QuoteProvider {
	
	Quote[]getRecentQuotes() throws IOException;

}
