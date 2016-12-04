package org.stocks.trackerbot.tagger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;

public class BlackList implements ITagger {

	private static final Logger logger = LoggerFactory.getLogger(BlackList.class);
	private Map<String, List<String>> blackList = new HashMap<String, List<String>>();
	private List<String> headers = new ArrayList<String>();

	public static final String CLEAN = "clean";
	public static final String GEM = "gem";
	public static final BlackList INST = new BlackList();
	public static final String RISK = "低度不適合投資";
	public static final String HIGH_RISK = "高度不適合投資";
	public static final String MEDIUM_RISK = "中度不適合投資";
	public static final String SUSPECTED = "垃圾收購/懷疑假數";

	private BlackList() {
		try {
			InputStream in = getClass().getResourceAsStream("/blacklist.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			int i = 1;
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				if (i == 1) {
					setupHeader(values);
				} else {
					setupValues(values);
				}
				i++;
			}
		} catch (Exception e) {
			logger.error("blacklist init fail", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.stocks.trackerbot.tagger.ITagger#tag(org.stocks.trackerbot.model.
	 * TrackerData)
	 */
	@Override
	public void tag(TrackerData data) {
		tag(data.getUps());
//		tag(data.getAos());
		// tag(data.getMcs());
		tag(data.getNewHighs());
		tag(data.getPendings());
		// tag(data.getPullBacks());
	}
	
	@Override
	public void tag(List<Stock> stocks) {
		for (Stock s : stocks) {
			String scan = scan(s.getSymbol());
			s.getTags().add(scan);
		}
	}

	public String scan(String symbol) {
		if (symbol == null) {
			return CLEAN;
		}
		symbol = symbol.toLowerCase().replace(".hk", "");
		symbol = String.format("%04d", Integer.parseInt(symbol));
		if (symbol.startsWith("8") && symbol.length() == 4) {
			return GEM;
		}
		for (Entry<String, List<String>> entry : blackList.entrySet()) {
			for (String blackListed : entry.getValue()) {
				if (symbol.equals(blackListed)) {
					return entry.getKey();
				}
			}
		}
		return CLEAN;
	}

	private void setupValues(String[] values) {
		int j = 0;
		for (String v : values) {
			blackList.get(headers.get(j)).add(v);
			j++;
		}
	}

	private void setupHeader(String[] values) {
		for (String v : values) {
			blackList.put(v, new ArrayList<String>());
			headers.add(v);
		}
	}

}
