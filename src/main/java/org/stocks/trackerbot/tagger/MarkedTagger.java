package org.stocks.trackerbot.tagger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.Config;
import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;

public class MarkedTagger implements ITagger {
	private static final Logger logger = LoggerFactory.getLogger(MarkedTagger.class);
	
	public static final String MARKED = "marked";
	private List<String> marked = new ArrayList<String>();

	public MarkedTagger() {
		File file = new File(Config.markedFilePath);
		try (InputStream is = new FileInputStream(file);
				InputStreamReader isr = new InputStreamReader(is, "UTF-8");
				BufferedReader br = new BufferedReader(isr);) {
			List<String> temp = new ArrayList<String>();
			String line = null;
			while ((line = br.readLine()) != null) {
				temp.add(String.format("%04d", Integer.parseInt(line)));
			}
			setMarked(temp);
		} catch (Exception e) {
			logger.error("marked tagger init fail", e);
		}
	}
	
	public List<String> getMarked() {
		return marked;
	}

	public void setMarked(List<String> marked) {
		this.marked = marked;
	}

	@Override
	public void tag(TrackerData data) {
		tag(data.getUps());
		tag(data.getNewHighs());
		tag(data.getPendings());
	}

	@Override
	public void tag(Collection<Stock> stocks) {
		for (Stock s : stocks) {
			if (marked.contains(s.getSymbolPadded())) {
				s.getTags().add(MARKED);
			}
		}
	}

}
