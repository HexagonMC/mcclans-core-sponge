package nl.riebie.mcclans.database;

import java.sql.PreparedStatement;

public class MCClansDatabaseTask {

	private PreparedStatement query;

	public MCClansDatabaseTask(PreparedStatement query) {
		this.query = query;
	}

	public PreparedStatement getQuery() {
		return query;
	}
}