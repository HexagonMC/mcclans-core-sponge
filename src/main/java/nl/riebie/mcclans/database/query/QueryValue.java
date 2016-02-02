package nl.riebie.mcclans.database.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QueryValue {

	public enum DataType {
		INTEGER, STRING, BOOLEAN, LONG, DOUBLE, FLOAT;
	}

	private final DataType dataType;

	private final String dataValue;

	public QueryValue(DataType dataType, String dataValue) {
		this.dataType = dataType;
		this.dataValue = dataValue;
	}

	public void appendValue(PreparedStatement statement, int index) throws SQLException {
		index = index + 1;
		switch (dataType) {
		case BOOLEAN:
			statement.setBoolean(index, dataValue.equals("1"));
			break;
		case INTEGER:
			statement.setInt(index, Integer.parseInt(dataValue));
			break;
		case STRING:
			statement.setString(index, dataValue);
			break;
		case DOUBLE:
			statement.setDouble(index, Double.parseDouble(dataValue));
			break;
		case FLOAT:
			statement.setFloat(index, Float.parseFloat(dataValue));
			break;
		case LONG:
			statement.setLong(index, Long.parseLong(dataValue));
			break;
		default:
			break;

		}
	}
}
