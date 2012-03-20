package fr.quoteBrowser;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import fr.quoteBrowser.service.QuoteUtils;
import fr.quoteBrowser.service.provider.QuoteProvider;

public class Quote implements Parcelable {

	private CharSequence quoteText;
	private CharSequence quoteTitle;
	private CharSequence quoteSource;
	private CharSequence quoteScore;
	private String quoteTextMD5;

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
	
	public CharSequence getFormattedQuoteText(){
		QuoteProvider p = QuoteUtils.getProviderFromSource(this.getQuoteSource());
		CharSequence quoteText= Html.fromHtml(getQuoteText().toString());
		if (p.supportsUsernameColorization()){
			quoteText=QuoteUtils.colorizeUsernames(quoteText);
		}
		return quoteText;
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

	public CharSequence getQuoteSource() {
		return quoteSource;
	}

	public CharSequence getQuoteScore() {
		return quoteScore;
	}

	public void setQuoteScore(CharSequence quoteScore) {
		this.quoteScore = quoteScore;
	}

	public void setQuoteSource(CharSequence quoteSource) {
		this.quoteSource = quoteSource;
	}

	public void setQuoteTextMD5(String quoteTextMD5) {
		this.quoteTextMD5=quoteTextMD5;
	}

	public String getQuoteTextMD5() {
		return quoteTextMD5;
	}
	
	public static String computeMD5Sum(CharSequence quoteText){
		return new String(Hex.encodeHex(DigestUtils.md5(quoteText.toString())));
	}

	
}
