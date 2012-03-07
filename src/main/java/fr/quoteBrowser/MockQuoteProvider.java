package fr.quoteBrowser;

public class MockQuoteProvider implements QuoteProvider {

	@Override
	public Quote[] getRecentQuotes() {
		return new Quote[]{new Quote("premiereQuote"),new Quote("deuxieme quote")};
	}

}
