package org.stocks.trackerbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackerSource {

	private static final Logger logger = LoggerFactory.getLogger(TrackerSource.class);
	
	private String urlStr = "http://210.176.224.9/testing/service1.asmx/GetData_ST2?idx=0&idx2=StockTracker";

	public String getData() {
		StringBuilder result = new StringBuilder();
		URL url = null;
		try {
			url = new URL(getUrlStr());
		} catch (MalformedURLException e) {
			logger.error("wrong url", e);
			return result.toString();
		}
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
			return result.toString();
		} catch (IOException e) {
			logger.error("network error", e);
			return result.toString();
		}
	}

	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

}
