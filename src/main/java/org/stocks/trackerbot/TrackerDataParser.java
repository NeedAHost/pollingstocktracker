package org.stocks.trackerbot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.Category;
import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;

public class TrackerDataParser {

	private static final Logger logger = LoggerFactory.getLogger(TrackerDataParser.class);
	
	public TrackerData parse(String rawStr) {
		TrackerData data = new TrackerData();
		try {
			if (rawStr == null || rawStr.length() == 0) {
				return data;
			}
			// remove ~##~
			String xmlTrim = rawStr.split("~##~")[1];
			String[] trim = xmlTrim.split("~#~");
			data.setId(trim[0]);
			data.setTime(trim[1]);
			// remove header, time
			String[] groups = trim[2].split("~@~");
			for (String group : groups) {
				if (group.startsWith("Up")) {
					data.setUps(parseStocks(Category.UP, group.substring(3)));
				} else if (group.startsWith("Pending")) {
					data.setPendings(parseStocks(Category.PENDING, group.substring(8)));
				} else if (group.startsWith("PB")) {
					data.setPullBacks(parseStocks(Category.PULL_BACK, group.substring(3)));
				} else if (group.startsWith("AO")) {
					data.setAos(parseAOAndNH(Category.AO, group.substring(3)));
				} else if (group.startsWith("NH")) {
					data.setNewHighs(parseAOAndNH(Category.NEW_HIGH, group.substring(3)));
				} else if (group.startsWith("MC")) {
					data.setMcs(parseStocks(Category.MC, group.substring(3)));
				}
			}
		} catch (Exception e) {
			logger.error("invalid tracker data: " + rawStr, e);
		}
		return data;
	}
	
	private List<Stock> parseAOAndNH(Category category, String trim) {
		List<Stock> ret = new ArrayList<Stock>();
		String[] stocks = trim.split("~!~");
		for (String stock : stocks) {
			if (stock.length() == 0) {
				continue;
			}
			try {
				Stock s = new Stock();
				s.setCategory(category);
				String[] values = stock.split("~:~");
				s.setSymbol(String.format("%04d", Integer.parseInt(values[1])));
				s.setName(values[2]);
				s.setPrice(replaceX(values[3]));
				s.setPriceChange(replaceX(values[4]));
				s.setOpen(replaceX(values[5]));
				s.setVolume(replaceX(values[6]));
				if (values.length == 8) {
					s.setStartTime(values[7]);
				} else {
					s.setStartTime(new Date());
				}
				ret.add(s);
			} catch (Exception e) {
				logger.error("invalid data: " + stock, e);
			}
		}
		return ret;
	}

	private List<Stock> parseStocks(Category category, String trim) {
		List<Stock> ret = new ArrayList<Stock>();
		String[] stocks = trim.split("~!~");
		for (String stock : stocks) {
			if (stock.length() == 0) {
				continue;
			}
			try {
				Stock s = new Stock();
				s.setCategory(category);
				String[] values = stock.split("~:~");
				s.setSymbol(String.format("%04d", Integer.parseInt(values[1])));
				s.setName(values[2]);
				s.setPrice(replaceX(values[3]));
				s.setPriceChange(replaceX(values[4]));
				s.setVolume(replaceX(values[5]));
				s.setAvgVolume(replaceX(values[6]));
				if (values.length == 8) {
					s.setStartTime(values[7]);
				} else {
					s.setStartTime(new Date());
				}
				ret.add(s);
			} catch (Exception e) {
				logger.error("invalid data: " + stock, e);
			}
		}
		return ret;
	}
	
	private String replaceX(String s) {
		return s.replace(".x", "").replace("x", "");
	}

}
