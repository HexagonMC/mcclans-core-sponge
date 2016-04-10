package nl.riebie.mcclans.permissions;

import nl.riebie.mcclans.api.permissions.ClanPermission;
import nl.riebie.mcclans.api.permissions.ClanPermissionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by riebie on 10/04/2016.
 */
public class ClanPermissionManagerImpl implements ClanPermissionManager {

    private boolean initialized;
    private Map<String, ClanPermission> clanPermissionList = new HashMap<>();

    public void setInitialized() {
        initialized = true;
    }

    @Override
    public boolean registerClanPermission(String name, String description) {
        if (initialized) {
            throw new IllegalStateException("ClanPermissions can only be registered during the initialization of the server");
        }
        name = name.toLowerCase();
        if (clanPermissionList.containsKey(name)) {
            return false;
        }
        clanPermissionList.put(name, new ClanPermissionImpl(name, description));
        return true;
    }

    @Override
    public boolean isActiveClanPermission(String name) {
        return clanPermissionList.containsKey(name.toLowerCase());
    }

    @Override
    public ClanPermission getClanPermission(String name) {
        return clanPermissionList.get(name);
    }
}
