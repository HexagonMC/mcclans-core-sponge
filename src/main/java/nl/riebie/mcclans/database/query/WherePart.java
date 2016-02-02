package nl.riebie.mcclans.database.query;

import java.util.List;

public class WherePart {

	private final String whereString;
	private final List<QueryValue> queryValues;

	public WherePart(String whereString, List<QueryValue> queryValues) {
		this.whereString = whereString;
		this.queryValues = queryValues;
	}

	public String getWhereString() {
		return whereString;
	}

	public List<QueryValue> getQueryValues() {
		return queryValues;
	}

}
