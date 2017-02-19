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

package nl.riebie.mcclans.api;

import nl.riebie.mcclans.api.enums.PermissionModifyResponse;

import java.util.List;

/**
 * Rank of a player in a clan.
 * <p>
 * Created by Kippers on 19-1-2016.
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
    List<String> getPermissions();

    /**
     * Add a permission to this Rank
     *
     * @param permission the name of a permission
     * @return PermissionModifyResponse enum value
     */
    PermissionModifyResponse addPermission(String permission);

    /**
     * Remove a permission from this Rank
     *
     * @param permission the name of a permission
     * @return PermissionModifyResponse enum value
     */
    PermissionModifyResponse removePermission(String permission);

    /**
     * Check if this Rank contains the given permission
     *
     * @param permission the name of a permission
     * @return true if the Rank contains the given Permission
     */
    boolean hasPermission(String permission);

}
