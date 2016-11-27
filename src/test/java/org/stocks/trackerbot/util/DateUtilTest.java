package org.stocks.trackerbot.util;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

public class DateUtilTest {

	@Test
	public void getDateRangeTest() {
		List<String> dates = DateUtil.getDateStrInRange("2016-11-27", "2016-12-02");
		assertEquals(6, dates.size());
		assertEquals("2016-11-27", dates.get(0));
		assertEquals("2016-11-28", dates.get(1));
		assertEquals("2016-11-29", dates.get(2));
		assertEquals("2016-11-30", dates.get(3));
		assertEquals("2016-12-01", dates.get(4));
		assertEquals("2016-12-02", dates.get(5));
	}
	
	@Test
	public void getLastYearMonthRangeTest() {
		List<String> months = DateUtil.getMonthStrFromLastYear();
		assertEquals(12, months.size());
	}
	
	@Test
	public void getMonthRangeTest() {
		List<String> months = DateUtil.getMonthStrInRange("2015-11-27", "2016-11-26");
		assertEquals(13, months.size());
		assertEquals("2016-11-01", months.get(0));
		assertEquals("2016-10-01", months.get(1));
		assertEquals("2016-09-01", months.get(2));
		assertEquals("2016-08-01", months.get(3));
		assertEquals("2016-07-01", months.get(4));
		assertEquals("2016-06-01", months.get(5));
		assertEquals("2016-05-01", months.get(6));
		assertEquals("2016-04-01", months.get(7));
		assertEquals("2016-03-01", months.get(8));
		assertEquals("2016-02-01", months.get(9));
		assertEquals("2016-01-01", months.get(10));
		assertEquals("2015-12-01", months.get(11));
		assertEquals("2015-11-01", months.get(12));
	}
	
	@Test
	public void dateDiffTest() {
		LocalDate d1 = LocalDate.of(2016, 11, 22);		
		LocalDate d2 = LocalDate.of(2016, 11, 23);
		LocalDate d3 = LocalDate.parse("2016-11-22");
		assertTrue(!d1.equals(d2));
		assertTrue(d1.equals(d3));
		assertTrue(!d3.equals(d2));
	}
}
