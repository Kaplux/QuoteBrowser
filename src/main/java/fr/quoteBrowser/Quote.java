package fr.quoteBrowser;

import android.os.Parcel;
import android.os.Parcelable;

public class Quote implements Parcelable {

	private String quoteText;
	
	public String getQuoteText() {
		return quoteText;
	}

	public void setQuoteText(String quoteText) {
		this.quoteText = quoteText;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(quoteText);
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
	
	public Quote(String quoteText) {
		super();
		this.quoteText = quoteText;
	}

	public Quote() {
		super();
	}
}
