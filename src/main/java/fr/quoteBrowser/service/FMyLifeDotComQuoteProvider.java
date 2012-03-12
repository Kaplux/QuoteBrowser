package fr.quoteBrowser.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import fr.quoteBrowser.Quote;

import android.text.Html;

class FMyLifeDotComQuoteProvider implements QuoteProvider {

	@Override
	public List<Quote> getLatestQuotes() throws IOException {
		return getQuotesFromURL("http://www.fmylife.com/");
	}

	@Override
	public List<Quote> getRandomQuotes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Quote> getQuotesFromPage(int pageNumber) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Quote> getTopQuotes() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private List<Quote> getQuotesFromURL(String url) throws IOException {
		ArrayList<Quote> quotes = new ArrayList<Quote>();
		Document doc = Jsoup.connect(url) .data("query", "Java")
				  .userAgent("Mozilla")
				  .cookie("auth", "token")
				  .timeout(3000)
				  .post();
		//String html="<div id=\"ad_leaderboard\"><div class=\"leaderboard\"><script type=\"text/javascript\" src=\"http://engine.espace.netavenir.com/?zid=2280\"></script> <noscript><a href=\"http://www.netavenir.fr/\">Net Avenir : gestion publicitaire<img src=\"http://engine.espace.netavenir.com/?zid=2280&noscript=true\" width=\"0\" height=\"0\" border=\"0\" alt=\"Net Avenir : gestion publicitaire\" /></a></noscript></div><div class=\"leaderboard_close\"><a href=\"javascript:;\" onclick=\"ClosePub();\">Close the advertisement</a></div><div class=\"clear\"></div></div><div class=\"post autopromo bordered\"><h2>The Illustrated FML</h2><a href=\"/miscellaneous/11316835\"><img src=\"http://cdn.betacie.net/fmylife/data/en/illust/mini_5cf184dfcd3166834d321d750ab63367.jpg\" /></a></div><div class=\"post autopromo bordered right\"><a href=\"http://www.fmylife.com/moderate\"><img src=\"http://cdn.betacie.net/fmylife/data/en/autopromo/61c8f7bf92b3503eba4d3e45edd498e5.png\" /></a></div><div class=\"clear\"></div><div id=\"top_message\"></div><div class=\"post article\" id=\"19244225\"><p><a href=\"/miscellaneous/19244225\" class=\"fmllink\">Today, I thought it would be funny to scare my dad for once, since he has scared me for fun dozens of times.</a><a href=\"/miscellaneous/19244225\" class=\"fmllink\"> It would have been funny, had he not punched me in the face.</a><a href=\"/miscellaneous/19244225\" class=\"fmllink\"> FML</a></p><div class=\"date\"><div class=\"left_part\"><a href=\"/miscellaneous/19244225\" id=\"article_19244225\" name=\"/resume/article/19244225\" class=\"jTip\">#19244225</a> (<span class=\"dyn-comments\">26</span>)</div><div class=\"right_part\"><p><span class=\"dyn-vote-j\" id=\"vote19244225\"><a href=\"javascript:;\" onclick=\"vote('19244225','232','agree');\">I agree, your life sucks</a> (<span class=\"dyn-vote-j-data\">232</span>)</span> - <span class=\"dyn-vote-t\" id=\"votebf19244225\"><a href=\"javascript:;\" onclick=\"vote('19244225','103','deserve');\" class=\"bf\">you deserved it</a> (<span class=\"dyn-vote-t-data\">103</span>)</span></p><p style=\"margin-top:2px;\">On 03/09/2012 at 5:10am - <a class=\"liencat\" href=\"/miscellaneous\">misc</a> - by stupidprankster  - <a href=\"/country/United States\" class=\"liencat\">United States</a></p></div></div><div class=\"more\" id=\"more19244225\"><iframe src=\"http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwww.fmylife.com%2Fmiscellaneous%2F19244225&amp;layout=button_count&amp;show_faces=false&amp;width=110&amp;action=like&amp;font&amp;colorscheme=light&amp;height=21\" scrolling=\"no\" frameborder=\"0\" style=\"border:none; overflow:hidden; width:110px; height:25px;\" allowTransparency=\"true\"></iframe><a href=\"javascript:;\" onclick=\"return twitter_click('http://www.fmylife.com/miscellaneous/19244225#new','19244225');\"><img src=\"http://cdn1.fmylife.com/fmylife/images/tw0.png\" onmouseover=\"this.src='http://cdn4.fmylife.com/fmylife/images/tw1.png';\" onmouseout=\"this.src='http://cdn3.fmylife.com/fmylife/images/tw0.png';\" title=\"ReTweet\" /></a><span class=\"dyn-fav\"><a href=\"javascript:;\" onclick=\"FavoriIcon('add','19244225');\" class=\"favori\"><img src=\"http://cdn4.fmylife.com/fmylife/images/fav0.png\" onmouseover=\"this.src='http://cdn5.fmylife.com/fmylife/images/fav05.png';\" onmouseout=\"this.src='http://cdn3.fmylife.com/fmylife/images/fav0.png';\" title=\"Add to favorites\" /></a></span><div class=\"clear\"></div></div><div class=\"clear\"></div></div><div class=\"post article\" id=\"19244068\"><p><a href=\"/health/19244068\" class=\"fmllink\">Today, I was walking to the bus in my favorite jeans, and I felt a uncontrollable itch in my leg.</a><a href=\"/health/19244068\" class=\"fmllink\"> I scratched and it went away, but then I felt something moving on my leg.</a><a href=\"/health/19244068\" class=\"fmllink\"> I hadn't worn my jeans in so long that a spider had decided to make it a nest.</a><a href=\"/health/19244068\" class=\"fmllink\"> FML</a></p><div class=\"date\"><div class=\"left_part\"><a href=\"/health/19244068\" id=\"article_19244068\" name=\"/resume/article/19244068\" class=\"jTip\">#19244068</a> (<span class=\"dyn-comments\">25</span>)</div><div class=\"right_part\"><p><span class=\"dyn-vote-j\" id=\"vote19244068\"><a href=\"javascript:;\" onclick=\"vote('19244068','292','agree');\">I agree, your life sucks</a> (<span class=\"dyn-vote-j-data\">292</span>)</span> - <span class=\"dyn-vote-t\" id=\"votebf19244068\"><a href=\"javascript:;\" onclick=\"vote('19244068','49','deserve');\" class=\"bf\">you deserved it</a> (<span class=\"dyn-vote-t-data\">49</span>)</span></p><p style=\"margin-top:2px;\">On 03/09/2012 at 3:30am - <a class=\"liencat\" href=\"/health\">health</a> - by Rissa Warrington  - <a href=\"/country/Canada\" class=\"liencat\">Canada</a> (<a href=\"/region/British Columbia\" class=\"light\">British Columbia</a>)</p></div></div><div class=\"more\" id=\"more19244068\"><iframe src=\"http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwww.fmylife.com%2Fhealth%2F19244068&amp;layout=button_count&amp;show_faces=false&amp;width=110&amp;action=like&amp;font&amp;colorscheme=light&amp;height=21\" scrolling=\"no\" frameborder=\"0\" style=\"border:none; overflow:hidden; width:110px; height:25px;\" allowTransparency=\"true\"></iframe><a href=\"javascript:;\" onclick=\"return twitter_click('http://www.fmylife.com/health/19244068#new','19244068');\"><img src=\"http://cdn2.fmylife.com/fmylife/images/tw0.png\" onmouseover=\"this.src='http://cdn4.fmylife.com/fmylife/images/tw1.png';\" onmouseout=\"this.src='http://cdn5.fmylife.com/fmylife/images/tw0.png';\" title=\"ReTweet\" /></a><span class=\"dyn-fav\"><a href=\"javascript:;\" onclick=\"FavoriIcon('add','19244068');\" class=\"favori\"><img src=\"http://cdn5.fmylife.com/fmylife/images/fav0.png\" onmouseover=\"this.src='http://cdn1.fmylife.com/fmylife/images/fav05.png';\" onmouseout=\"this.src='http://cdn4.fmylife.com/fmylife/images/fav0.png';\" title=\"Add to favorites\" /></a></span><div class=\"clear\"></div></div><div class=\"clear\"></div></div><div class=\"post article\" id=\"19243836\"><p><a href=\"/intimacy/19243836\" class=\"fmllink\">Today, I was at my girlfriend's house for the first time.</a><a href=\"/intimacy/19243836\" class=\"fmllink\"> I cracked a joke that offended her, so she gave me the silent treatment.</a><a href=\"/intimacy/19243836\" class=\"fmllink\"> I had to pee, and since she wouldn't tell me where the bathroom was, I went to look for it.</a><a href=\"/intimacy/19243836\" class=\"fmllink\"> I walked in on her parents making love.</a><a href=\"/intimacy/19243836\" class=\"fmllink\"> FML</a></p><div class=\"date\"><div class=\"left_part\"><a href=\"/intimacy/19243836\" id=\"article_19243836\" name=\"/resume/article/19243836\" class=\"jTip\">#19243836</a> (<span class=\"dyn-comments\">110</span>)</div><div class=\"right_part\"><p><span class=\"dyn-vote-j\" id=\"vote19243836\"><a href=\"javascript:;\" onclick=\"vote('19243836','1108','agree');\">I agree, your life sucks</a> (<span class=\"dyn-vote-j-data\">1108</span>)</span> - <span class=\"dyn-vote-t\" id=\"votebf19243836\"><a href=\"javascript:;\" onclick=\"vote('19243836','148','deserve');\" class=\"bf\">you deserved it</a> (<span class=\"dyn-vote-t-data\">148</span>)</span></p><p style=\"margin-top:2px;\">On 03/09/2012 at 1:59am - <a class=\"liencat\" href=\"/intimacy\">intimacy</a> - by banned  - <a href=\"/country/United States\" class=\"liencat\">United States</a> (<a href=\"/region/California\" class=\"light\">California</a>)</p></div></div><div class=\"more\" id=\"more19243836\"><iframe src=\"http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwww.fmylife.com%2Fintimacy%2F19243836&amp;layout=button_count&amp;show_faces=false&amp;width=110&amp;action=like&amp;font&amp;colorscheme=light&amp;height=21\" scrolling=\"no\" frameborder=\"0\" style=\"border:none; overflow:hidden; width:110px; height:25px;\" allowTransparency=\"true\"></iframe><a href=\"javascript:;\" onclick=\"return twitter_click('http://www.fmylife.com/intimacy/19243836#new','19243836');\"><img src=\"http://cdn3.fmylife.com/fmylife/images/tw0.png\" onmouseover=\"this.src='http://cdn5.fmylife.com/fmylife/images/tw1.png';\" onmouseout=\"this.src='http://cdn4.fmylife.com/fmylife/images/tw0.png';\" title=\"ReTweet\" /></a><span class=\"dyn-fav\"><a href=\"javascript:;\" onclick=\"FavoriIcon('add','19243836');\" class=\"favori\"><img src=\"http://cdn4.fmylife.com/fmylife/images/fav0.png\" onmouseover=\"this.src='http://cdn4.fmylife.com/fmylife/images/fav05.png';\" onmouseout=\"this.src='http://cdn5.fmylife.com/fmylife/images/fav0.png';\" title=\"Add to favorites\" /></a></span><div class=\"clear\"></div></div><div class=\"clear\"></div></div><div class=\"post article\" id=\"19243809\"><p><a href=\"/work/19243809\" class=\"fmllink\">Today, while on trial for a desperately needed new job, I tripped crossing a road with my would-be manager.</a><a href=\"/work/19243809\" class=\"fmllink\"> I twisted my ankle, and he had to carry me across the road and call a taxi for me to go home.</a><a href=\"/work/19243809\" class=\"fmllink\"> FML</a></p><div class=\"date\"><div class=\"left_part\"><a href=\"/work/19243809\" id=\"article_19243809\" name=\"/resume/article/19243809\" class=\"jTip\">#19243809</a> (<span class=\"dyn-comments\">31</span>)</div><div class=\"right_part\"><p><span class=\"dyn-vote-j\" id=\"vote19243809\"><a href=\"javascript:;\" onclick=\"vote('19243809','935','agree');\">I agree, your life sucks</a> (<span class=\"dyn-vote-j-data\">935</span>)</span> - <span class=\"dyn-vote-t\" id=\"votebf19243809\"><a href=\"javascript:;\" onclick=\"vote('19243809','106','deserve');\" class=\"bf\">you deserved it</a> (<span class=\"dyn-vote-t-data\">106</span>)</span></p><p style=\"margin-top:2px;\">On 03/09/2012 at 1:48am - <a class=\"liencat\" href=\"/work\">work</a> - by Katie (<a href=\"/gender/woman\" class=\"light\">woman</a>) - <a href=\"/country/United Kingdom\" class=\"liencat\">United Kingdom</a></p></div></div><div class=\"more\" id=\"more19243809\"><iframe src=\"http://www.facebook.com/plugins/like.php?href=http%3A%2F%2Fwww.fmylife.com%2Fwork%2F19243809&amp;layout=button_count&amp;show_faces=false&amp;width=110&amp;action=like&amp;font&amp;colorscheme=light&amp;height=21\" scrolling=\"no\" frameborder=\"0\" style=\"border:none; overflow:hidden; width:110px; height:25px;\" allowTransparency=\"true\"></iframe><a href=\"javascript:;\" onclick=\"return twitter_click('http://www.fmylife.com/work/19243809#new','19243809');\"><img src=\"http://cdn5.fmylife.com/fmylife/images/tw0.png\" onmouseover=\"this.src='http://cdn1.fmylife.com/fmylife/images/tw1.png';\" onmouseout=\"this.src='http://cdn3.fmylife.com/fmylife/images/tw0.png';\" title=\"ReTweet\" /></a><span class=\"dyn-fav\"><a href=\"javascript:;\" onclick=\"FavoriIcon('add','19243809');\" class=\"favori\"><img src=\"http://cdn5.fmylife.com/fmylife/images/fav0.png\" onmouseover=\"this.src='http://cdn1.fmylife.com/fmylife/images/fav05.png';\" onmouseout=\"this.src='http://cdn1.fmylife.com/fmylife/images/fav0.png';\" title=\"Add to favorites\" /></a></span><div class=\"clear\"></div></div><div class=\"clear\"></div></div>";
		//Document doc = Jsoup.parse(html);
		Elements quotesElts = doc.select("div.article");
		for (Element quotesElt : quotesElts) {
			CharSequence quoteTitle = quotesElt.id();
			CharSequence quoteScore= Html.fromHtml(quotesElt.select("span.dyn-vote-j-data").text());
			CharSequence quoteText = Html.fromHtml(quotesElt.select("p").first().text());
			Quote quote = new Quote(quoteText);
			quote.setQuoteTitle(quoteTitle);
			quote.setQuoteSource("fmylife.com");
			quote.setQuoteScore(quoteScore);
			quotes.add(quote);
		}
		return quotes;
	}
	
	@Override
	public String getPreferenceId() {
		return "fmylifedotcom_preference";
	}

	@Override
	public String getPreferenceTitle() {
		return "fmylife.com";
	}

	@Override
	public String getPreferenceSummary() {
		return "Enable fmylife.com provider";
	}

}