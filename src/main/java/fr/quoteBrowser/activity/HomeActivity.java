package fr.quoteBrowser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import fr.quoteBrowser.R;

public class HomeActivity extends Activity {

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
		setContentView(R.layout.home_activity_layout);
		((Button) findViewById(R.id.latestQuotesButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(),
								LatestQuotesActivity.class);
						startActivity(intent);
					}
				});
		((Button) findViewById(R.id.randomQuotesButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(),
								RandomQuotesActivity.class);
						startActivity(intent);
					}
				});
		((Button) findViewById(R.id.browseQuotesButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getApplicationContext(),
								BrowseQuotesActivity.class);
						startActivity(intent);
					}
				});
		((Button) findViewById(R.id.topQuotesButton))
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						TopQuotesActivity.class);
				startActivity(intent);
			}
		});

	}

}
