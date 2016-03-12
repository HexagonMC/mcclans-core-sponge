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

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kippers on 19-1-2016.
 */
public interface Clan {

    /**
     * Returns all the members in this clan
     *
     * @return all the members in this clan
     */
    public List<ClanPlayer> getMembers();

    /**
     * Returns the number of members in this clan
     *
     * @return the number of members in this clan
     */
    public int getMemberCount();

    /**
     * Returns the clanplayer if the given player is a member of this clan
     *
     * @param uuid the uuid of a player
     * @return the clanplayer if the given player is a member of this clan
     */
    public ClanPlayer getMember(UUID uuid);

    /**
     * Returns true if the given player is a member of this clan
     *
     * @param uuid the uuid of a player
     * @return true if the given player is a member of this clan
     */
    public boolean isPlayerMember(UUID uuid);

    /**
     * Returns true if the given player is friendly to this clan
     *
     * @param clanPlayer the clanPlayer of a player
     * @return true if the given player is friendly to this clan
     * @throws NotDefaultImplementationException if the given ClanPlayer is not created by MCClans
     */
    public boolean isPlayerFriendlyToThisClan(ClanPlayer clanPlayer);

    /**
     * Returns all the ranks available in this clan
     *
     * @return all the ranks available in this clan
     */
    public List<Rank> getRanks();

    /**
     * Sets the home location of this clan
     *
     * @param location position of the new home location
     */
    public void setHome(Location<World> location);

    /**
     * Get the home location of this clan
     *
     * @return position of the home location
     */
    public Location<World> getHome();

    /**
     * Get the total unweighted kills of all the members in this clan
     *
     * @return the unweighted kills of all the members in this clan
     */
    public int getKills();

    /**
     * Get the total high factored kills of all the members in this clan
     *
     * @return the high factored kills of all the members in this clan
     */
    public int getKillsHigh();

    /**
     * Get the total medium factored kills of all the members in this clan
     *
     * @return the medium factored kills of all the members in this clan
     */
    public int getKillsMedium();

    /**
     * Get the total low factored kills of all the members in this clan
     *
     * @return the low factored kills of all the members in this clan
     */
    public int getKillsLow();

    /**
     * Get the total unweighted deaths of all the members in this clan
     *
     * @return the unweighted deaths of all the members in this clan
     */
    public int getDeaths();

    /**
     * Get the total high factored deaths of all the members in this clan
     *
     * @return the high factored deaths of all the members in this clan
     */
    public int getDeathsHigh();

    /**
     * Get the total medium factored deaths of all the members in this clan
     *
     * @return the medium factored deaths of all the members in this clan
     */
    public int getDeathsMedium();

    /**
     * Get the total low factored deaths of all the members in this clan
     *
     * @return the low factored deaths of all the members in this clan
     */
    public int getDeathsLow();

    /**
     * Get the average weighted kill/death ratio of all the members in this clan
     *
     * @return the average weighted kill/death ratio of all the members in this clan
     */
    public double getKDR();

    /**
     * Change the name of the clan
     *
     * @param name the new name of the clan
     */
    public void setName(String name);

    /**
     * Get the name of the clan
     *
     * @return the name of the clan
     */
    public String getName();

    /**
     * Get the tag that identifies this clan
     *
     * @return the tag of this clan
     */
    public String getTag();

    /**
     * Get the colored tag that identifies this clan
     *
     * @return the colored tag of this clan
     */
    public Text getTagColored();

    /**
     * Get a rank object with a given name
     *
     * @param rank name of the rank to be retrieved
     * @return a Rank object
     */
    public Rank getRank(String rank);

    /**
     * Rename a rank
     *
     * @param oldName name of the rank to be renamed
     * @param newName new name for the rank
     */
    public void renameRank(String oldName, String newName);

    /**
     * Check if this clan contains a rank with the given name
     *
     * @param name name of the rank to be checked
     * @return true of the clan contains the given rank
     */
    public boolean containsRank(String name);

    /**
     * Set the owner of this clan
     *
     * @param clanPlayer the ClanPlayer of the new owner of this clan
     * @throws NotDefaultImplementationException if the given ClanPlayer is not created by MCClans
     */
    public void setOwner(ClanPlayer clanPlayer);

    /**
     * Get the owner of this clan
     *
     * @return the ClanPlayer of the owner of this clan
     */
    public ClanPlayer getOwner();

    /**
     * Get the color of the tag that identifies this clan
     *
     * @return the color of the tag of this clan
     */
    public TextColor getTagColor();

    /**
     * Set the color of the clan tag
     *
     * @param textColor the the color to be set
     */
    public boolean setTagColor(TextColor textColor);

    /**
     * Get the clan of the given clanTag if it is an ally of this clan
     *
     * @param clanTag the clanTag of a clan
     * @return the clan of the given clanTag if it is an ally of this clan
     */
    public Clan getAlly(String clanTag);

    /**
     * Check if the given clan is an ally of this clan
     *
     * @param clanTag the clanTag of a clan
     * @return true if the clan of the given clanTag is an ally of this clan
     */
    public boolean isClanAllyOfThisClan(String clanTag);

    /**
     * Check if the given clan is an ally of this clan
     *
     * @param clan a clan
     * @return true if the given clan is an ally of this clan
     */
    public boolean isClanAllyOfThisClan(Clan clan);

    /**
     * Set if the clan is inviteable for ally requests
     *
     * @param inviteable true if the clan is inviteable
     */
    public void setAllowingAllyInvites(boolean inviteable);

    /**
     * Get if the clan is inviteable for ally requests
     *
     * @return true if the clan is inviteable
     */
    public boolean isAllowingAllyInvites();

    /**
     * Send a message to all the members of this clan
     *
     * @param message the message to be sent
     */
    public void sendMessage(Text... message);

    /**
     * Send a message to all the members of this clan with the given permission
     *
     * @param message    the message to be sent
     * @param permission the permission needed by the members to receive this message
     */
    public void sendMessage(Permission permission, Text... message);

    /**
     * Get a list of all the allies of this clan
     *
     * @return a list with the Clan objects of the allies of this clan
     */
    public List<Clan> getAllies();

    /**
     * Get the creation date of this clan
     *
     * @return the creation date of this clan
     */
    public Date getCreationDate();

    /**
     * Get the creation date of this clan in a user friendly format
     *
     * @return the creation date of this clan in a user friendly format
     */
    public String getCreationDateUserFriendly();

    /**
     * Check if the friendly fire protection is on
     *
     * @return true if the friendly fire protection is on
     */
    public boolean isFfProtected();

    /**
     * Set if the friendly fire protection is on
     *
     * @param ffProtection if set to true the friendly fire protection is on
     */
    public void setFfProtection(boolean ffProtection);
}
