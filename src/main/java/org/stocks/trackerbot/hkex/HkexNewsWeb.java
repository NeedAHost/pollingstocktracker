package org.stocks.trackerbot.hkex;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.hkex.News;
import org.stocks.trackerbot.util.HttpUtil;

public class HkexNewsWeb {
	
	private static final Logger logger = LoggerFactory.getLogger(HkexNewsWeb.class);

	private final String baseUrl = "http://www.hkexnews.hk/listedco/listconews/advancedsearch/search_active_main_c.aspx";
	
	private final Pattern viewStatePattern = Pattern.compile("<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"([a-zA-Z0-9+/]+={0,2})\" />");
	
	private final Pattern viewStateGeneratorPattern = Pattern.compile("<input type=\"hidden\" name=\"__VIEWSTATEGENERATOR\" id=\"__VIEWSTATEGENERATOR\" value=\"([\\w]+)\" />");
	
	public class SessionData {
		public String viewState;
		public String viewStateGenerator;
		public String cookie;
		
		@Override
		public String toString() {
			return "__VIEWSTATE=" + URLEncoder.encode(viewState) + "&__VIEWSTATEGENERATOR=" + URLEncoder.encode(viewStateGenerator) + "&__VIEWSTATEENCRYPTED=";
		}
	}
	
	public SessionData getSession() {
		String[] ret = HttpUtil.getCookieAndData(baseUrl);
		SessionData data = new SessionData();
		data.cookie = ret[0];
		Matcher vsp = viewStatePattern.matcher(ret[1]);
		if (vsp.find()) {
			data.viewState = vsp.group(1);
		}
		Matcher vsgp = viewStateGeneratorPattern.matcher(ret[1]);
		if (vsgp.find()) {
			data.viewStateGenerator = vsgp.group(1);
		}
		return data;
	}
	
