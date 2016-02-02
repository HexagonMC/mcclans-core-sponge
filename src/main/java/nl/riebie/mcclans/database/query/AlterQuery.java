package nl.riebie.mcclans.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.riebie.mcclans.database.query.QueryValue.DataType;

public class AlterQuery extends TableQuery {

	private Map<String, DataType> addedColumns = new HashMap<String, DataType>();
	private List<String> droppedColumns = new ArrayList<String>();

	public AlterQuery(String tableName, Connection connection) {
		super(tableName, connection);
	}

	public AlterQuery addColumn(String key, DataType dataType) {
		addedColumns.put(key, dataType);
		return this;
	}

	public AlterQuery dropColumn(String key) {
		droppedColumns.add(key);
		return this;
	}

	//	public AlterQuery addDefaultValue(String key, Object value) { //?
	//		values.put(key, dataType);
	//		return this;
	//	}

	@Override
	public PreparedStatement create() {

		try {

			String query = "ALTER TABLE " + getTableName() + " " + getAddedColumnqueryPart() + " " + getDroppedColumnqueryPart();
			PreparedStatement preparedStatement = getConnection().prepareStatement(query);

			return preparedStatement;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getAddedColumnqueryPart() {
		String stuff = "";
		for (Entry<String, DataType> keyValuePair : addedColumns.entrySet()) {
			stuff += "ADD " + keyValuePair.getValue() + " " + keyValuePair.getKey() + ", ";
		}
		return stuff;
	}

	private String getDroppedColumnqueryPart() {
		String stuff = "";
		for (String key : droppedColumns) {
			stuff += "DROP COLUMN " + key + ", ";
		}
		return stuff;
	}
}
