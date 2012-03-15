package fr.quoteBrowser.service.provider;

public class QuoteProviderPreferencesDescription {
	private String key;
	private String title;
	private String summary;
	private boolean enabledByDefault;
	
	public QuoteProviderPreferencesDescription(String key, String title,
			String summary,boolean enabledByDefault) {
		super();
		this.key = key;
		this.title = title;
		this.summary = summary;
		this.enabledByDefault=enabledByDefault;
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


	public boolean isEnabledByDefault() {
		return enabledByDefault;
	}


	public void setEnabledByDefault(boolean enabledByDefault) {
		this.enabledByDefault = enabledByDefault;
	}
	
}
