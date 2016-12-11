package org.stocks.trackerbot.tagger;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.dao.MarkedStockDao;
import org.stocks.trackerbot.model.MarkedStock;
import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;

public class MarkedTagger implements ITagger {
	private static final Logger logger = LoggerFactory.getLogger(MarkedTagger.class);

	public static final String MARKED = "marked";
	private MarkedStockDao dao = new MarkedStockDao();
	private Set<MarkedStock> marked = Collections.synchronizedSet(new LinkedHashSet<MarkedStock>());

	public MarkedTagger() {
	}

	@Override
	public void tag(TrackerData data) {
		marked = dao.getAll();
		tag(data.getUps());
		tag(data.getNewHighs());
		tag(data.getPendings());
		tag(data.getPullBacks());
	}

	@Override
	public void tag(List<Stock> stocks) {
		for (Stock s : stocks) {
			for (MarkedStock ms : marked) {
				if (ms.getSymbol().endsWith(s.getSymbolPadded())) {
					s.getTags().add(MARKED);
					s.setMarked(ms);
					ms.setStock(s);
					break;
				}
			}			
		}
	}

}
