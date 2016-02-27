package nl.riebie.mcclans.api;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.api.enums.PermissionModifyResponse;

import java.util.List;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public interface Rank {
    /**
     * Get the name of this rank
     *
     * @return the name of this rank
     */
    String getName();

    /**
     * Get a List of Permissions contained by this rank
     *
     * @return a List containing all the Permissions of this Rank
     */
    List<Permission> getPermissions();

    /**
     * Add a permission to this Rank
     *
     * @param permission the name of a permission
     * @return PermissionModifyResponse enum value
     */
    PermissionModifyResponse addPermission(String permission);

    /**
     * Add a permission to this Rank
     *
     * @param permission a permission
     * @return PermissionModifyResponse enum value
     */
    PermissionModifyResponse addPermission(Permission permission);

    /**
     * Remove a permission from this Rank
     *
     * @param permission the name of a permission
     * @return PermissionModifyResponse enum value
     */
    PermissionModifyResponse removePermission(String permission);

    /**
     * Remove a permission from this Rank
     *
     * @param permission a permission
     * @return PermissionModifyResponse enum value
     */
    PermissionModifyResponse removePermission(Permission permission);


    /**
     * Check if this Rank contains the given permission
     *
     * @param permission the name of a permission
     * @return true if the Rank contains the given Permission
     */
    boolean hasPermission(String permission);

    /**
     * Check if this Rank contains the given permission
     *
     * @param permission a Permission enum value
     * @return true if the Rank contains the given Permission
     */
    boolean hasPermission(Permission permission);
}
