package nl.riebie.mcclans.permissions;

import nl.riebie.mcclans.api.permissions.ClanPermission;

/**
 * Created by riebie on 10/04/2016.
 */
public class ClanPermissionImpl implements ClanPermission {

    private final String name;
    private final String description;

    public ClanPermissionImpl(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
