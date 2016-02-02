package nl.riebie.mcclans.database.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

import nl.riebie.mcclans.database.query.QueryValue.DataType;

public abstract class Query {

	private final String tableName;
	private final Connection connection;
	private HashMap<String, QueryValue> values = new HashMap<String, QueryValue>();
	private WherePart wherePart;

	public Query(String tableName, Connection connection) {
		this.tableName = tableName;
		this.connection = connection;
	}

	public Query value(String key, String value) {
		values.put(key, new QueryValue(DataType.STRING, value));
		return this;
	}

	public Query value(String key, int value) {
		values.put(key, new QueryValue(DataType.INTEGER, Integer.toString(value)));
		return this;
	}

	public Query value(String key, long value) {
		values.put(key, new QueryValue(DataType.LONG, Long.toString(value)));
		return this;
	}

	public Query value(String key, double value) {
		values.put(key, new QueryValue(DataType.DOUBLE, Double.toString(value)));
		return this;
	}

	public Query value(String key, float value) {
		values.put(key, new QueryValue(DataType.FLOAT, Float.toString(value)));
		return this;
	}

	public Query value(String key, boolean value) {
		values.put(key, new QueryValue(DataType.BOOLEAN, value ? "1" : "0"));
		return this;
	}

	public Where where(String key, String value) {
		return new Where(key, value, this);
	}

	public Where where(String key, int value) {
		return new Where(key, value, this);
	}

	public Where where(String key, double value) {
		return new Where(key, value, this);
	}

	public Where where(String key, long value) {
		return new Where(key, value, this);
	}

	public Where where(String key, boolean value) {
		return new Where(key, value, this);
	}

	public Where where(String key, float value) {
		return new Where(key, value, this);
	}

	protected Map<String, QueryValue> getValues() {
		return values;
	}

	protected String getTableName() {
		return tableName;
	}

	protected WherePart getWherePart() {
		return wherePart;
	}

	protected void setWherePart(WherePart wherePart) {
		this.wherePart = wherePart;
	}

	public Connection getConnection() {
		return connection;
	}

	public abstract PreparedStatement create();

}
