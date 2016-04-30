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

package nl.riebie.mcclans.clan;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.Rank;
import nl.riebie.mcclans.api.enums.PermissionModifyResponse;
import nl.riebie.mcclans.api.permissions.ClanPermissionManager;
import nl.riebie.mcclans.persistence.TaskForwarder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kippers on 19-1-2016.
 */
public class RankImpl implements Rank {
    private int rankID;
    private String name;
    private List<String> permissions = new ArrayList<>();
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
        TaskForwarder.sendUpdateRank(this);
    }

    @Override
    public List<String> getPermissions() {
        return permissions;
    }

    public String getPermissionsAsString() {
        String permissions = "";
        int i = 0;
        List<String> permissionList = getPermissions();
        for (String perm : permissionList) {
            if (i != 0) {
                permissions += ",";
            }
            permissions += perm;
            i++;
        }
        return permissions;
    }

    @Override
    public PermissionModifyResponse addPermission(String permission) {
        ClanPermissionManager clanPermissionManager = ClansImpl.getInstance().getClanPermissionManager();
        if (clanPermissionManager.isActiveClanPermission(permission)) {
            if (hasPermission(permission)) {
                return PermissionModifyResponse.ALREADY_CONTAINS_PERMISSION;
            } else {
                permissions.add(permission.toLowerCase());
                TaskForwarder.sendUpdateRank(this);
                return PermissionModifyResponse.SUCCESSFULLY_MODIFIED;
            }
        } else {
            return PermissionModifyResponse.NOT_A_VALID_PERMISSION;
        }
    }

    public void setPermissions(List<String> permissions){
        this.permissions = permissions;
    }

    @Override
    public PermissionModifyResponse removePermission(String permission) {
        if (hasPermission(permission)) {
            permissions.remove(permission);
            TaskForwarder.sendUpdateRank(this);
            return PermissionModifyResponse.SUCCESSFULLY_MODIFIED;
        } else {
            return PermissionModifyResponse.DOES_NOT_CONTAIN_PERMISSION;
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        ClanPermissionManager clanPermissionManager = ClansImpl.getInstance().getClanPermissionManager();
        if (clanPermissionManager.isActiveClanPermission(permission)) {
            return rankID == RankFactory.getOwnerID() || permissions.contains(permission.toLowerCase());
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
