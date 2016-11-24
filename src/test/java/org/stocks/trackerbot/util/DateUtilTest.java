package org.stocks.trackerbot.util;

import static org.junit.Assert.*;

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
}
