package nl.riebie.mcclans.database.upgrade.versions;

import nl.riebie.mcclans.database.query.QueryValue.DataType;
import nl.riebie.mcclans.database.upgrade.DatabaseUpgrade;

public class DatabaseUpgrade2 extends DatabaseUpgrade{

	public DatabaseUpgrade2() {
		super(2);
	}

	@Override
	public void upgradeDatabase() {
		System.out.println(getVersion());
		alterTable("MCClans").addColumn("newFeature", DataType.FLOAT).dropColumn("oldFeature");

	}

}
