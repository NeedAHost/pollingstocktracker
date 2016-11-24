package org.stocks.trackerbot.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.TrackerBot;
import org.stocks.trackerbot.model.hkex.Shareholding;

public class ShareholdingDao {

	private static final Logger logger = LoggerFactory.getLogger(TrackerBot.class);
	private String connectionStr = "jdbc:sqlite:C:/Users/Main/Documents/trackerbot/ccass.db"; // local

	private Connection con;

	public ShareholdingDao() {
		try {
			con = DriverManager.getConnection(connectionStr);
			Statement st = con.createStatement();
			st.executeUpdate(
					"CREATE TABLE IF NOT EXISTS shareholding (share_id TEXT NOT NULL, participant_id TEXT NOT NULL, date DATE NOT NULL, percent INT NOT NULL, PRIMARY KEY (share_id, participant_id, date))");
			st.close();
		} catch (SQLException e) {
			logger.error("db error", e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				logger.error("db close fail", e);
			}
		}
	}

	public boolean insert(List<Shareholding> sh) {
		if (sh == null || sh.isEmpty()) {
			return true;
		}
		try {
			con = DriverManager.getConnection(connectionStr);
			Statement st = con.createStatement();
			String q = "INSERT INTO shareholding (share_id, participant_id, date, percent) VALUES ";
			for (Shareholding s : sh) {
				q += "('" + s.getShareId() + "','" + s.getParticipantId() + "','" + s.getDateStr() + "'," + s.getPercentX100() + "),";
			}
			q = q.substring(0, q.length() - 1);
			int updates = st.executeUpdate(q);
			logger.debug(updates + " shareholding inserted");
			st.close();
			return true;
		} catch (SQLException e) {
			logger.error("db error", e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				logger.error("db close fail", e);
			}
		}
		return false;
	}
}
