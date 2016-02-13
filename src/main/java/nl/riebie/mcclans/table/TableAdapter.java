package nl.riebie.mcclans.table;

public interface TableAdapter<T> {
	public void fillRow(Row row, T item, int index);

}
