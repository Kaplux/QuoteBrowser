package fr.quoteBrowser.activity;

import java.io.IOException;

import fr.quoteBrowser.Quote;
import fr.quoteBrowser.R;
import fr.quoteBrowser.R.layout;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public abstract class AbstractQuoteListActivity extends ListActivity {

	
    private static String TAG = "quoteBrowser";

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after 
     * previously being shut down then this Bundle contains the data it most 
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	
		try {
			Quote[] quotes = getQuotes();
		
    	
    	setListAdapter(new ArrayAdapter<Quote>(this, R.layout.quote_list_item_layout, quotes){

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView v=(TextView) super.getView(position, convertView, parent);
				v.setText(getItem(position).getQuoteText());
				return v;
			}});
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
  	
    }

	protected abstract Quote[] getQuotes() throws IOException;
    
    

}

