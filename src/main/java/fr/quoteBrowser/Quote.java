package fr.quoteBrowser;

import android.os.Parcel;
import android.os.Parcelable;

public class Quote implements Parcelable {

	private CharSequence quoteText;
	private CharSequence quoteTitle;
	private int quoteScore;
	


	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeValue(quoteText);
	}

	public static final Parcelable.Creator<Quote> CREATOR = new Parcelable.Creator<Quote>() {
		public Quote createFromParcel(Parcel in) {
			return new Quote(in);
		}

		public Quote[] newArray(int size) {
			return new Quote[size];
		}
	};

	private Quote(Parcel in) {
		quoteText = in.readString();
	}
	
	public Quote(CharSequence quoteText) {
		super();
		this.quoteText = quoteText;
	}

	public Quote() {
		super();
	}

	public CharSequence getQuoteText() {
		return quoteText;
	}

	public void setQuoteText(CharSequence quoteText) {
		this.quoteText = quoteText;
	}

	public CharSequence getQuoteTitle() {
		return quoteTitle;
	}

	public void setQuoteTitle(CharSequence quoteTitle) {
		this.quoteTitle = quoteTitle;
	}

	public int getQuoteScore() {
		return quoteScore;
	}

	public void setQuoteScore(int quoteScore) {
		this.quoteScore = quoteScore;
	}
}
