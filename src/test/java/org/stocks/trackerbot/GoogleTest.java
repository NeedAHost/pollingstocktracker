package org.stocks.trackerbot;

import static org.junit.Assert.*;

import org.junit.Test;
import org.stocks.trackerbot.api.GoogFinanceAPI;
import org.stocks.trackerbot.model.Stock;

public class GoogleTest {

	@Test
	public void updateStock() {
		Stock s = new Stock();
		s.setSymbol("1");
		GoogFinanceAPI goog = new GoogFinanceAPI();
		goog.update(s);
		System.out.println(s.getPrice());
		System.out.println(s.getPriceChange());
		assertTrue(s.getPrice() != null);
//		assertTrue(s.getPriceChange() != null);
	}
	
}
