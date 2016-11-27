package org.stocks.trackerbot.dao;

public class SchemaInitializer extends AbstractDao {

	public void createSchema() {
		// @formatter:off
		executeUpdate(
				"CREATE TABLE IF NOT EXISTS share ("
				+ "id TEXT NOT NULL, "
				+ "name TEXT NOT NULL, "
				+ "PRIMARY KEY (id))");

		executeUpdate(
				"CREATE TABLE IF NOT EXISTS participant ("
				+ "id TEXT NOT NULL, "
				+ "name TEXT NOT NULL, "
				+ "PRIMARY KEY (id))");

		executeUpdate(
				"CREATE TABLE IF NOT EXISTS shareholding (" 
				+ "share_id TEXT NOT NULL, "
				+ "participant_id TEXT NOT NULL, " 
				+ "date DATE NOT NULL, " 
				+ "value INT NOT NULL, "
				+ "percent INT NOT NULL, "
				+ "PRIMARY KEY (share_id, participant_id, date), "
				+ "FOREIGN KEY (share_id) REFERENCES share(id))");
		// @formatter:on
	}

}
