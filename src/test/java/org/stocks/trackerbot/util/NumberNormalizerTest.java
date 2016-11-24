package org.stocks.trackerbot.util;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.stocks.trackerbot.util.NumberNormalizer;

public class NumberNormalizerTest {

	@Test
	public void allCases() {
		Assert.assertTrue(NumberNormalizer.normalize("29.03K").compareTo(new BigDecimal("29030")) == 0);
		Assert.assertTrue(NumberNormalizer.normalize("2.47億").compareTo(new BigDecimal("247000000")) == 0);
		Assert.assertTrue(NumberNormalizer.normalize("-0.3x%").compareTo(new BigDecimal("-0.003")) == 0);
		Assert.assertTrue(NumberNormalizer.normalize("-9.xM").compareTo(new BigDecimal("-9000000")) == 0);
	}
	
}
