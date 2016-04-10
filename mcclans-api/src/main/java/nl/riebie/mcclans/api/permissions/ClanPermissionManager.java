package nl.riebie.mcclans.api.permissions;

/**
 * Created by riebie on 10/04/2016.
 */
public interface ClanPermissionManager {

    /**
     * Add a new ClanPermission
     *
     * @param name the name of the new ClanPermission
     * @param description the description of the new ClanPermission
     * @return false if the given name already is used for a different Permission
     */
    boolean registerClanPermission(String name, String description);

    /**
     * Check if there is a registered ClanPermission for the given name
     *
     * @param name the ClanPermission name to check
     * @return true if there is a registered ClanPermission for the given name
     */
    boolean isActiveClanPermission(String name);

    /**
     * Get the ClanPermission for a given name
     *
     * @param name the name to get a ClanPermission for
     * @return the ClanPermission for a given name
     */
    ClanPermission getClanPermission(String name);
}
