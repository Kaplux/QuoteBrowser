package fr.quoteBrowser.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.R;

public abstract class AbstractQuoteListActivity extends ListActivity {

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
		final Activity currentActivity=this;
		final ProgressDialog progressDialog = ProgressDialog.show(
				this, "Loading", "please wait", true);
		new AsyncTask<Void, Void, Quote[]>() {

			@Override
			protected Quote[] doInBackground(Void... params) {
				Quote[] quotes=new Quote[0];
				try {
					quotes= getQuotes();
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				}
				return quotes;
			}

			protected void onPreExecute() {
				super.onPreExecute();
			}

			protected void onPostExecute(Quote[] quotes) {
				progressDialog.dismiss();
				setListAdapter(new ArrayAdapter<Quote>(currentActivity,
						R.layout.quote_list_item_layout, quotes) {

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						TextView v = (TextView) super.getView(position,
								convertView, parent);
						v.setText(getItem(position).getQuoteText());
						return v;
					}
				});

			}

		}.execute();

	}

	protected abstract Quote[] getQuotes() throws IOException;

}
