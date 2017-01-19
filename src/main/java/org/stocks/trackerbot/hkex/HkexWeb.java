package org.stocks.trackerbot.hkex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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

	private String baseUrl = "http://www.hkexnews.hk/sdw/search/search_sdw.asp";
//	private final String postTemplate = "txt_today_d=%1$s&txt_today_m=%2$s&txt_today_y=%3$s&current_page=1&stock_market=HKEX&IsExist_Slt_Stock_Id=False&IsExist_Slt_Part_Id=False&rdo_SelectSortBy=Shareholding&sessionToken=%4$s&sel_ShareholdingDate_d=%5$s&sel_ShareholdingDate_m=%6$s&sel_ShareholdingDate_y=%7$s&txt_stock_code=%8$05d&txt_stock_name=&txt_ParticipantID=&txt_Participant_name=";
	private final String postTemplate = "today=%1%s&sortBy=&selPartID=&alertMsg=&ddlShareholdingDay=%2%s&ddlShareholdingMonth=%3%s&ddlShareholdingYear=%4%s&txtStockCode=%5%s&txtStockName=&txtParticipantID=&txtParticipantName=&btnSearch.x=18&btnSearch.y=15";
	
	private final Pattern participantPattern = Pattern.compile(
			"<td valign=\"top\" nowrap=\"nowrap\" class=\"arial12black\" bgcolor=\"#[\\w]+\"><img src=\"../../image/spacer.gif\" width=\"10\" height=\"10\"/>([\\w]+)</td>");
	private final Pattern percentagePattern = Pattern.compile(
			"<td valign=\"top\" nowrap=\"nowrap\" class=\"arial12black\" align=\"Right\" bgcolor=\"#[\\w]+\">([0-9.%]+)<img src=\"../../image/spacer.gif\" width=\"10\" height=\"10\"/>");
	private final Pattern holdingPattern = Pattern.compile(
			"<td valign=\"top\" nowrap=\"nowrap\" class=\"arial12black\" align=\"Right\" bgcolor=\"#[\\w]+\">([0-9,]+)</td>");
	private final Pattern totalHoldingPattern = Pattern.compile(
			"<td nowrap=\"nowrap\" class=\"arial12black\"  align=\"Right\"><span class=\"mobilezoom\">15,144,602,396</span>");

	private static final SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy");
	private static final SimpleDateFormat monthSdf = new SimpleDateFormat("MM");
	private static final SimpleDateFormat dateSdf = new SimpleDateFormat("dd");
	private static final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

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
			String sessionToken = this.extractSessionToken(cookieAndData[1]);
			// System.out.println("ST " + sessionToken);
			// System.out.println("Cookie " + cookieAndData[0]);

			Calendar today = Calendar.getInstance();

			int symbolInt = Integer.parseInt(symbol);

			String postData = String.format(postTemplate, yyyyMMdd.format(today.getTime()), sessionToken, date, month,
					year, symbolInt);
			 System.out.println("PostData " + postData);

			resp = HttpUtil.post(baseUrl, postData, cookieAndData[0]);

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

}
