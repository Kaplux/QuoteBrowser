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
import fr.quoteBrowser.service.Preferences;
import fr.quoteBrowser.service.QuoteUtils;
import fr.quoteBrowser.service.provider.QuoteProvider;

class QuoteAdapter extends ArrayAdapter<Quote> {

	Context context;

	public QuoteAdapter(Context context, int textViewResourceId,
			List<Quote> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.quote_list_item_layout,
				null);
		boolean colorizeUsernames = Preferences.getInstance(context)
				.colorizeUsernames();
		if (!isEmpty()) {
			Quote quote = getItem(position);
			CharSequence formatedQuoteText = quote.getFormattedQuoteText();
			QuoteProvider p = QuoteUtils.getProviderFromSource(quote
					.getQuoteSource());
			if (colorizeUsernames && p.supportsUsernameColorization()) {
				formatedQuoteText = QuoteUtils
						.colorizeUsernames(formatedQuoteText);
			}
			((TextView) view.findViewById(R.id.quoteItemTextView))
					.setText(formatedQuoteText);
			((TextView) view.findViewById(R.id.quoteItemSourceView))
					.setText("source: " + quote.getQuoteSource());
			((TextView) view.findViewById(R.id.quoteItemTitleView))
					.setText("id: " + quote.getQuoteId());
			if (quote.getQuoteScore() != null && quote.getQuoteScore() != "") {
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
