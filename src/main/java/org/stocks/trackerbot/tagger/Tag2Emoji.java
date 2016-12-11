package org.stocks.trackerbot.tagger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.stocks.trackerbot.model.Category;
import org.stocks.trackerbot.model.Emoji;

public class Tag2Emoji {

	private static Map<String, Integer> map = Collections.synchronizedMap(new HashMap<String, Integer>());
	
	static {
		map.put(BlackList.HIGH_RISK, Emoji.dragon);
		map.put(BlackList.MEDIUM_RISK, Emoji.crocodile);
		map.put(BlackList.RISK, Emoji.snake);
		map.put(BlackList.SUSPECTED, Emoji.bug);
		map.put(BlackList.GEM, Emoji.gem);
		map.put(BlackList.CLEAN, Emoji.rabbit);
		map.put(HighOpenTagger.HIGH, Emoji.mountain);
		
		map.put(Category.UP.name(), Emoji.volcano);
		map.put(Category.PENDING.name(), Emoji.fire);
		map.put(Category.NEW_HIGH.name(), Emoji.rocket);
		map.put(Category.PULL_BACK.name(), Emoji.scream);
		map.put(MarkedTagger.MARKED, Emoji.exclamation);
		map.put(VolumeTagger.LOW, Emoji.fallingLeaf);
		map.put(PriceTagger.HIGH, Emoji.moneyBag);
		map.put(PriceTagger.LOW, Emoji.angel);
	}
	
	public static int mapTag(String tag) {
		Integer i = map.get(tag);
		if (i == null) {
			i = Emoji.question;
		}
		return i;
	}
	
	public static Map<String, Integer> getMap() {
		return map;
	}
	
}
