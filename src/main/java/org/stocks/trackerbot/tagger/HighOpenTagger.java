package org.stocks.trackerbot.tagger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import org.stocks.trackerbot.model.Stock;
import org.stocks.trackerbot.model.TrackerData;
import org.stocks.trackerbot.yahoo.YFinanceAPI;

public class HighOpenTagger implements ITagger {

	private BigDecimal upperLimit = new BigDecimal("103");
	private BigDecimal hundred = new BigDecimal("100");
	public static String HIGH = "high open";
	private YFinanceAPI yFin;
	
	public HighOpenTagger(YFinanceAPI yFin) {
		this.yFin = yFin;
	}

	@Override
	public void tag(TrackerData data) {
		tag(data.getUps());
//		tag(data.getAos());
		tag(data.getPendings());
		// tag(data.getMcs());
		tag(data.getNewHighs());
		// tag(data.getPullBacks());
	}

	@Override
	public void tag(Collection<Stock> stocks) {
		for (Stock stock : stocks) {
			yFin.update(stock);
			if (stock.getOpen() != null && stock.getPrevClose() != null) {
				BigDecimal open = new BigDecimal(stock.getOpen());
				BigDecimal prevClose = new BigDecimal(stock.getPrevClose());
				if (open.multiply(hundred).divide(prevClose, RoundingMode.HALF_UP).compareTo(upperLimit) >= 0) {
					stock.getTags().add(HIGH);
				}
			}
		}
	}
}
