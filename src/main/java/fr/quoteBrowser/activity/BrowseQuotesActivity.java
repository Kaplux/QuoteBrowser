package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.mutable.MutableInt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import fr.quoteBrowser.Quote;
import fr.quoteBrowser.R;
import fr.quoteBrowser.service.Preferences;
import fr.quoteBrowser.service.QuotePager;
import fr.quoteBrowser.service.QuoteUtils;
import fr.quoteBrowser.service.provider.QuoteProvider;

public class BrowseQuotesActivity extends Activity implements
		OnSharedPreferenceChangeListener {

	private static final String QUOTES = "quotes";
	private static String TAG = "quoteBrowser";

	private AdView adView;

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

		if (QuotePager.getInstance(this).isDatabaseEmpty()) {
			reindexDatabase();
		}else{
			QuoteUtils.scheduleDatabaseUpdate(this,false,true);
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
				try {
					QuotePager.getInstance(currentActivity).reindexDatabase();
					QuoteUtils.scheduleDatabaseUpdate(currentActivity,false,true);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				progressDialog.dismiss();
				loadQuoteList(LoadListAction.RELOAD_PAGE);

			}
		}.execute();
	}

	protected void initAdBannerView() {
		final ViewGroup quoteLayout = (ViewGroup) findViewById(R.id.quoteListLayout);
		// Create the adView
		adView = new AdView(this, AdSize.BANNER, "a14f6e53e04f4bf");
		LinearLayout adLayout = new LinearLayout(this);

		adLayout.addView(adLayout);
		// Add the adView to it
		quoteLayout.addView(adView);
		AdRequest ar = new AdRequest();
		// Initiate a generic request to load it with an ad
		adView.loadAd(ar);

	}

	protected void loadQuoteList(final LoadListAction action) {
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
								getApplicationContext()).getPreviousQuotePage());
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
			loadQuoteList(LoadListAction.NEXT_PAGE);
			return true;
		case R.id.previousQuotePageMenuOption:
			loadQuoteList(LoadListAction.PREVIOUS_PAGE);
			return true;
		case R.id.preferencesMenuOption:
			Intent intent = new Intent(getApplicationContext(),
					QuotePreferencesActivity.class);
			startActivity(intent);
			return true;
		case R.id.displayMenuOption:
			showDisplayOptionsDialog();
			return true;
		case R.id.gotoPageMenuOption:
			showGotoPageDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void showGotoPageDialog() {
		final Activity currentActivity = this;
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.goto_page_dialog_layout,
				(ViewGroup) findViewById(R.id.gotoPageLayoutRoot));
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		final TextView pageNumber = (TextView) layout
				.findViewById(R.id.pageNumber);
		final Button pageNumberDownButton = (Button) layout
				.findViewById(R.id.pageNumberDown);
		final Button pageNumberUpButton = (Button) layout
				.findViewById(R.id.pageNumberUp);
		final int maxPageNumber = QuotePager.getInstance(this).computeMaxPage();
		final MutableInt selectedPageNumber = new MutableInt(QuotePager
				.getInstance(this).getCurrentPage());
		pageNumber.setText(String.valueOf(selectedPageNumber));

		final Handler mHandler = new Handler();
		final Runnable updatePageNumberDown = new Runnable() {
			public void run() {
				if (selectedPageNumber.intValue() > 1) {
					selectedPageNumber.decrement();
					pageNumber.setText(String.valueOf(selectedPageNumber
							.intValue()));
				}
				mHandler.postAtTime(this, SystemClock.uptimeMillis() + 200);
			}
		};

		final Runnable updatePageNumberUp = new Runnable() {
			public void run() {
				if (selectedPageNumber.intValue() < maxPageNumber) {
					selectedPageNumber.increment();
					pageNumber.setText(String.valueOf(selectedPageNumber
							.intValue()));
				}
				mHandler.postAtTime(this, SystemClock.uptimeMillis() + 200);
			}
		};

		pageNumberDownButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					mHandler.removeCallbacks(updatePageNumberDown);
					mHandler.postAtTime(updatePageNumberDown,
							SystemClock.uptimeMillis());
				} else if (action == MotionEvent.ACTION_UP) {
					mHandler.removeCallbacks(updatePageNumberDown);
				}
				return false;
			}
		});

		pageNumberUpButton.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					mHandler.removeCallbacks(updatePageNumberUp);
					mHandler.postAtTime(updatePageNumberUp,
							SystemClock.uptimeMillis());
				} else if (action == MotionEvent.ACTION_UP) {
					mHandler.removeCallbacks(updatePageNumberUp);
				}
				return false;
			}
		});

		builder.setTitle("Goto page");
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				try {
					QuotePager.getInstance(currentActivity).gotoPage(
							Integer.valueOf(StringUtils.defaultString(
									pageNumber.getText().toString(), "1")));
					loadQuoteList(LoadListAction.RELOAD_PAGE);
				} catch (IOException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();

	}

	private void showDisplayOptionsDialog() {
		final List<CharSequence> options = new ArrayList<CharSequence>();
		String selectedOption = Preferences
				.getInstance(getApplicationContext()).getDisplayPreference();
		int selectedOptionIndex = 0;
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
						Preferences.getInstance(getApplicationContext())
								.saveDisplayPreference(
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
		if (adView != null) {
			adView.destroy();
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
			Log.d(TAG, "Preferences changed");
			loadQuoteList(LoadListAction.RELOAD_PAGE);
			QuoteUtils.scheduleDatabaseUpdate(this, true, false);
			preferencesChanged = false;
		}
		super.onResume();
		setTitle(this);
	}

}
