package nl.riebie.mcclans.api.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public enum Permission {
    none("Non usable permission"), home("Teleport to the clan home"), sethome("Set the clan home location"), invite("Invite players to the clan"), remove(
            "Remove clan members"), disband("Disband the clan"), friendlyfire("Change the clan's friendly fire setting"), coords(
            "See the location of clan members"), tag("Modify the clan tag"), rank("Create, change or remove ranks"), setrank(
            "Assign ranks to clan members"), ally("Invite or remove allies of the clan"), clanchat("Talk in clan chat"), allychat("Talk in ally chat");

    private String description;

    Permission(String description) {
        this.description = description;
    }

    public static boolean contains(String testedPermission) {
        for (Permission permission : Permission.values()) {
            if (permission.name().equals(testedPermission)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUsablePermission(String testedPermission) {
        for (Permission permission : Permission.values()) {
            if (permission.name().equals(testedPermission)) {
                return true;
            }
        }
        return false;
    }

    public static List<Permission> getUsablePermissions() {
        List<Permission> permissions = new ArrayList<Permission>();
        Permission[] permissionArray = Permission.values();
        for (Permission permission : permissionArray) {
            if (!permission.name().equals(Permission.none.name())) {
                permissions.add(permission);
            }
        }
        return permissions;
    }

    public String getDescription() {
        return description;
    }
}
