package org.stocks.trackerbot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;
import org.stocks.trackerbot.tagger.BlackList;
import org.stocks.trackerbot.tagger.HighOpenTagger;
import org.stocks.trackerbot.tagger.ITagger;
import org.stocks.trackerbot.tagger.PriceTagger;
import org.stocks.trackerbot.tagger.Tag2Emoji;
import org.stocks.trackerbot.tagger.VolumeTagger;

public class Recommender {

	private Set<Stock> recommended = new HashSet<Stock>();
	private List<ITagger> cheapTaggers = new ArrayList<ITagger>();
	private List<ITagger> expensiveTaggers = new ArrayList<ITagger>();

	public Recommender() {
		cheapTaggers.add(BlackList.INST);
		cheapTaggers.add(new VolumeTagger());
		cheapTaggers.add(new PriceTagger());
		expensiveTaggers.add(new HighOpenTagger());
	}

	public void clear() {
		getRecommended().clear();
	}

	public String analyze(TrackerData data) {
		HashSet<Stock> recommending = new HashSet<Stock>(getFiltered(data));
		for (Iterator<Stock> iterator = recommending.iterator(); iterator.hasNext();) {
			Stock ing = iterator.next();
			for (Stock ed : recommended) {
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
		
		StringBuilder msg = getLatestRecommendMessage(recommending);
		
		return msg.toString();
	}

	private StringBuilder getLatestRecommendMessage(Collection<Stock> latest) {
		StringBuilder msg = new StringBuilder();
		for (Stock f : latest) {
			msg = msg.appendCodePoint(Tag2Emoji.mapTag(f.getCategory().name()));
			for (String t : f.getTags()) {
				Integer i = Tag2Emoji.mapTag(t);
				if (i != null) {
					msg = msg.appendCodePoint(i);
				}
			}
			msg = msg.append(" " + f.print() + "\n");
		}
		return msg;
	}

	private List<Stock> getFiltered(TrackerData data) {
		for (ITagger t : this.cheapTaggers) {
			t.tag(data);
		}
		List<Stock> filtered = new ArrayList<Stock>();
		for (Stock up : data.getUps()) {
			if (!up.getTags().contains(PriceTagger.HIGH) && !up.getTags().contains(PriceTagger.LOW)
					&& !up.getTags().contains(VolumeTagger.LOW)) {
				filtered.add(up);
			}
		}
		for (Stock pending : data.getPendings()) {
			if (!pending.getTags().contains(PriceTagger.HIGH) && !pending.getTags().contains(PriceTagger.LOW)
					&& !pending.getTags().contains(VolumeTagger.LOW)) {
				filtered.add(pending);
			}
		}
		for (Stock nh : data.getNewHighs()) {
			if (!nh.getTags().contains(PriceTagger.HIGH) && !nh.getTags().contains(PriceTagger.LOW)
					&& !nh.getTags().contains(VolumeTagger.LOW)) {
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
