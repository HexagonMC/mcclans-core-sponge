package nl.riebie.mcclans.table;

public class Column {

	public String key;
	public int spacing;
	public boolean trim;

	public Column(String key, int spacing, boolean trim) {
		this.key = key;
		this.spacing = spacing;
		this.trim = trim;
	}

}
