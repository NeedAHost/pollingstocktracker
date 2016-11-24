package org.stocks.trackerbot;

import org.junit.Assert;
import org.junit.Test;
import org.stocks.trackerbot.tagger.BlackList;

public class BlackListTest {

	@Test
	public void scanTest() {
		Assert.assertEquals(BlackList.GEM, BlackList.INST.scan("8290"));
		Assert.assertEquals("垃圾收購/懷疑假數", BlackList.INST.scan("1039"));
		Assert.assertEquals("低度不適合投資", BlackList.INST.scan("1808"));
		Assert.assertEquals("中度不適合投資", BlackList.INST.scan("6878"));
		Assert.assertEquals("高度不適合投資", BlackList.INST.scan("3989"));
	}
	
}
