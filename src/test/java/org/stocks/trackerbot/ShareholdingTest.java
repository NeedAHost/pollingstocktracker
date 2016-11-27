package org.stocks.trackerbot;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

public class ShareholdingTest {

	@Test
	public void bigIntTest() {
		BigInteger bi = new BigInteger("2154997299");
		assertEquals("2154997299", bi.toString());
	}
}
