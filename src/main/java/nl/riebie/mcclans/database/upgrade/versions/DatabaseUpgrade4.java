package nl.riebie.mcclans.database.upgrade.versions;

import nl.riebie.mcclans.database.upgrade.DatabaseUpgrade;

public class DatabaseUpgrade4 extends DatabaseUpgrade{

	public DatabaseUpgrade4() {
		super(4);
	}

	@Override
	public void upgradeDatabase() {
		System.out.println(getVersion());
		
	}

}
