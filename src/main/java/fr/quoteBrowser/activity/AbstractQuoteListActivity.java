package fr.quoteBrowser.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.quoteBrowser.MockQuoteProvider;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.R;

public abstract class AbstractQuoteListActivity extends Activity {

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
				quoteListView.setAdapter(new ArrayAdapter<Quote>(
						currentActivity, R.layout.quote_list_item_layout,
						quotes) {

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					    View  view = layoutInflater.inflate(R.layout.quote_list_item_layout, null);
						((TextView)view.findViewById(R.id.quoteItemTextView)).setText(getItem(position).getQuoteText());
						return view;
					}
				});
				
			}

		}.execute();

	}

	protected abstract List<Quote> getQuotes() throws IOException;

}
