/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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
