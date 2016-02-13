package nl.riebie.mcclans.table;

import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.List;


public class Row {
	private HashMap<String, Text> columns = new HashMap<>();
	private boolean horizontal;
	private double spaceLength = -1;

	public Row(List<Column> columns) {
		for (Column column : columns) {
			this.columns.put(column.key, Text.EMPTY);
		}
		horizontal = true;
	}
	public Row(){
		horizontal = false;
	}

	public void setValue(String key, Text value) {
		if (!horizontal || columns.containsKey(key)) {
			columns.put(key, value);
		}
	}
	
	protected Text getValue(String key) {
		if (columns.containsKey(key)) {
			return columns.get(key);
		}
		return Text.EMPTY;
	}
	
	public double getSpaceLength(){
		return spaceLength;
	}
	
	public void setSpaceLength(double spaceLength){
		this.spaceLength = spaceLength;
	}

}
