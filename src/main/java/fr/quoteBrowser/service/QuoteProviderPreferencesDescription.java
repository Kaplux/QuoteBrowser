package fr.quoteBrowser.service;

public class QuoteProviderPreferencesDescription {
	private String key;
	private String title;
	private String summary;
	
	public QuoteProviderPreferencesDescription(String key, String title,
			String summary) {
		super();
		this.key = key;
		this.title = title;
		this.summary = summary;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}
	
}
