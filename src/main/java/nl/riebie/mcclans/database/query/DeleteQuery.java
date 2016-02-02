package nl.riebie.mcclans.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteQuery extends Query {

	public DeleteQuery(String tableName, Connection connection) {
		super(tableName, connection);
	}

	@Override
	public PreparedStatement create() {

		try {
			WherePart wherePart = getWherePart();
			if (wherePart == null) {
				return null;
			}
			String query = "DELETE FROM " + getTableName() + " " + wherePart.getWhereString();
			PreparedStatement preparedStatement = getConnection().prepareStatement(query);
			for (int i = 0; i < wherePart.getQueryValues().size(); i++) {
				wherePart.getQueryValues().get(i).appendValue(preparedStatement, i);
			}

			return preparedStatement;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
