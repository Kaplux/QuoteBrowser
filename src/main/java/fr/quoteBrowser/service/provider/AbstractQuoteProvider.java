package fr.quoteBrowser.service.provider;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;

public abstract class AbstractQuoteProvider implements QuoteProvider {

	private static final int MAX_RETRY = 3;
	private static final String TAG = "quoteBrowser";
	
	protected Document getDocumentFromUrl(String url) throws IOException {
		int retryCount = 0;
		while (true) {
			try {
				Document doc = Jsoup.connect(url).data("query", "Java")
						.userAgent("Mozilla").cookie("auth", "token")
						.timeout(3000).post();
				return doc;
			} catch (IOException e) {
				retryCount++;
				if (retryCount > MAX_RETRY) {
					Log.i(TAG,"error while retrieving "+url +" max retry reached ...");
					throw e;
				}else{
					Log.i(TAG,"error while retrieving "+url +" retrying...");
				}
			}
		}
	}

}
