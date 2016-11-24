package org.stocks.trackerbot.tagger;

import java.util.Collection;

import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;

public interface ITagger {

	void tag(TrackerData data);

	void tag(Collection<Stock> stocks);
	
}