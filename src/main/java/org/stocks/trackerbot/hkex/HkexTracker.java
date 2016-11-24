package org.stocks.trackerbot.hkex;

import java.util.List;

import org.stocks.trackerbot.dao.SchemaInitializer;
import org.stocks.trackerbot.dao.ShareDao;
import org.stocks.trackerbot.dao.ShareholdingDao;
import org.stocks.trackerbot.model.hkex.Share;
import org.stocks.trackerbot.model.hkex.Shareholding;
import org.stocks.trackerbot.util.DateUtil;

public class HkexTracker {

	private ShareDao shareDao = new ShareDao();
	private ShareholdingDao shareholdingDao = new ShareholdingDao();
	private SchemaInitializer initializer = new SchemaInitializer();
	private HkexWeb web = new HkexWeb();

	public HkexTracker() {
		initializer.createSchema();
//		shareDao.preload();
	}

	public void loadHistorial() {
		List<Share> allShares = shareDao.getAll();
		List<String> wholeYear = DateUtil.getDateStrFromLastYear();
		for (Share share : allShares) {
			for (String date : wholeYear) {
				System.out.println(share.getId() + " " + date);
				List<Shareholding> shareholdings = web.get(share.getId(), date);
				shareholdingDao.insert(shareholdings);
			}
		}
	}
}
