package org.stocks.trackerbot;

import org.junit.Test;
import org.stocks.trackerbot.api.YFinanceAPI;
import org.stocks.trackerbot.model.Stock;

public class YahooTest {

	@Test
	public void updateTest() {
		Stock s = new Stock();
		s.setSymbol("136");
		YFinanceAPI yFin = new YFinanceAPI();
		yFin.update(s);
		System.out.println(s.getPrevClose() + " " + s.getOpen() + " " + s.getPrice() + " " + s.getPriceChange());
	}
}
