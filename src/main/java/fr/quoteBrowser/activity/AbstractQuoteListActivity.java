package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ListView;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.R;

public abstract class AbstractQuoteListActivity extends Activity implements
		OnSharedPreferenceChangeListener {

	private static String TAG = "quoteBrowser";

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState
	 *            If the activity is being re-initialized after previously being
	 *            shut down then this Bundle contains the data it most recently
	 *            supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it
	 *            is null.</b>
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.quote_list_layout);
		PreferenceManager.getDefaultSharedPreferences(this)
		.registerOnSharedPreferenceChangeListener(this);
		
		initAdBannerView();
		loadQuoteList();

	}

	protected void initAdBannerView() {
		WebView bannerAdView = (WebView) findViewById(R.id.bannerAdView);
		bannerAdView.getSettings().setJavaScriptEnabled(true);
		bannerAdView
				.loadData(
						"<script type=\"text/javascript\" src=\"http://ad.leadboltads.net/show_app_ad.js?section_id=593079000\"></script>",
						"text/html", null);
	}

	protected void loadQuoteList() {
		final ListView quoteListView = (ListView) findViewById(R.id.quoteListView);
		final Activity currentActivity = this;
		final ProgressDialog progressDialog = ProgressDialog.show(this,
				"Loading", "please wait", true);
		new AsyncTask<Void, Void, List<Quote>>() {

			@Override
			protected List<Quote> doInBackground(Void... params) {
				List<Quote> quotes = new ArrayList<Quote>();
				try {
					quotes = getQuotes();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
				return quotes;
			}

			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(List<Quote> quotes) {
				progressDialog.dismiss();
				quoteListView.setAdapter(new QuoteAdapter(
						currentActivity, R.layout.quote_list_item_layout,
						quotes));

			}

		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.quote_list_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.refreshMenuOption:
			loadQuoteList();
			return true;
		case R.id.preferencesMenuOption:
			Intent intent = new Intent(getApplicationContext(),
					QuotePreferencesActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected abstract List<Quote> getQuotes() throws IOException;

	

	@Override
	protected void onDestroy() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this)
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		loadQuoteList();
	}

}
