package nl.riebie.mcclans.database.upgrade;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.riebie.mcclans.database.DatabaseConnectionOwner;
import nl.riebie.mcclans.database.upgrade.versions.DatabaseUpgrade2;
import nl.riebie.mcclans.database.upgrade.versions.DatabaseUpgrade3;
import nl.riebie.mcclans.database.upgrade.versions.DatabaseUpgrade4;

public class DatabaseUpgrader {

	private List<DatabaseUpgrade> allDatabaseUpgrades = new ArrayList<DatabaseUpgrade>();
	private static final int CURRENT_DATABASE_VERSION = 4;

	public DatabaseUpgrader() {
		allDatabaseUpgrades.add(new DatabaseUpgrade2());
		allDatabaseUpgrades.add(new DatabaseUpgrade3());
		allDatabaseUpgrades.add(new DatabaseUpgrade4());
	}

	public void upgradeDatabase() {
		List<DatabaseUpgrade> upgrades = getNewDatabaseUpgrades();

		try {
			DatabaseConnectionOwner.getInstance().startTransaction();
			for (DatabaseUpgrade upgrade : upgrades) {
				upgrade.upgradeDatabase();
				upgrade.execute();
			}
			DatabaseConnectionOwner.getInstance().commitTransaction();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			DatabaseConnectionOwner.getInstance().cancelTransaction();
			e.printStackTrace();
		}
	}

	public boolean databaseNeedsUpgrade() {
		return getLoadedDatabaseVersion() < CURRENT_DATABASE_VERSION;
	}

	private List<DatabaseUpgrade> getNewDatabaseUpgrades() {
		int loadedDatabaseVersion = getLoadedDatabaseVersion();
		List<DatabaseUpgrade> newDatabaseUpgrades = new ArrayList<DatabaseUpgrade>();
		for (DatabaseUpgrade databaseUpgrade : allDatabaseUpgrades) {
			if (databaseUpgrade.getVersion() > loadedDatabaseVersion && databaseUpgrade.getVersion() <= CURRENT_DATABASE_VERSION) {
				newDatabaseUpgrades.add(databaseUpgrade);
			}
		}
		Collections.sort(newDatabaseUpgrades, new DatabaseUpgradeComparator());
		return newDatabaseUpgrades;
	}

	private int getLoadedDatabaseVersion() {
		return 2;
	}

}
