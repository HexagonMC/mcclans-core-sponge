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

package nl.riebie.mcclans.api.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kippers on 19-1-2016.
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