	private final static DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	public String getTodayPlacingParams(SessionData session) {
		StringBuilder sb = new StringBuilder();
		sb.append(session.toString());		
		LocalDate today = LocalDate.now();
		String todayStr = today.format(yyyyMMdd);
		LocalDate yesterday = today.minusDays(1);
		sb.append("&" + URLEncoder.encode("ctl00$txt_today") + "=" + todayStr);
		sb.append("&" + URLEncoder.encode("ctl00$hfStatus") + "=ACM");
		sb.append("&" + URLEncoder.encode("ctl00$hfAlert") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$txt_stock_code") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$txt_stock_name") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectDocType") + "=rbAfter2006");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_1") + "=1");
		sb.append("&" + URLEncoder.encode("ctl00$sel_DocTypePrior2006") + "=-1");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_2_group") + "=8");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_2") + "=95");
		sb.append("&" + URLEncoder.encode("ctl00$ddlTierTwo") + "=59,1,7");
		sb.append("&" + URLEncoder.encode("ctl00$ddlTierTwoGroup") + "=26,5");
		sb.append("&" + URLEncoder.encode("ctl00$txtKeyWord") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_d") + "=" + pad00(yesterday.getDayOfMonth()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_m") + "=" + pad00(yesterday.getMonthValue()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_y") + "=" + yesterday.getYear());
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_d") + "=" + pad00(today.getDayOfMonth()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_m") + "=" + pad00(today.getMonthValue()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_y") + "=" + today.getYear());
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectDateOfRelease") + "=rbManualRange");
		sb.append("&" + URLEncoder.encode("ctl00$sel_defaultDateRange") + "=SevenDays");
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectSortBy") + "=rbDateTime");
//		System.out.println(sb.toString());
		return sb.toString();
	}
	
	private String pad00(int s) {
		return String.format("%02d", s);
	}
	
	public String getTodayAcquiringParams(SessionData session) {
		StringBuilder sb = new StringBuilder();
		sb.append(session.toString());		
		LocalDate today = LocalDate.now();
		String todayStr = today.format(yyyyMMdd);
		LocalDate yesterday = today.minusDays(1);
		sb.append("&" + URLEncoder.encode("ctl00$txt_today") + "=" + todayStr);
		sb.append("&" + URLEncoder.encode("ctl00$hfStatus") + "=ACM");
		sb.append("&" + URLEncoder.encode("ctl00$hfAlert") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$txt_stock_code") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$txt_stock_name") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectDocType") + "=rbAfter2006");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_1") + "=1");
		sb.append("&" + URLEncoder.encode("ctl00$sel_DocTypePrior2006") + "=-1");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_2_group") + "=7");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_2") + "=60");
		sb.append("&" + URLEncoder.encode("ctl00$ddlTierTwo") + "=59,1,7");
		sb.append("&" + URLEncoder.encode("ctl00$ddlTierTwoGroup") + "=26,5");
		sb.append("&" + URLEncoder.encode("ctl00$txtKeyWord") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_d") + "=" + pad00(yesterday.getDayOfMonth()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_m") + "=" + pad00(yesterday.getMonthValue()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_y") + "=" + yesterday.getYear());
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_d") + "=" + pad00(today.getDayOfMonth()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_m") + "=" + pad00(today.getMonthValue()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_y") + "=" + today.getYear());
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectDateOfRelease") + "=rbManualRange");
		sb.append("&" + URLEncoder.encode("ctl00$sel_defaultDateRange") + "=SevenDays");
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectSortBy") + "=rbDateTime");
		return sb.toString();
	}
	
	public String getTodayRevenueParams(SessionData session) {
		StringBuilder sb = new StringBuilder();
		sb.append(session.toString());		
		LocalDate today = LocalDate.now();
		String todayStr = today.format(yyyyMMdd);
		LocalDate yesterday = today.minusDays(1);
		sb.append("&" + URLEncoder.encode("ctl00$txt_today") + "=" + todayStr);
		sb.append("&" + URLEncoder.encode("ctl00$hfStatus") + "=ACM");
		sb.append("&" + URLEncoder.encode("ctl00$hfAlert") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$txt_stock_code") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$txt_stock_name") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectDocType") + "=rbAfter2006");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_1") + "=1");
		sb.append("&" + URLEncoder.encode("ctl00$sel_DocTypePrior2006") + "=-1");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_2_group") + "=3");
		sb.append("&" + URLEncoder.encode("ctl00$sel_tier_2") + "=31");
		sb.append("&" + URLEncoder.encode("ctl00$ddlTierTwo") + "=59,1,7");
		sb.append("&" + URLEncoder.encode("ctl00$ddlTierTwoGroup") + "=26,5");
		sb.append("&" + URLEncoder.encode("ctl00$txtKeyWord") + "=");
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_d") + "=" + pad00(yesterday.getDayOfMonth()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_m") + "=" + pad00(yesterday.getMonthValue()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseFrom_y") + "=" + yesterday.getYear());
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_d") + "=" + pad00(today.getDayOfMonth()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_m") + "=" + pad00(today.getMonthValue()));
		sb.append("&" + URLEncoder.encode("ctl00$sel_DateOfReleaseTo_y") + "=" + today.getYear());
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectDateOfRelease") + "=rbManualRange");
		sb.append("&" + URLEncoder.encode("ctl00$sel_defaultDateRange") + "=SevenDays");
		sb.append("&" + URLEncoder.encode("ctl00$rdo_SelectSortBy") + "=rbDateTime");
		return sb.toString();
	}
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	private final Pattern stockCodePattern = Pattern.compile("<span id=\"ctl[\\d]+_gvMain_ctl[\\d]+_lbStockCode\">([\\d]+)</span>");
	private final Pattern namePattern = Pattern.compile("<span id=\"ctl[\\d]+_gvMain_ctl[\\d]+_lbStockName\">(.+?)(?=<)</span>");
	private final Pattern datePattern = Pattern.compile("<span id=\"ctl[\\d]+_gvMain_ctl[\\d]+_lbDateTime\">([\\d/]+)<br>([\\d:]+)</span>");
	private final Pattern titleAndUrlPattern = Pattern.compile("<a id=\"ctl[\\d]+_gvMain_ctl[\\d]+_hlTitle\" class=\"news\" href=\"(.+?)(?=\")\" target=\"_blank\">(.+?)(?=<)</a>");
	
	public List<News> getAcquiringNewsList() {
		SessionData session = this.getSession();
		String postData = this.getTodayAcquiringParams(session);
		return this.getNewsList(session, postData);
	}
	
	public List<News> getRevenueNewsList() {
		SessionData session = this.getSession();
		String postData = this.getTodayRevenueParams(session);
		return this.getNewsList(session, postData);
	}
	
	public List<News> getPlacingNewsList() {
		SessionData session = this.getSession();
		String postData = this.getTodayPlacingParams(session);
		return this.getNewsList(session, postData);
	}
	
	public List<News> getNewsList(SessionData session, String postData) {
		String resp = HttpUtil.post(baseUrl, postData, session.cookie);
		
//		System.out.println(resp);
		
		List<String> allCode = new ArrayList<String>();
		Matcher mscp = stockCodePattern.matcher(resp);
		while (mscp.find()) {
			allCode.add(mscp.group(1));
		}
		
		List<String> allName = new ArrayList<String>();
		Matcher mnp = namePattern.matcher(resp);
		while (mnp.find()) {
			allName.add(mnp.group(1));
		}
		
		
		List<Date> allDate = new ArrayList<Date>();
		Matcher mdp = datePattern.matcher(resp);
		while (mdp.find()) {
			try {
				Date d = sdf.parse(mdp.group(1) + " " + mdp.group(2));
				allDate.add(d);
			} catch (ParseException e) {
				logger.error("Invalid date " + mdp.group(1) + " " + mdp.group(2), e);
			}			
		}
		
		List<String> allTitle = new ArrayList<String>();
		List<String> allUrl = new ArrayList<String>();
		Matcher mtup = titleAndUrlPattern.matcher(resp);
		while (mtup.find()) {
			allUrl.add("http://www.hkexnews.hk" + mtup.group(1));
			allTitle.add(mtup.group(2));
		}
		
		List<News> ret = new ArrayList<News>();
		for (int i = 0; i < allCode.size(); i++) {
			News n = new News();
			if (allCode.size() > i) {
				n.setSymbol(allCode.get(i));
			}
			if (allName.size() > i) {
				n.setName(allName.get(i));
			}
			if (allDate.size() > i) {
				n.setTime(allDate.get(i));
			}
			if (allTitle.size() > i) {
				n.setTitle(allTitle.get(i));
			}
			if (allUrl.size() > i) {
				n.setPdfUrl(allUrl.get(i));
			}
			ret.add(n);
		}
		
		return ret;
	}
	
}
