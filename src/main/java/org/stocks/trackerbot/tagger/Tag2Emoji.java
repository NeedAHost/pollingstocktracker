package org.stocks.trackerbot.tagger;

import java.util.HashMap;
import java.util.Map;

import org.stocks.trackerbot.model.Category;
import org.stocks.trackerbot.model.Emoji;

public class Tag2Emoji {

	private static Map<String, Integer> map = new HashMap<String, Integer>();
	
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
	}
	
	public static int mapTag(String tag) {
		return map.get(tag);
	}
	
}
