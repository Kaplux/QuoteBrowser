package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.Leadbolt.AdController;

import fr.quoteBrowser.Quote;
import fr.quoteBrowser.R;
import fr.quoteBrowser.service.QuotePager;

public class BrowseQuotesActivity extends Activity implements
		OnSharedPreferenceChangeListener {

	private static final String QUOTES = "quotes";
	private static String TAG = "quoteBrowser";

	private AdController adController;

	private enum LoadListAction {
		RELOAD_PAGE, NEXT_PAGE, PREVIOUS_PAGE
	};

	private ArrayList<Quote> quotes = null;

	private boolean preferencesChanged;

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

		if (savedInstanceState != null) {
			quotes = savedInstanceState.getParcelableArrayList(QUOTES);
		}
		if (quotes == null) {
			quotes = new ArrayList<Quote>();
		}

		initAdBannerView();
		ListView quoteListView = (ListView) findViewById(R.id.quoteListView);
		quoteListView.setAdapter(new QuoteAdapter(this,
				R.layout.quote_list_item_layout, quotes));
		if (quotes.isEmpty()) {
			loadQuoteList(LoadListAction.NEXT_PAGE);
		}

	}

	protected void initAdBannerView() {
		final Activity currentActivity = this;
		final ViewGroup quoteLayout = (ViewGroup) findViewById(R.id.quoteListLayout);
		DisplayMetrics dm = new DisplayMetrics();
		currentActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		final int screenWidth = dm.widthPixels;
		LinearLayout adLayout = new LinearLayout(this);
		if (screenWidth >= 468) {
			adLayout.setMinimumHeight(60);
		} else {
			adLayout.setMinimumHeight(50);
		}
		quoteLayout.addView(adLayout);
		quoteLayout.post(new Runnable() {
			public void run() {
				String myAdId = "";
				if (screenWidth >= 468) {
					myAdId = "332579652";
				} else {
					myAdId = "595643384";
				}
				adController = new AdController(currentActivity, myAdId);
				adController.loadAd();
			}
		});

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	protected void loadQuoteList(final LoadListAction action) {
		if (!isNetworkAvailable()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Internet connection unavailable. Please check your network connection settings and refresh the page")
					.setCancelable(false)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
								}
							});
			builder.create().show();
		} else {
			final ProgressDialog progressDialog = ProgressDialog.show(this,
					"Loading", "please wait", true);
			new AsyncTask<Void, Void, List<Quote>>() {
				@Override
				protected List<Quote> doInBackground(Void... params) {
					try {
						quotes.clear();
						switch (action) {
						case RELOAD_PAGE:
							quotes.addAll(QuotePager.getInstance(
									getApplicationContext()).reloadQuotePage());
							break;
						case NEXT_PAGE:
							quotes.addAll(QuotePager.getInstance(
									getApplicationContext()).getNextQuotePage());
							break;
						case PREVIOUS_PAGE:
							quotes.addAll(QuotePager.getInstance(
									getApplicationContext())
									.getPreviousQuotePage());
							break;
						default:
							break;
						}

					} catch (IOException e) {
						Log.e(TAG, e.getMessage(), e);
					}
					return quotes;
				}

				protected void onPreExecute() {
					super.onPreExecute();
				}

				protected void onPostExecute(List<Quote> quotes) {
					ListView quoteListView = (ListView) findViewById(R.id.quoteListView);
					((QuoteAdapter) quoteListView.getAdapter())
							.notifyDataSetChanged();
					quoteListView.setSelectionAfterHeaderView();
					progressDialog.dismiss();
				}

			}.execute();
		}
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
		case R.id.nextQuotePageMenuOption:
			adController.loadAd();
			loadQuoteList(LoadListAction.NEXT_PAGE);
			return true;
		case R.id.previousQuotePageMenuOption:
			adController.loadAd();
			loadQuoteList(LoadListAction.PREVIOUS_PAGE);
			return true;
		case R.id.refreshMenuOption:
			adController.loadAd();
			loadQuoteList(LoadListAction.RELOAD_PAGE);
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

	@Override
	protected void onDestroy() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this)
				.unregisterOnSharedPreferenceChangeListener(this);
		adController.destroyAd();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		preferencesChanged = true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putParcelableArrayList(QUOTES, quotes);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (QuotePager.getInstance(getApplicationContext()).getCurrentPage() > 0) {
			menu.findItem(R.id.previousQuotePageMenuOption).setEnabled(true);
		} else {
			menu.findItem(R.id.previousQuotePageMenuOption).setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		if (preferencesChanged) {
			QuotePager.getInstance(this).reset();
			loadQuoteList(LoadListAction.RELOAD_PAGE);
			preferencesChanged = false;
		}
		super.onResume();
	}

}
