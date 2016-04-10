package nl.riebie.mcclans.api.permissions;

/**
 * Created by riebie on 10/04/2016.
 */
public interface ClanPermission {

    /**
     * Get the name for this ClanPermission
     * @return the name for this ClanPermission
     */
    String getName();

    /**
     * Get the description for this ClanPermission
     * @return the description for this ClanPermission
     */
    String getDescription();
}
