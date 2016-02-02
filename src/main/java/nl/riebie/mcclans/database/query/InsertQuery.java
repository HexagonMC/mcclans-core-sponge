package nl.riebie.mcclans.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class InsertQuery extends Query {

	public InsertQuery(String tableName, Connection connection) {
		super(tableName, connection);
	}

	@Override
	public PreparedStatement create() {
		String fields = "";
		String values = "";
		boolean firstRun = true;
		List<QueryValue> queryValues = new ArrayList<QueryValue>();

		for (Entry<String, QueryValue> keyValuePair : getValues().entrySet()) {
			if (!firstRun) {
				fields += ",";
				values += ",";
			} else {
				firstRun = false;
			}
			fields += keyValuePair.getKey();
			values += "?";
			queryValues.add(keyValuePair.getValue());
		}

		String query = "INSERT INTO " + getTableName() + " (" + fields + ") VALUES (" + values + ")";
		try {
			PreparedStatement preparedStatement = getConnection().prepareStatement(query);
			for (int i = 0; i < queryValues.size(); i++) {
				queryValues.get(i).appendValue(preparedStatement, i);
			}

			return preparedStatement;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
