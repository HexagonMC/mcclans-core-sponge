/*
 *  Copyright (c) 2016 riebie, Kippers < https://bitbucket.org/Kippers/mcclans-core-sponge>
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
 *
 */

package nl.riebie.mcclans.api.permissions;

import java.util.List;

/**
 * Keeps track of all registered clan permissions.
 * <p>
 * Created by riebie on 10/04/2016.
 */
public interface ClanPermissionManager {

    /**
     * Register a new ClanPermission. Should only be called during the INITIALIZATION state
     *
     * @param name        the name of the new ClanPermission
     * @param description the description of the new ClanPermission
     * @return false if the given name already is used for a different Permission
     * @throws IllegalStateException when called after the INITIALIZATION state
     */
    boolean registerClanPermission(String name, String description);

    /**
     * Check if there is a registered ClanPermission for the given name. Should not be called before the LOAD_COMPLETE
     * state.
     *
     * @param name the ClanPermission name to check
     * @return true if there is a registered ClanPermission for the given name
     * @throws IllegalStateException when called before the LOAD_COMPLETE state
     */
    boolean isActiveClanPermission(String name);

    /**
     * Get the ClanPermission for a given name. Should not be called before the LOAD_COMPLETE state.
     *
     * @param name the name to get a ClanPermission for
     * @return the ClanPermission for a given name
     * @throws IllegalStateException when called before the LOAD_COMPLETE state
     */
    ClanPermission getClanPermission(String name);

    /**
     * Get all the active ClanPermissions. Should not be called before the LOAD_COMPLETE state.
     *
     * @return a list of all the active ClanPermissions
     * @throws IllegalStateException when called before the LOAD_COMPLETE state
     */
    List<ClanPermission> getClanPermissions();
}
