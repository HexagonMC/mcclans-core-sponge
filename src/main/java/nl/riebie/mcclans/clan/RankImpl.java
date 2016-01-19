package nl.riebie.mcclans.clan;

import nl.riebie.mcclans.api.Rank;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.api.enums.PermissionModifyResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class RankImpl implements Rank {
    private int rankID;
    private String name;
    private List<Permission> permissions = new ArrayList<Permission>();
    private boolean changeable;

    private RankImpl(Builder builder) {
        this.rankID = builder.rankID;
        this.name = builder.name;
        this.changeable = builder.changeable;
    }

    public int getID() {
        return rankID;
    }

    @Override
    public String getName() {
        return name;
    }

    // Don't call this method without changing the Rank hashmap key in ClansImpl
    public void setName(String name) {
        this.name = name;
        // TODO SPONGE:
        // TaskForwarder.sendUpdateRank(this);
    }

    @Override
    public List<Permission> getPermissions() {
        return new ArrayList<Permission>(permissions);
    }

    public String getPermissionsAsString() {
        String permissions = "";
        int i = 0;
        List<Permission> permissionList = getPermissions();
        for (Permission perm : permissionList) {
            if (i != 0) {
                permissions += ",";
            }
            permissions += perm.name();
            i++;
        }
        return permissions;
    }

    @Override
    public PermissionModifyResponse addPermission(String permission) {
        if (Permission.isUsablePermission(permission)) {
            Permission permEnum = Permission.valueOf(permission);
            if (hasPermission(permEnum)) {
                return PermissionModifyResponse.ALREADY_CONTAINS_PERMISSION;
            } else {
                permissions.add(permEnum);
                // TODO SPONGE:
//                TaskForwarder.sendUpdateRank(this);
                return PermissionModifyResponse.SUCCESSFULLY_MODIFIED;
            }
        } else {
            return PermissionModifyResponse.NOT_A_VALID_PERMISSION;
        }
    }

    @Override
    public PermissionModifyResponse removePermission(String permission) {
        if (Permission.isUsablePermission(permission)) {
            Permission permEnum = Permission.valueOf(permission);
            if (hasPermission(permEnum)) {
                permissions.remove(permEnum);
                // TODO SPONGE:
//                TaskForwarder.sendUpdateRank(this);
                return PermissionModifyResponse.SUCCESSFULLY_MODIFIED;
            } else {
                return PermissionModifyResponse.DOES_NOT_CONTAIN_PERMISSION;
            }
        } else {
            return PermissionModifyResponse.NOT_A_VALID_PERMISSION;
        }
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return permissions.contains(permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        if (Permission.isUsablePermission(permission)) {
            Permission permEnum = Permission.valueOf(permission);
            return hasPermission(permEnum);
        }
        return false;
    }

    public int getImportance() {
        return permissions.size();
    }

    public boolean isChangeable() {
        return changeable;
    }

    public static class Builder {
        private int rankID;
        private String name;
        private boolean changeable = true;

        public Builder(int rankID, String name) {
            this.rankID = rankID;
            this.name = name;
        }

        public Builder unchangeable() {
            this.changeable = false;
            return this;
        }

        public RankImpl build() {
            return new RankImpl(this);
        }
    }
}
