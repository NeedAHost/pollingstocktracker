package org.stocks.trackerbot.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.hkex.Share;

public class ShareDao extends AbstractDao {

	private static final Logger logger = LoggerFactory.getLogger(ShareDao.class);

	public ShareDao() {
	}

	public void preload() {
		try {
			InputStream in = getClass().getResourceAsStream("/shares.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			List<Share> shares = new ArrayList<Share>();
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				Share s = new Share();
				s.setId(values[0]);
				s.setName(values[1]);
				shares.add(s);
			}
			insert(shares);
		} catch (Exception e) {
			logger.error("shares init fail", e);
		}
	}

	public boolean insert(List<Share> s) {
		if (s == null || s.isEmpty()) {
			return true;
		}
		String q = "INSERT INTO share (id, name) VALUES ";
		for (Share share : s) {
			q += "('" + share.getId() + "','" + share.getName().replace("'", " ") + "'),";
		}
		q = q.substring(0, q.length() - 1);
		return this.executeUpdate(q) > 0;
	}

	public List<Share> getAll() {
		List<Share> ret = new ArrayList<Share>();
		String q = "SELECT * FROM share";
		try (Connection con = DriverManager.getConnection(getConnectionStr()); Statement st = con.createStatement()) {
			ResultSet rs = st.executeQuery(q);
			while (rs.next()) {
				Share s = new Share();
				s.setId(rs.getString("id"));
				s.setName(rs.getString("name"));
				ret.add(s);
			}
			return ret;
		} catch (Exception e) {
			logger.error("share get all error", e);
			return ret;
		}
	}

}
