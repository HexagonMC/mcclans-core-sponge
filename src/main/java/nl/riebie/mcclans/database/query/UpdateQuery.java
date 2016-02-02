package nl.riebie.mcclans.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class UpdateQuery extends Query {

	public UpdateQuery(String tableName, Connection connection) {
		super(tableName, connection);
	}

	@Override
	public PreparedStatement create() {
		List<QueryValue> parameters = new ArrayList<QueryValue>();

		String values = "";
		boolean firstRun = true;
		for (Entry<String, QueryValue> keyValuePair : getValues().entrySet()) {
			if (!firstRun) {
				values += ",";
			} else {
				firstRun = false;
			}
			parameters.add(keyValuePair.getValue());
			values += keyValuePair.getKey() + "=" + "?";
		}
		WherePart wherePart = getWherePart();
		String query = "UPDATE " + getTableName() + " SET " + values;
		if (wherePart != null) {
			query += " " + wherePart.getWhereString();
			parameters.addAll(wherePart.getQueryValues());
		}
		try {
			PreparedStatement preparedStatement = getConnection().prepareStatement(query);
			for (int i = 0; i < parameters.size(); i++) {
				parameters.get(i).appendValue(preparedStatement, i);
			}

			return preparedStatement;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void execute() {

	}

}
