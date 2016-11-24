package org.stocks.trackerbot;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.stocks.trackerbot.model.Stock;

public class StockTest {

	@Test
	public void equalsTest() {
		Stock s1 = new Stock();
		s1.setSymbol("0001");
		Stock s2 = new Stock();
		s2.setSymbol("0001");
		
		assertTrue(s1.equals(s2));
		
		Stock s3 = new Stock();
		s3.setSymbol("0001");
		s3.setName("xxx");
		assertTrue(s1.equals(s3));
		
		Stock s4 = new Stock();
		s4.setSymbol("0002");
		assertTrue(!s1.equals(s4));

		Set<Stock> s = new HashSet<Stock>();
		s.add(s1);
		s.add(s2);
		s.add(s3);
		s.add(s4);
		assertEquals(2, s.size());
	}
	
	@Test
	public void volChangeTest1() {
		Stock s = new Stock();
		s.setVolume("61.11");
		s.setAvgVolume("23.87");
		assertEquals("256%", s.getVolumeChange());
	}
	
	@Test
	public void volChangeTest2() {
		Stock s = new Stock();
		s.setVolume("25.11");
		s.setAvgVolume("23.87");
		assertEquals("105%", s.getVolumeChange());
	}
	
	@Test
	public void volChangeTest3() {
		Stock s = new Stock();
		s.setVolume("15.11");
		s.setAvgVolume("23.87");
		assertEquals("63%", s.getVolumeChange());
	}
	
	@Test
	public void symbolTest() {
		String s1 = "0001";
		String s2 = "1";
		
		assertTrue(s1.endsWith(s2) || s2.endsWith(s1));
	}
}
