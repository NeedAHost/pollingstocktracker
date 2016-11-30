package org.stocks.trackerbot.util;

import java.math.BigDecimal;

public class NumberNormalizer {

	public static BigDecimal normalize(String num) {
		if (num == null) {
			return null;
		}
		num = num.replace("x", "0");
		if (num.endsWith("%")) {
			return new BigDecimal(num.substring(0, num.length() - 1)).divide(new BigDecimal(100));
		}
		if (num.endsWith("億")) {
			return new BigDecimal(num.substring(0, num.length() - 1)).multiply(new BigDecimal(100000000));
		}
		if (num.endsWith("K")) {
			return new BigDecimal(num.substring(0, num.length() - 1)).multiply(new BigDecimal(1000));
		}
		if (num.endsWith("M")) {
			return new BigDecimal(num.substring(0, num.length() - 1)).multiply(new BigDecimal(1000000));
		}
		if (num.endsWith("B")) {
			return new BigDecimal(num.substring(0, num.length() - 1)).multiply(new BigDecimal(1000000000));
		}
		return new BigDecimal(num);
	}
	
}
