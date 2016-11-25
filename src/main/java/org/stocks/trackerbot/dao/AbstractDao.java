package org.stocks.trackerbot.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDao {

	private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);

	private String connectionStr = "jdbc:sqlite:C:/Users/Main/Documents/trackerbot/ccass.db"; // local
//	private String connectionStr = "jdbc:sqlite:/Users/cy/Documents/workspace/ccass.db"; // local2

	public boolean isTableExist(String tableName) {
		Connection con = null;
		try {
			con = DriverManager.getConnection(getConnectionStr());
			DatabaseMetaData md = con.getMetaData();
			ResultSet rs = md.getTables(null, null, "%", null);
			while (rs.next()) {
				if (tableName.equalsIgnoreCase(rs.getString(3))) {
					return true;
				}
			}
		} catch (SQLException e) {
			logger.error("db error", e);
			return false;
		} finally {
			try {
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error("db close fail", e);
			}
		}
		return false;
	}

	public int executeUpdate(String sql) {
		Connection con = null;
		Statement st = null;
		try {
			con = DriverManager.getConnection(getConnectionStr());
			if (con == null) {
				return 0;
			}
			st = con.createStatement();
			return st.executeUpdate(sql);
		} catch (SQLException e) {
			logger.error("db error", e);
			return 0;
		} finally {
			try {
				if (st != null) {
					st.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				logger.error("db close fail", e);
			}
		}
	}
	
	public String getConnectionStr() {
		return connectionStr;
	}

	public void setConnectionStr(String connectionStr) {
		this.connectionStr = connectionStr;
	}

}
