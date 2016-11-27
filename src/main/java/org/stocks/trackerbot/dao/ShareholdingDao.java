package org.stocks.trackerbot.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
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
		String q = "INSERT INTO shareholding (share_id, participant_id, date, value, percent) VALUES ";
		for (Shareholding s : sh) {
			q += "('" + s.getShareId() + "','" + s.getParticipantId() + "','" + s.getDateStr() + "',"
					+ s.getValue() + ", " + s.getPercentX100() + "),";
		}
		q = q.substring(0, q.length() - 1);
		return this.executeUpdate(q) > 0;
	}

	public List<Shareholding> getByParticipantAndDate(String participantId, LocalDate date) {
		List<Shareholding> ret = new ArrayList<Shareholding>();
		String q = "SELECT * FROM shareholding sh AND sh.participant_id = ? AND sh.date = ? ORDER BY sh.percent DESC";
		try (Connection con = DriverManager.getConnection(getConnectionStr());
				PreparedStatement ps = con.prepareStatement(q)) {
			ps.setString(1, participantId);
			ps.setDate(2, Date.valueOf(date));
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Shareholding sh = new Shareholding();
				sh.setShareId(rs.getString("share_id"));
				sh.setParticipantId(rs.getString("participant_id"));
				sh.setDate(rs.getDate("date"));
				sh.setPercentX100(rs.getInt("percent"));
				ret.add(sh);
			}
			return ret;
		} catch (Exception e) {
			logger.error("shareholding get by participantId " + participantId + " date " + date + " error", e);
			return ret;
		}
	}

}
