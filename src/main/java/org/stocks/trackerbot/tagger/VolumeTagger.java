package org.stocks.trackerbot.tagger;

import java.math.BigDecimal;
import java.util.List;

import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;
import org.stocks.trackerbot.util.NumberNormalizer;

public class VolumeTagger implements ITagger {

	private BigDecimal lowerLimit = new BigDecimal("8000000");
	public static final String LOW = "low avg volume";

	public VolumeTagger() {

	}

	public VolumeTagger(BigDecimal lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	@Override
	public void tag(TrackerData data) {
		tag(data.getUps());
//		tag(data.getAos());
		tag(data.getPendings());
		// tag(data.getMcs());
		tag(data.getNewHighs());
		tag(data.getPullBacks());
	}

	@Override
	public void tag(List<Stock> stocks) {
		for (Stock stock : stocks) {
			if (stock.getAvgVolume() != null) {
				BigDecimal avgVolume = NumberNormalizer.normalize(stock.getAvgVolume());
				if (avgVolume.compareTo(lowerLimit) < 0) {
					stock.getTags().add(LOW);
				}
			} else if (stock.getVolume() != null) {
				BigDecimal volume = NumberNormalizer.normalize(stock.getVolume());
				if (volume.compareTo(lowerLimit) < 0) {
					stock.getTags().add(LOW);
				}
			}
		}
	}

}
