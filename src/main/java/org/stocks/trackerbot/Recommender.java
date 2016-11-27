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
			for (Stock ed : recommended) {
				if (ing.getTags().contains(MarkedTagger.MARKED)) {
					// marked always display
					continue;
				}
				if (ing.getSymbol().endsWith(ed.getSymbol()) || ed.getSymbol().endsWith(ing.getSymbol())) {
					iterator.remove();
					break;
				}
			}
		}
		getRecommended().addAll(recommending);

		for (ITagger t : this.expensiveTaggers) {
			t.tag(recommending);
		}

		String msg = getLatestRecommendMessage(recommending);
		return msg;
	}

	public String summarize() {
		StringBuilder msg = new StringBuilder();
		msg.appendCodePoint(Emoji.barchart);
		msg.append("Today summary:\n");
		msg.append("----------------\n");
		for (Stock f : this.getRecommended()) {
			// update latest price
			yFin.update(f);
//			msg = msg.appendCodePoint(Tag2Emoji.mapTag(f.getCategory().name()));
			for (String t : f.getTags()) {
				Integer i = Tag2Emoji.mapTag(t);
				if (i != null) {
					msg = msg.appendCodePoint(i);
				}
			}
			msg = msg.append(" " + f.printSummary() + "\n");
		}
		return msg.toString();
	}

	private String getLatestRecommendMessage(Collection<Stock> latest) {
		StringBuilder msg = new StringBuilder();
		for (Stock f : latest) {
//			msg = msg.appendCodePoint(Tag2Emoji.mapTag(f.getCategory().name()));
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

	public void setRecommended(Set<Stock> recommended) {
		this.recommended = recommended;
	}

}
