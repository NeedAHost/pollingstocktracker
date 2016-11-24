package org.stocks.trackerbot.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.hkex.Shareholding;

public class ShareholdingDao extends AbstractDao {

	private static final Logger logger = LoggerFactory.getLogger(ShareholdingDao.class);

	public ShareholdingDao() {

	}

	public boolean insert(List<Shareholding> sh) {
		if (sh == null || sh.isEmpty()) {
			return true;
		}
		String q = "INSERT INTO shareholding (share_id, participant_id, date, percent) VALUES ";
		for (Shareholding s : sh) {
			q += "('" + s.getShareId() + "','" + s.getParticipantId() + "','" + s.getDateStr() + "',"
					+ s.getPercentX100() + "),";
		}
		q = q.substring(0, q.length() - 1);
		return this.executeUpdate(q) > 0;
	}
}
