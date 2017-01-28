package org.stocks.trackerbot.hkex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.hkex.Shareholding;
import org.stocks.trackerbot.util.DateUtil;
import org.stocks.trackerbot.util.HttpUtil;

public class HkexWeb {

	private static final Logger logger = LoggerFactory.getLogger(HkexWeb.class);

	private static final String baseUrl = "http://www.hkexnews.hk/sdw/search/searchsdw_c.aspx";

	private final Pattern viewStatePattern = Pattern.compile(
			"<input type=\"hidden\" name=\"__VIEWSTATE\" id=\"__VIEWSTATE\" value=\"([a-zA-Z0-9+/]+={0,2})\" />");
	private final Pattern viewStateGeneratorPattern = Pattern.compile(
			"<input type=\"hidden\" name=\"__VIEWSTATEGENERATOR\" id=\"__VIEWSTATEGENERATOR\" value=\"([\\w]+)\" />");
	private final Pattern eventValidationPattern = Pattern.compile(
			"<input type=\"hidden\" name=\"__EVENTVALIDATION\" id=\"__EVENTVALIDATION\" value=\"([a-zA-Z0-9+/]+={0,2})\" />");

	private final Pattern participantPattern = Pattern.compile(
			"<td valign=\"top\" nowrap=\"nowrap\" class=\"arial12black\">[\\s]*<img src=\"../../image/spacer.gif\" width=\"10\" height=\"10\" />([\\w]+)[\\s]*</td>");
	private final Pattern holdingPattern = Pattern.compile(
			"<td valign=\"top\" nowrap=\"nowrap\" class=\"arial12black\" style=\"text-align: right;\">[\\s]*([0-9,]+)<img src=\"../../image/spacer.gif\" width=\"10\" height=\"10\" />");
	private final Pattern percentagePattern = Pattern.compile(
			"<td valign=\"top\" nowrap=\"nowrap\" class=\"arial12black\" style=\"text-align: right;\">[\\s]*([0-9.%]+)<img src=\"../../image/spacer.gif\" alt=\"space\"[\\s]*width=\"10\" height=\"10\" />");
	
//	private final Pattern totalHoldingPattern = Pattern.compile(
//			"<td nowrap=\"nowrap\" class=\"arial12black\"  align=\"Right\"><span class=\"mobilezoom\">15,144,602,396</span>");

	private static final SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
	private static final SimpleDateFormat dateSdf = new SimpleDateFormat("dd");
	private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

	private static final String referer = "http://www.hkexnews.hk/sdw/search/searchsdw_c.aspx";

	public List<Shareholding> get(String symbol, Date date) {
		return this.get(symbol, yearSdf.format(date), monthSdf.format(date), dateSdf.format(date));
	}

	public List<Shareholding> get(String symbol, String dateStr) {
		String[] d = dateStr.split("-");
		return this.get(symbol, d[0], d[1], d[2]);
	}

