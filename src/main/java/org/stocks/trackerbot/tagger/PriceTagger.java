package org.stocks.trackerbot.tagger;

import java.math.BigDecimal;
import java.util.Collection;

import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;
import org.stocks.trackerbot.util.NumberNormalizer;

public class PriceTagger implements ITagger {

	private BigDecimal upperLimit = new BigDecimal("15");
	private BigDecimal lowerLimit = new BigDecimal("0.1");
	public static String HIGH = "high price";
	public static String LOW = "low price";
	
	public PriceTagger() {
		
	}
	
	public PriceTagger(BigDecimal upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	@Override
	public void tag(TrackerData data) {
		tag(data.getUps());
//		tag(data.getAos());
		tag(data.getPendings());
//		tag(data.getMcs());
		tag(data.getNewHighs());
//		tag(data.getPullBacks());
	}

	@Override
	public void tag(Collection<Stock> stocks) {
		for (Stock stock : stocks) {
			BigDecimal price = NumberNormalizer.normalize(stock.getPrice());
			if (price != null)  {
				if (price.compareTo(upperLimit) > 0) {
					stock.getTags().add(HIGH);
				}
				if (price.compareTo(lowerLimit) < 0) {
					stock.getTags().add(LOW);
				}
			}
		}
	}
	
}
