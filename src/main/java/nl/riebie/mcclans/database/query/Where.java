package nl.riebie.mcclans.database.query;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.riebie.mcclans.database.query.QueryValue.DataType;

public class Where {

	private Map<String, QueryValue> values = new HashMap<String, QueryValue>();
	private Query query;

	public Where(String key, String value, Query query) {
		values.put(key, new QueryValue(DataType.STRING, value));
		this.query = query;
	}

	public Where(String key, int value, Query query) {
		values.put(key, new QueryValue(DataType.INTEGER, Integer.toString(value)));
		this.query = query;
	}

	public Where(String key, double value, Query query) {
		values.put(key, new QueryValue(DataType.DOUBLE, Double.toString(value)));
		this.query = query;
	}

	public Where(String key, long value, Query query) {
		values.put(key, new QueryValue(DataType.LONG, Long.toString(value)));
		this.query = query;
	}

	public Where(String key, float value, Query query) {
		values.put(key, new QueryValue(DataType.FLOAT, Float.toString(value)));
		this.query = query;
	}

	public Where(String key, boolean value, Query query) {
		values.put(key, new QueryValue(DataType.BOOLEAN, value ? "1" : "0"));
		this.query = query;
	}

	public Where and(String key, String value) {
		values.put(key, new QueryValue(DataType.STRING, value));
		return this;
	}

	public Where and(String key, int value) {
		values.put(key, new QueryValue(DataType.INTEGER, Integer.toString(value)));
		return this;
	}

	public Where and(String key, double value) {
		values.put(key, new QueryValue(DataType.DOUBLE, Double.toString(value)));
		return this;
	}

	public Where and(String key, long value) {
		values.put(key, new QueryValue(DataType.LONG, Long.toString(value)));
		return this;
	}

	public Where and(String key, boolean value) {
		values.put(key, new QueryValue(DataType.BOOLEAN, value ? "1" : "0"));
		return this;
	}

	public Where and(String key, float value) {
		values.put(key, new QueryValue(DataType.FLOAT, Float.toString(value)));
		return this;
	}

	public PreparedStatement create() {
		boolean firstRun = true;
		String whereString = "";
		List<QueryValue> queryValues = new ArrayList<QueryValue>();

		for (Entry<String, QueryValue> keyValuePair : values.entrySet()) {
			if (!firstRun) {
				whereString += " AND ";
			} else {
				whereString += "WHERE ";
				firstRun = false;
			}
			queryValues.add(keyValuePair.getValue());
			whereString += keyValuePair.getKey() + "=?";
		}
		query.setWherePart(new WherePart(whereString, queryValues));
		return query.create();

	}

}
