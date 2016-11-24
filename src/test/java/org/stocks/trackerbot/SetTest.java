package org.stocks.trackerbot;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.stocks.trackerbot.model.Stock;

public class SetTest {

	@Test
	public void test() {
		Set<Stock> s = new HashSet<Stock>();
		s.add(new Stock("1"));
		s.add(new Stock("2"));
		s.add(new Stock("3"));
		
		Set<Stock> s1 = new HashSet<Stock>();
		s1.add(new Stock("1"));
		s1.add(new Stock("2"));
		
		s.removeAll(s1);
		
		assertEquals(1, s.size());
	}
	
	@Test
	public void test2() {
		List<Stock> s = new ArrayList<Stock>();
		s.add(new Stock("1"));
		s.add(new Stock("2"));
		s.add(new Stock("3"));
		
		Set<Stock> s1 = new HashSet<Stock>();
		s1.add(new Stock("1"));
		s1.add(new Stock("2"));
		
		s.removeAll(s1);
		s1.addAll(s);
		
		assertEquals(1, s.size());
		assertEquals(3, s1.size());
	}
	
}