	public List<Shareholding> get(String symbol, String year, String month, String date) {
		List<Shareholding> ret = new ArrayList<Shareholding>();
		String resp = null;
		try {
			String[] cookieAndData = HttpUtil.getCookieAndData(baseUrl);
//			String sessionToken = this.extractSessionToken(cookieAndData[1]);
//			System.out.println("ST " + sessionToken);
//			System.out.println("Cookie " + cookieAndData[0].split(";")[0]);
//			System.out.println("Data " + cookieAndData[1]);

			Matcher mvs = this.viewStatePattern.matcher(cookieAndData[1]);
			String viewState = "";
			if (mvs.find()) {
				viewState = mvs.group(1);
			}

			Matcher mvsg = this.viewStateGeneratorPattern.matcher(cookieAndData[1]);
			String viewStateGenerator = "";
			if (mvsg.find()) {
				viewStateGenerator = mvsg.group(1);
			}

			Matcher ev = this.eventValidationPattern.matcher(cookieAndData[1]);
			String eventValidation = "";
			if (ev.find()) {
				eventValidation = ev.group(1);
			}

			Calendar today = Calendar.getInstance();

			int symbolInt = Integer.parseInt(symbol);

			StringBuilder sb = new StringBuilder();
			sb.append("__VIEWSTATE" + "=" + URLEncoder.encode(viewState));
			sb.append("&" + "__VIEWSTATEGENERATOR" + "=" + URLEncoder.encode(viewStateGenerator));
			sb.append("&" + "__EVENTVALIDATION" + "=" + URLEncoder.encode(eventValidation));
			sb.append("&" + "today" + "=" + yyyyMMdd.format(today.getTime()));
			sb.append("&" + "sortBy" + "=");
			sb.append("&" + "selPartID" + "=");
			sb.append("&" + "alertMsg" + "=");
			sb.append("&" + "ddlShareholdingDay" + "=" + date);
			sb.append("&" + "ddlShareholdingMonth" + "=" + month);
			sb.append("&" + "ddlShareholdingYear" + "=" + year);
			sb.append("&" + "txtStockCode" + "=" + pad00000(symbolInt));
			sb.append("&" + "txtStockName" + "=");
			sb.append("&" + "txtParticipantID" + "=");
			sb.append("&" + "txtParticipantName" + "=");
			sb.append("&" + "btnSearch.x" + "=36");
			sb.append("&" + "btnSearch.y" + "=13");
						
//			System.out.println("PostData " + sb.toString());

			resp = HttpUtil.post(baseUrl, sb.toString(), cookieAndData[0], referer);

//			System.out.println(resp);

			List<String> allPId = new ArrayList<String>();
			Matcher mpa = participantPattern.matcher(resp);
			while (mpa.find()) {
				allPId.add(mpa.group(1));
			}
			List<String> allPercent = new ArrayList<String>();
			Matcher mpe = percentagePattern.matcher(resp);
			while (mpe.find()) {
				allPercent.add(mpe.group(1));
			}
			List<String> allHolding = new ArrayList<String>();
			Matcher mph = holdingPattern.matcher(resp);
			while (mph.find()) {
				allHolding.add(mph.group(1));
			}
			if (allPId.size() == 0 || allPercent.size() == 0 || allHolding.size() == 0) {
				logger.warn("skpping " + symbol + " " + year + "-" + month + "-" + date);
				return ret;
			}

			for (int i = 0; i < allPId.size(); i++) {
				Shareholding sh = new Shareholding();
				try {
					sh.setShareId(symbol);
					sh.setDate(DateUtil.getDate(year, month, date));
					sh.setParticipantId(allPId.get(i));
					if (i < allHolding.size()) {
						String h = allHolding.get(i);
						sh.setValue(parseHolding(h));
					}
					if (i < allPercent.size()) {
						String p = allPercent.get(i);
						sh.setPercentX100(parsePercent(p));
					}
					ret.add(sh);
				} catch (Exception e) {
					logger.error("Invalid shareholding: " + sh.toString(), e);
				}
			}

			return ret;
		} catch (Exception e) {
			logger.error("lost " + symbol + " " + year + "-" + month + "-" + date + ": " + resp, e);
		}
		return ret;
	}

	private BigInteger parseHolding(String h) {
		h = h.replace(",", "");
		return new BigInteger(h);
	}

	private static final BigDecimal bd100 = new BigDecimal(100);

	private int parsePercent(String p) {
		p = p.replaceAll("%", "");
		BigDecimal bd = new BigDecimal(p);
		bd = bd.multiply(bd100);
		bd = bd.setScale(0, RoundingMode.HALF_UP);
		return bd.intValue();
	}

	private final Pattern sessionTokenPattern = Pattern
			.compile("<input type=\"hidden\" name=\"sessionToken\" value=\"([\\d.]+)\".*");

	public String extractSessionToken(String input) {
		Matcher m = sessionTokenPattern.matcher(input);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}
	
	private String pad00000(int s) {
		return String.format("%05d", s);
	}
	
}
