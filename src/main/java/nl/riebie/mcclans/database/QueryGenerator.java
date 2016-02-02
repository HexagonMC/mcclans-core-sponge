package nl.riebie.mcclans.database;

import java.sql.Connection;

import nl.riebie.mcclans.database.query.DeleteQuery;
import nl.riebie.mcclans.database.query.InsertQuery;
import nl.riebie.mcclans.database.query.UpdateQuery;

public class QueryGenerator {

	public static InsertQuery createInsertQuery(String tableName, Connection connection) {
		return new InsertQuery(tableName, connection);
	}

	public static UpdateQuery createUpdateQuery(String tableName, Connection connection) {
		return new UpdateQuery(tableName, connection);
	}

	public static DeleteQuery createDeleteQuery(String tableName, Connection connection) {
		return new DeleteQuery(tableName, connection);
	}
}