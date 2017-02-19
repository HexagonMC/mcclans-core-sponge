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

import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;
import nl.riebie.mcclans.api.permissions.ClanPermissionManager;

import java.util.List;
import java.util.UUID;

/**
 * Starting point for interacting with the MCClans api. Provided as a Sponge service.
 * <p>
 * Created by Kippers on 19-1-2016.
 */
public interface ClanService {
    /**
     * Create a Clan
     *
     * @param tag   the tag of the new clan
     * @param name  the name of the new clan
     * @param owner the owner of the new clan
     * @return the new Clan
     * @throws NotDefaultImplementationException if owner is not created by MCClans
     * @throws IllegalArgumentException          if a provided argument is null, if the owner is already in a clan, or if the clan tag is already taken
     */
    Result<Clan> createClan(String tag, String name, ClanPlayer owner);

    /**
     * Disband a clan
     *
     * @param clan the clan to be disbanded
     * @throws NotDefaultImplementationException if supplied clan is not the default implementation
     */
    void disbandClan(Clan clan);

    /**
     * Get a Clan with the given tag
     *
     * @param tag the tag of a clan
     * @return a Clan with the given tag
     */
    Clan getClan(String tag);

    /**
     * Create a ClanPlayer
     *
     * @param uuid the uuid of a player
     * @param name the name of a player
     * @return a new ClanPlayer
     */
    ClanPlayer createClanPlayer(UUID uuid, String name);

    /**
     * Remove a ClanPlayer
     *
     * @param clanPlayer the clanPlayer to be removed
     * @throws NotDefaultImplementationException if clanPlayer is not created by MCClans
     */
    void removeClanPlayer(ClanPlayer clanPlayer);

    /**
     * Get a ClanPlayer with the given tag
     *
     * @param uuid the uuid of a player
     * @return a ClanPlayer with the given UUID
     */
    ClanPlayer getClanPlayer(UUID uuid);

    /**
     * Get a List containing all the registered Clans
     *
     * @return a List containing all the Clans
     */
    List<Clan> getClans();

    /**
     * Check if the given clanTag is free
     *
     * @param tag a clanTag
     * @return true if the given clanTag is free
     */
    boolean isTagAvailable(String tag);

    /**
     * Get the ClanPermissionManager for registering and retrieving ClanPermissions
     *
     * @return the ClanPermissionManager
     */
    ClanPermissionManager getClanPermissionManager();

}
