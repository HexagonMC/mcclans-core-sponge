package nl.riebie.mcclans.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import nl.riebie.mcclans.database.query.QueryValue.DataType;

public class CreateQuery extends TableQuery{

	private Map<String, DataType> addedColumns = new HashMap<String, DataType>();

	public CreateQuery(String tableName, Connection connection) {
		super(tableName, connection);
	}

	public CreateQuery column(String key, DataType dataType) {
		addedColumns.put(key, dataType);
		return this;
	}
	
	@Override
	public PreparedStatement create() {
		// TODO Auto-generated method stub
		return null;
	}

}
