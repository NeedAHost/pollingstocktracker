package org.stocks.trackerbot.hkex;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.dao.ParticipantDao;
import org.stocks.trackerbot.dao.SchemaInitializer;
import org.stocks.trackerbot.dao.ShareDao;
import org.stocks.trackerbot.dao.ShareholdingDao;
import org.stocks.trackerbot.model.hkex.Share;
import org.stocks.trackerbot.model.hkex.Shareholding;
import org.stocks.trackerbot.util.DateUtil;

public class HkexTracker {
	private static final Logger logger = LoggerFactory.getLogger(HkexTracker.class);

	private ShareDao shareDao = new ShareDao();
	private ParticipantDao participantDao = new ParticipantDao();
	private ShareholdingDao shareholdingDao = new ShareholdingDao();
	private SchemaInitializer initializer = new SchemaInitializer();
	private HkexWeb web = new HkexWeb();

	public HkexTracker() {
		initializer.createSchema();
		shareDao.preload();
		participantDao.preload();
	}
	
	public void loadToday() {
		List<Share> allShares = shareDao.getAll();		
		logger.info("loading " + allShares.size() + " shares in today");
		
		String date = DateUtil.getTodayDateStr();
		load(allShares, date);
	}
	
	public void load(String date) {
		List<Share> allShares = shareDao.getAll();		
		logger.info("loading " + allShares.size() + " shares in " + date);
		
		load(allShares, date);
	}

	private void load(List<Share> shares, String date) {
		for (Share share : shares) {
			logger.info(share.getId() + " " + date);
			List<Shareholding> shareholdings = web.get(share.getId(), date);
			shareholdingDao.insert(shareholdings);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {					
			}
		}
	}
	
	public void loadHistorial(List<Share> shares) {
		List<String> wholeYear = DateUtil.getMonthStrFromLastYear();
		logger.info("loading " + shares.size() + " shares in " + wholeYear.size() + " days..");

		for (Share share : shares) {
			for (String date : wholeYear) {
				logger.info(share.getId() + " " + date);
				List<Shareholding> shareholdings = web.get(share.getId(), date);
				shareholdingDao.insert(shareholdings);
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {					
				}
			}
		}
	}

	public void loadHistorial() {
		List<Share> allShares = shareDao.getAll();
		loadHistorial(allShares);
	}
}
