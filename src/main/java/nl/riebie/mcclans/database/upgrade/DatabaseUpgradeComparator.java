package nl.riebie.mcclans.database.upgrade;

import java.util.Comparator;

public class DatabaseUpgradeComparator implements Comparator<DatabaseUpgrade> {

	@Override
	public int compare(DatabaseUpgrade upgrade1, DatabaseUpgrade upgrade2) {
		int version1 = upgrade1.getVersion();
		double version2 = upgrade2.getVersion();
		if (version1 < version2) {
			return -1;
		} else if (version1 > version2) {
			return 1;
		}

		return 0;
	}

}
