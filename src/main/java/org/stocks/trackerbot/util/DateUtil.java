package org.stocks.trackerbot.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
	
	public static String getTodayDateStr() {
		LocalDate today = LocalDate.now();
		return today.format(formatter);
	}

	public static List<String> getDateStrInRange(String start, String end) {
		LocalDate startD = LocalDate.parse(start);
		LocalDate endD = LocalDate.parse(end);
		return getDateStrInRange(startD, endD);
	}
	
	public static List<String> getMonthStrInRange(String start, String end) {
		LocalDate startD = LocalDate.parse(start);
		LocalDate endD = LocalDate.parse(end);
		return getMonthStrInRange(startD, endD);
	}
	
	public static List<String> getMonthStrInRange(LocalDate start, LocalDate end) {
		List<String> dates = new ArrayList<String>();
		LocalDate startM = start.withDayOfMonth(1);
		LocalDate endM = end.withDayOfMonth(1);
		while (!startM.isAfter(endM)) {
			dates.add(endM.format(formatter));
			endM = endM.plusMonths(-1).withDayOfMonth(1);
		}

		return dates;
	}
	
	public static List<String> getDateStrInRange(LocalDate start, LocalDate end) {
		List<String> dates = new ArrayList<String>();

		while (!start.isAfter(end)) {
			dates.add(start.format(formatter));
			start = start.plusDays(1);
		}

		return dates;
	}
	
	public static List<String> getMonthStrFromLastYear() {
		LocalDate now = LocalDate.now();
		LocalDate lastYear = now.plus(-1, ChronoUnit.YEARS).plus(1, ChronoUnit.MONTHS);
		return getMonthStrInRange(lastYear, now);
	}
	
	public static List<String> getDateStrFromLastYear() {
		LocalDate now = LocalDate.now();
		LocalDate lastYear = now.plus(-1, ChronoUnit.YEARS).plus(1, ChronoUnit.DAYS);
		return getDateStrInRange(lastYear, now);
	}

}
