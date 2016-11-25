package org.stocks.trackerbot.hkex;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.dao.SchemaInitializer;
import org.stocks.trackerbot.dao.ShareDao;
import org.stocks.trackerbot.dao.ShareholdingDao;
import org.stocks.trackerbot.model.hkex.Share;
import org.stocks.trackerbot.model.hkex.Shareholding;
import org.stocks.trackerbot.util.DateUtil;

public class HkexTracker {
	private static final Logger logger = LoggerFactory.getLogger(HkexTracker.class);

	private ShareDao shareDao = new ShareDao();
	private ShareholdingDao shareholdingDao = new ShareholdingDao();
	private SchemaInitializer initializer = new SchemaInitializer();
	private HkexWeb web = new HkexWeb();

	public HkexTracker() {
		initializer.createSchema();
		shareDao.preload();
	}

	public void loadHistorial() {
		List<Share> allShares = shareDao.getAll();
		List<String> wholeYear = DateUtil.getDateStrFromLastYear();
		logger.info("loading " + allShares.size() + " shares in " + wholeYear.size() + " days..");
		
		allShares = new ArrayList<Share>();
		Share s = new Share();
		Share s2 = new Share();
		s.setId("00705");
		s2.setId("00575");
		allShares.add(s);
		allShares.add(s2);
		
		for (Share share : allShares) {
			for (String date : wholeYear) {
				logger.info(share.getId() + " " + date);
				List<Shareholding> shareholdings = web.get(share.getId(), date);
				shareholdingDao.insert(shareholdings);
			}
		}
	}
}
