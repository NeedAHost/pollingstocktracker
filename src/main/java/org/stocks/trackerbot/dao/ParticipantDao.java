package org.stocks.trackerbot.dao;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stocks.trackerbot.model.hkex.Participant;

public class ParticipantDao extends AbstractDao {

	private static final Logger logger = LoggerFactory.getLogger(ParticipantDao.class);

	public void preload() {
		try {
			InputStream in = getClass().getResourceAsStream("/participants.csv");
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

			List<Participant> participants = new ArrayList<Participant>();
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(",");
				Participant p = new Participant();
				p.setId(values[0]);
				p.setName(values[1]);
				participants.add(p);
			}
			insert(participants);
		} catch (Exception e) {
			logger.error("shares init fail", e);
		}
	}

	public boolean insert(List<Participant> ps) {
		if (ps == null || ps.isEmpty()) {
			return true;
		}
		String q = "INSERT INTO participant (id, name) VALUES ";
		for (Participant p : ps) {
			q += "('" + p.getId() + "','" + p.getName().replace("'", " ") + "'),";
		}
		q = q.substring(0, q.length() - 1);
		return this.executeUpdate(q) > 0;
	}

	public Participant get(String id) {
		Participant ret = null;
		String q = "SELECT * FROM participant WHERE id = ?";
		try (Connection con = DriverManager.getConnection(getConnectionStr());
				PreparedStatement ps = con.prepareStatement(q)) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				ret = new Participant();
				ret.setId(rs.getString("id"));
				ret.setName(rs.getString("name"));
				break;
			}
			return ret;
		} catch (Exception e) {
			logger.error("participant get all error", e);
			return ret;
		}
	}

}
