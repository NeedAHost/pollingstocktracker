package org.stocks.trackerbot.google;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.util.HttpUtil;

public class GoogFinanceAPI {

	private static final Logger logger = LoggerFactory.getLogger(GoogFinanceAPI.class);
	private final String baseUrl = "https://www.google.com/finance/info?q=HKG:";

	// [ { "id": "164573760542896" ,"t" : "0001" ,"e" : "HKG" ,"l" : "93.60"
	// ,"l_fix" : "93.60" ,"l_cur" : "HK$93.60" ,"s": "0" ,"ltt":"4:09PM GMT+8"
	// ,"lt" : "Nov 15, 4:09PM GMT+8" ,"lt_dts" : "2016-11-15T16:09:03Z" ,"c" :
	// "+0.25" ,"c_fix" : "0.25" ,"cp" : "0.27" ,"cp_fix" : "0.27" ,"ccol" :
	// "chg" ,"pcls_fix" : "93.35" } ]
	public void update(Stock stock) {
		String get = HttpUtil.get(baseUrl + stock.getSymbolPadded());
		if (get == null) {
			return;
		}
		get = get.substring(2, get.length());
		JSONArray arr = new JSONArray(get);
		if (arr.length() < 1) {
			return;
		}
		JSONObject stockObj = arr.getJSONObject(0);
		stock.setPrice(stockObj.getString("l"));
		stock.setPriceChange(stockObj.getString("cp") + "%");
	}
}
