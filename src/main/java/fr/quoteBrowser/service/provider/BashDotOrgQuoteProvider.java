package fr.quoteBrowser.service.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import fr.quoteBrowser.Quote;

import android.text.Html;

public class BashDotOrgQuoteProvider implements QuoteProvider {

	private Integer mostRecentQuotePage=null;
	
	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		if (mostRecentQuotePage==null){
		 mostRecentQuotePage=getMostRecentQuotePage();	
		}
		return getQuotesFromURL("http://bash.org/?browse&p=" + (mostRecentQuotePage-pageNumber));
	}

	private Integer getMostRecentQuotePage() throws IOException {
		//String html="<p><center><table cellpadding=\"0\" cellspacing=\"0\" width=\"80%\"><tr><td><font class=\"qt\"><center><font class=\"qa\">01</font>-<a href=\"./?browse&p=02\" class=\"qa\">02</a>-<a href=\"./?browse&p=03\" class=\"qa\">03</a>-<a href=\"./?browse&p=04\" class=\"qa\">04</a>-<a href=\"./?browse&p=05\" class=\"qa\">05</a>-<a href=\"./?browse&p=06\" class=\"qa\">06</a> <a href=\"./?browse&p=2\" class=\"qa\">&gt;</a> <a href=\"./?browse&p=11\" class=\"qa\">+10</a> <a href=\"?browse&p=419\" class=\"qa\">End</a> <br><form action=\"./?browse\" name=\"sel\"><font class=\"seltext\">Page: &nbsp;<select class=\"seltext\" name=\"browse\" onchange=\"javascript: document.sel.submit()\"></font><option value=\"1\" selected>1</option><option value=\"2\">2</option><option value=\"3\">3</option><option value=\"4\">4</option><option value=\"5\">5</option><option value=\"6\">6</option><option value=\"7\">7</option><option value=\"8\">8</option><option value=\"9\">9</option><option value=\"10\">10</option><option value=\"11\">11</option><option value=\"12\">12</option><option value=\"13\">13</option><option value=\"14\">14</option><option value=\"15\">15</option><option value=\"16\">16</option><option value=\"17\">17</option><option value=\"18\">18</option><option value=\"19\">19</option><option value=\"20\">20</option><option value=\"21\">21</option><option value=\"22\">22</option><option value=\"23\">23</option><option value=\"24\">24</option><option value=\"25\">25</option><option value=\"26\">26</option><option value=\"27\">27</option><option value=\"28\">28</option><option value=\"29\">29</option><option value=\"30\">30</option><option value=\"31\">31</option><option value=\"32\">32</option><option value=\"33\">33</option><option value=\"34\">34</option><option value=\"35\">35</option><option value=\"36\">36</option><option value=\"37\">37</option><option value=\"38\">38</option><option value=\"39\">39</option><option value=\"40\">40</option><option value=\"41\">41</option><option value=\"42\">42</option><option value=\"43\">43</option><option value=\"44\">44</option><option value=\"45\">45</option><option value=\"46\">46</option><option value=\"47\">47</option><option value=\"48\">48</option><option value=\"49\">49</option><option value=\"50\">50</option><option value=\"51\">51</option><option value=\"52\">52</option><option value=\"53\">53</option><option value=\"54\">54</option><option value=\"55\">55</option><option value=\"56\">56</option><option value=\"57\">57</option><option value=\"58\">58</option><option value=\"59\">59</option>";
		//Document doc=Jsoup.parse(html);
		
		Document doc = Jsoup.connect("http://bash.org/?browse").get();
		
		String endPageUrl = doc.select("a:containsOwn(end)").first().attr("href");
		return Integer.valueOf(endPageUrl.substring(endPageUrl.lastIndexOf("=")+1));
	}

	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		// Document doc = Jsoup
		// .parse("<p class=\"quote\"><a href=\"?949959\" title=\"Permanent link to this quote.\"><b>#949959</b></a> <a href=\"./?le=232502a3822ac47973c7f647edf9eff3&amp;rox=949959\" class=\"qa\">+</a>(690)<a href=\"./?le=232502a3822ac47973c7f647edf9eff3&amp;sox=949959\" class=\"qa\">-</a> <a href=\"./?le=232502a3822ac47973c7f647edf9eff3&amp;sux=949959\" onClick=\"return confirm('Flag quote for review?');\" class=\"qa\">[X]</a></p><p class=\"qt\">&lt;DevXen&gt; Today I was at the store and saw a Darth Vader action figure that said &quot;Choking Hazard.&quot; It was great.</p><p class=\"quote\"><a href=\"?949802\" title=\"Permanent link to this quote.\"><b>#949802</b></a> <a href=\"./?le=232502a3822ac47973c7f647edf9eff3&amp;rox=949802\" class=\"qa\">+</a>(34)<a href=\"./?le=232502a3822ac47973c7f647edf9eff3&amp;sox=949802\" class=\"qa\">-</a> <a href=\"./?le=232502a3822ac47973c7f647edf9eff3&amp;sux=949802\" onClick=\"return confirm('Flag quote for review?');\" class=\"qa\">[X]</a></p><p class=\"qt\">&lt;hq1&gt; i promised myself not to touch java EVER in my life again, i'd rather drive a taxi</p>");
		Document doc = Jsoup.connect(url).get();
		Elements quotesElts = doc.select("p.quote");
		for (Element quotesElt : quotesElts) {
			CharSequence quoteTitle = Html.fromHtml(new TextNode(quotesElt
					.select("b").html(), "").getWholeText());
			CharSequence quoteScore = quotesElt.childNode(3).toString();
			CharSequence quoteText = Html.fromHtml(new TextNode(quotesElt
					.nextElementSibling().html(), "").getWholeText());
			Quote quote = new Quote(quoteText);
			quote.setQuoteTitle(quoteTitle);
			quote.setQuoteSource("bash.org");
			quote.setQuoteScore(quoteScore);
			quotes.add(quote);
		}
		return quotes;
	}

	@Override
	public QuoteProviderPreferencesDescription getPreferencesDescription() {
		return new QuoteProviderPreferencesDescription("bashdotorg_preference",
				"bash.org", "Enable bash.org provider");
	}

	@Override
	public boolean supportsUsernameColorization() {
		return true;
	}



}
