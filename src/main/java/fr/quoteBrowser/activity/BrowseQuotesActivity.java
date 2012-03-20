package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
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
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.Leadbolt.AdController;

import fr.quoteBrowser.Quote;
import fr.quoteBrowser.R;
import fr.quoteBrowser.service.PeriodicalQuoteUpdater;
import fr.quoteBrowser.service.Preferences;
import fr.quoteBrowser.service.QuotePager;
import fr.quoteBrowser.service.QuoteUtils;
import fr.quoteBrowser.service.provider.QuoteProvider;

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
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.quote_list_layout);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon);
		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(this);

		if (savedInstanceState != null) {
			quotes = savedInstanceState.getParcelableArrayList(QUOTES);
		}
		if (quotes == null) {
			quotes = new ArrayList<Quote>();
		}

		initAdBannerView();
		Intent intent = new Intent(getApplicationContext(),
				PeriodicalQuoteUpdater.class);
		PendingIntent sender = PendingIntent.getBroadcast(this, 1, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

		if (QuotePager.getInstance(this).isDatabaseEmpty()) {
			reindexDatabase();
			am.setRepeating(AlarmManager.ELAPSED_REALTIME,
					AlarmManager.INTERVAL_HOUR,System.currentTimeMillis()+ AlarmManager.INTERVAL_HOUR,
					sender);
		} else {
			am.setRepeating(AlarmManager.ELAPSED_REALTIME, 0,
					AlarmManager.INTERVAL_HOUR, sender);
		}

		ListView quoteListView = (ListView) findViewById(R.id.quoteListView);
		quoteListView.setAdapter(new QuoteAdapter(this,
				R.layout.quote_list_item_layout, quotes));
		if (quotes.isEmpty()) {
			loadQuoteList(LoadListAction.RELOAD_PAGE);
		}

	}

	private void reindexDatabase() {
		final Activity currentActivity = this;
		final ProgressDialog progressDialog = ProgressDialog.show(this,
				"Initialising Quote Database", "please wait", true);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				progressDialog.show();
				QuotePager.getInstance(currentActivity).reindexDatabase();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progressDialog.dismiss();
				if (QuotePager.getInstance(currentActivity).isDatabaseEmpty()) {
					showDatabaseReindexFailureAlert();
				}

			}
		}.execute();
	}

	private void showDatabaseReindexFailureAlert() {
		final Activity currentActivity = this;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Failed to load quote page")
				.setCancelable(false)
				.setPositiveButton("Retry",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								reindexDatabase();
							}
						});

		builder.setNegativeButton("Quit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						currentActivity.finish();
					}
				});

		builder.create().show();
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
			showInternetConnectionNotAvailableAlert();
		} else {
			final Activity currentActivity = this;
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

				protected void onPostExecute(List<Quote> quotes) {
					progressDialog.dismiss();
					ListView quoteListView = (ListView) findViewById(R.id.quoteListView);
					((QuoteAdapter) quoteListView.getAdapter())
							.notifyDataSetChanged();
					quoteListView.setSelectionAfterHeaderView();

					setTitle(currentActivity);
				}

			}.execute();
		}
	}

	private void setTitle(final Activity currentActivity) {
		int currentPage = QuotePager.getInstance(getApplicationContext())
				.getCurrentPage();
		int maxPage = QuotePager.getInstance(getApplicationContext())
				.computeMaxPage();
		String currentPageIndicator = currentPage >= 0 ? " (page "
				+ currentPage + "/" + maxPage + ")" : "";
		currentActivity.setTitle(getString(R.string.app_name)
				+ currentPageIndicator);
	}

	private void showInternetConnectionNotAvailableAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Internet connection unavailable. Please check your network connection settings and refresh the page.")
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.create().show();
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
		case R.id.displayMenuOption:
			showDisplayOptionsDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showDisplayOptionsDialog() {
		final List<CharSequence> options = new ArrayList<CharSequence>();
		String selectedOption = Preferences
				.getDisplayPreference(getApplicationContext());
		int selectedOptionIndex = 0;
		options.add("all");
		for (QuoteProvider qp : QuoteUtils.PROVIDERS) {
			options.add(qp.getSource());
			if (qp.getSource().toString().equals(selectedOption)) {
				selectedOptionIndex = options.size() - 1;
			}
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Quotes to display: ");
		builder.setSingleChoiceItems(options.toArray(new CharSequence[0]),
				selectedOptionIndex, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						CharSequence selectedOption = options.get(item);
						Preferences.saveDisplayPreference(
								getApplicationContext(),
								selectedOption.toString());
						loadQuoteList(LoadListAction.RELOAD_PAGE);
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	protected void onDestroy() {
		super.onPause();
		PreferenceManager.getDefaultSharedPreferences(this)
				.unregisterOnSharedPreferenceChangeListener(this);
		if (adController != null) {
			adController.destroyAd();
		}
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
		if (QuotePager.getInstance(getApplicationContext()).getCurrentPage() > QuotePager.FIRST_PAGE_INDEX) {
			menu.findItem(R.id.previousQuotePageMenuOption).setEnabled(true);
		} else {
			menu.findItem(R.id.previousQuotePageMenuOption).setEnabled(false);
		}
		if (QuotePager.getInstance(getApplicationContext()).getCurrentPage() < QuotePager
				.getInstance(getApplicationContext()).computeMaxPage()) {
			menu.findItem(R.id.nextQuotePageMenuOption).setEnabled(true);
		} else {
			menu.findItem(R.id.nextQuotePageMenuOption).setEnabled(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		if (preferencesChanged) {
			loadQuoteList(LoadListAction.RELOAD_PAGE);
			preferencesChanged = false;
		}
		super.onResume();
		setTitle(this);
	}

}
