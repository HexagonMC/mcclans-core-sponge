package nl.riebie.mcclans.database.upgrade;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import nl.riebie.mcclans.database.DatabaseConnectionOwner;
import nl.riebie.mcclans.database.query.AlterQuery;
import nl.riebie.mcclans.database.query.CreateQuery;
import nl.riebie.mcclans.database.query.TableQuery;

public abstract class DatabaseUpgrade {

	private int databaseVersion;
	private List<TableQuery> queries = new ArrayList<TableQuery>();

	public DatabaseUpgrade(int version) {
		this.databaseVersion = version;
	}

	public int getVersion() {
		return databaseVersion;
	}

	public abstract void upgradeDatabase();

	public AlterQuery alterTable(String tableName) {
		AlterQuery query = new AlterQuery(tableName, DatabaseConnectionOwner.getInstance().getConnection());
		queries.add(query);
		return query;
	}

	public CreateQuery createTable(String tableName) {
		CreateQuery query = new CreateQuery(tableName, DatabaseConnectionOwner.getInstance().getConnection());
		queries.add(query);
		return query;
	}

	public void dropTable(String tableName) {
	}

	public void execute() throws SQLException {
		for (TableQuery query : queries) {
			PreparedStatement preparedStatement = query.create();
			DatabaseConnectionOwner.getInstance().executeTransactionStatement(preparedStatement);
		}
	}
}
