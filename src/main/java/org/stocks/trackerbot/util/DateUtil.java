package org.stocks.trackerbot.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {

	private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

	public static Date getDate(String year, String month, String date) {
		Calendar c = Calendar.getInstance();
		c.set(Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(date), 0, 0);
		return c.getTime();
	}

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static List<String> getDateStrInRange(String start, String end) {
		LocalDate startD = LocalDate.parse(start);
		LocalDate endD = LocalDate.parse(end);
		List<String> dates = new ArrayList<String>();

		while (!startD.isAfter(endD)) {
			dates.add(startD.format(formatter));
			startD = startD.plusDays(1);
		}

		return dates;
	}

}
