package fr.quoteBrowser.activity;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.quoteBrowser.Quote;
import fr.quoteBrowser.R;

class QuoteAdapter extends ArrayAdapter<Quote> {

	public QuoteAdapter(Context context, int textViewResourceId,
			List<Quote> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.quote_list_item_layout,
				null);
		if (!isEmpty()) {
			Quote quote = getItem(position);
			((TextView) view.findViewById(R.id.quoteItemTextView))
					.setText(quote.getQuoteText());
			((TextView) view.findViewById(R.id.quoteItemSourceView))
					.setText("source: " + quote.getQuoteSource());
			((TextView) view.findViewById(R.id.quoteItemTitleView))
					.setText("id: " + quote.getQuoteTitle());
			if (quote.getQuoteScore() != null) {
				((TextView) view.findViewById(R.id.quoteItemScoreView))
						.setText("score: " + quote.getQuoteScore());
			}
		}
		return view;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}

}
