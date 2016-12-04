package org.stocks.trackerbot.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.util.HttpUtil;

public class YFinanceAPI {

	private static final String N_A = "N/A";
	private static final Logger logger = LoggerFactory.getLogger(YFinanceAPI.class);
	private final String baseUrl = "http://finance.yahoo.com/d/quotes.csv?f=pob3p2&s=";

	public void update(List<Stock> stocks) {
		String url = baseUrl;
		for (Stock stock : stocks) {
			url += stock.getSymbolPadded() + ".HK,";
		}
		if (stocks.size() > 0) {
			url = url.substring(0, url.length() - 1);
		}
		String get = HttpUtil.get(url);
		String[] lines = get.split("\n");
		for (int i = 0; i < stocks.size(); i++) {
			if (i < lines.length) {
				update(stocks.get(i), lines[i]);
			}
		}
	}

	// 5.45,5.44
	public void update(Stock stock) {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}
		String get = HttpUtil.get(baseUrl + stock.getSymbolPadded() + ".HK");
		update(stock, get);
	}

	private void update(Stock stock, String get) {
		if (get == null || get.length() == 0) {
			return;
		}
		String[] split = get.split(",");
		if (split.length > 0) {
			String open = split[0];
			if (!open.equals(N_A)) {
				stock.setOpen(open);
			}
		}
		if (split.length > 1) {
			String prevClose = split[1];
			if (!prevClose.equals(N_A)) {
				stock.setPrevClose(prevClose);
			}
		}
		if (split.length > 2) {
			String bid = split[2];
			if (!bid.equals(N_A)) {
				stock.setPrice(bid);
			}
		}
		if (split.length > 3) {
			String changePercent = split[3];
			if (!changePercent.equals(N_A)) {
				stock.setPriceChange(changePercent.replace("+", "").replace("\"", ""));
			}
		}
	}

}
