package nl.riebie.mcclans.database.upgrade.versions;

import nl.riebie.mcclans.database.upgrade.DatabaseUpgrade;

public class DatabaseUpgrade3 extends DatabaseUpgrade{

	public DatabaseUpgrade3() {
		super(3);
	}

	@Override
	public void upgradeDatabase() {
		System.out.println(getVersion());
		
	}

}
