package nl.riebie.mcclans.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;

public abstract class TableQuery {
	
	private String tableName;
	private Connection connection;

	public TableQuery(String tableName, Connection connection) {
		this.tableName = tableName;
		this.connection = connection;
	}
	
	protected String getTableName() {
		return tableName;
	}

	protected Connection getConnection() {
		return connection;
	}

	public abstract PreparedStatement create();

}
