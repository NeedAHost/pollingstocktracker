package org.stocks.trackerbot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.google.GoogFinanceAPI;
import org.stocks.trackerbot.model.Emoji;
import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;
import org.stocks.trackerbot.tagger.BlackList;
import org.stocks.trackerbot.tagger.HighOpenTagger;
import org.stocks.trackerbot.tagger.ITagger;
import org.stocks.trackerbot.tagger.MarkedTagger;
import org.stocks.trackerbot.tagger.PriceTagger;
import org.stocks.trackerbot.tagger.Tag2Emoji;
import org.stocks.trackerbot.tagger.VolumeTagger;
import org.stocks.trackerbot.yahoo.YFinanceAPI;

public class Recommender {

	private static final Logger logger = LoggerFactory.getLogger(Recommender.class);
	private Set<Stock> recommended = new LinkedHashSet<Stock>();
	private List<ITagger> cheapTaggers = new ArrayList<ITagger>();
	private List<ITagger> expensiveTaggers = new ArrayList<ITagger>();
	private YFinanceAPI yFin = new YFinanceAPI();
	private GoogFinanceAPI goog = new GoogFinanceAPI(); 

	public Recommender() {
		cheapTaggers.add(new MarkedTagger());
		cheapTaggers.add(new VolumeTagger());
		cheapTaggers.add(new PriceTagger());
		cheapTaggers.add(BlackList.INST);
		expensiveTaggers.add(new HighOpenTagger(yFin));
	}

	public void clear() {
		getRecommended().clear();
	}

	public String analyze(TrackerData data) {
		HashSet<Stock> recommending = new HashSet<Stock>(filter(data));
		for (Iterator<Stock> iterator = recommending.iterator(); iterator.hasNext();) {
			Stock ing = iterator.next();
			if (ing.getTags().contains(MarkedTagger.MARKED)) {
				// marked always display
				continue;
			}
			for (Stock ed : recommended) {
				if (ing.getSymbol().endsWith(ed.getSymbol()) || ed.getSymbol().endsWith(ing.getSymbol())) {
					iterator.remove();
					break;
				}
			}
		}
		for (Stock r : recommending) {
			if (!getRecommended().contains(r)) {
				getRecommended().add(r);
			}
		}

		for (ITagger t : this.expensiveTaggers) {
			t.tag(recommending);
		}

		// reorder
		Set<Stock> ordered = new LinkedHashSet<Stock>();
		for (Stock stock : recommending) {
			if (stock.getTags().contains(MarkedTagger.MARKED)) {
				ordered.add(stock);
			}
		}
		recommending.removeAll(ordered);
		// marked at first
		ordered.addAll(recommending);

		String msg = getLatestRecommendMessage(ordered);
		return msg;
	}

	public List<String> summarize() {
		StringBuilder msg = new StringBuilder();
		msg.appendCodePoint(Emoji.barchart);
		msg.append("Today summary:\n");
		msg.append("----------------\n");
		for (Stock f : this.getRecommended()) {
			// update latest price
			goog.update(f);
			// msg =
			// msg.appendCodePoint(Tag2Emoji.mapTag(f.getCategory().name()));
			for (String t : f.getTags()) {
				Integer i = Tag2Emoji.mapTag(t);
				if (i != null) {
					msg = msg.appendCodePoint(i);
				}
			}
			msg = msg.append(" " + f.printSummary() + "\n");
		}
		String[] perLine = msg.toString().split("\\n");
		return divideMessage(perLine);
	}

	private int linePerMessage = 15;

	private List<String> divideMessage(String[] perLine) {
		String temp = "";
		List<String> ret = new ArrayList<String>();
		for (int j = 1; j <= perLine.length; j++) {
			String s = perLine[j - 1];
			temp += s + "\n";
			if (j % linePerMessage == 0) {
				ret.add(temp);
				temp = "";
			}
		}
		if (temp.length() > 0) {
			ret.add(temp);
		}
		return ret;
	}

	private String getLatestRecommendMessage(Collection<Stock> latest) {
		StringBuilder msg = new StringBuilder();
		for (Stock f : latest) {
			// msg =
			// msg.appendCodePoint(Tag2Emoji.mapTag(f.getCategory().name()));
			for (String t : f.getTags()) {
				Integer i = Tag2Emoji.mapTag(t);
				if (i != null) {
					msg = msg.appendCodePoint(i);
				}
			}
			msg = msg.append(" " + f.print() + "\n");
		}
		return msg.toString();
	}

	private List<Stock> filter(TrackerData data) {
		for (ITagger t : this.cheapTaggers) {
			t.tag(data);
		}
		List<Stock> filtered = new ArrayList<Stock>();
		for (Stock up : data.getUps()) {
			Set<String> tags = up.getTags();
			if (tags.contains(MarkedTagger.MARKED) || (!tags.contains(PriceTagger.HIGH)
					&& !tags.contains(PriceTagger.LOW) && !tags.contains(VolumeTagger.LOW))) {
				filtered.add(up);
			}
		}
		for (Stock pending : data.getPendings()) {
			Set<String> tags = pending.getTags();
			if (tags.contains(MarkedTagger.MARKED) || (!tags.contains(PriceTagger.HIGH)
					&& !tags.contains(PriceTagger.LOW) && !tags.contains(VolumeTagger.LOW))) {
				filtered.add(pending);
			}
		}
		for (Stock nh : data.getNewHighs()) {
			Set<String> tags = nh.getTags();
			if (tags.contains(MarkedTagger.MARKED) || (!tags.contains(PriceTagger.HIGH)
					&& !tags.contains(PriceTagger.LOW) && !tags.contains(VolumeTagger.LOW))) {
				filtered.add(nh);
			}
		}
		return filtered;
	}

	public Set<Stock> getRecommended() {
		return recommended;
	}

}
