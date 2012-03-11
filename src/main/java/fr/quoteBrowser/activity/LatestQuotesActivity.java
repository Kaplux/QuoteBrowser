package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import fr.quoteBrowser.BashDotOrgQuoteProvider;
import fr.quoteBrowser.FuckMyLifeDotComQuoteProvider;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.QuoteProvider;
import fr.quoteBrowser.XKCDBQuoteProvider;

public class LatestQuotesActivity extends AbstractQuoteListActivity {
	
	private static String TAG = "quoteBrowser";
	
	@Override
	protected List<Quote> getQuotes() throws IOException {
		final List<Quote> result=Collections.synchronizedList(new ArrayList<Quote>());
		final QuoteProvider fmylifeQP=new FuckMyLifeDotComQuoteProvider();
		final QuoteProvider bashQP=new BashDotOrgQuoteProvider();
		final QuoteProvider xkcdbQP=new XKCDBQuoteProvider();
		
		Thread[] threads =new Thread[]{new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					result.addAll(fmylifeQP.getLatestQuotes());
				} catch (IOException e) {
					Log.e(TAG,e.getMessage());
				}
				
			}
		}),new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					result.addAll(bashQP.getLatestQuotes());
				} catch (IOException e) {
					Log.e(TAG,e.getMessage());
				}
				
			}
		}),new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					result.addAll(xkcdbQP.getLatestQuotes());
				} catch (IOException e) {
					Log.e(TAG,e.getMessage());
				}
				
			}
		})};
		
		for(Thread t:threads){
				t.start();
		}
		
		for(Thread t:threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				Log.e(TAG,e.getMessage());
			}
		}
	
		Collections.shuffle(result);
		return result;
	}

}
