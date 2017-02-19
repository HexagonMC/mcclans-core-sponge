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

import nl.riebie.mcclans.api.enums.KillDeathFactor;
import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * A group of players.
 * <p>
 * Created by Kippers on 19-1-2016.
 */
public interface Clan {

    /**
     * Returns all the members in this clan
     *
     * @return all the members in this clan
     */
    List<ClanPlayer> getMembers();

    /**
     * Returns the number of members in this clan
     *
     * @return the number of members in this clan
     */
    int getMemberCount();

    /**
     * Returns the clanplayer if the given player is a member of this clan
     *
     * @param uuid the uuid of a player
     * @return the clanplayer if the given player is a member of this clan
     */
    ClanPlayer getMember(UUID uuid);

    /**
     * Returns true if the given player is a member of this clan
     *
     * @param uuid the uuid of a player
     * @return true if the given player is a member of this clan
     */
    boolean isPlayerMember(UUID uuid);

    /**
     * Returns true if the given player is friendly to this clan
     *
     * @param clanPlayer the clanPlayer of a player
     * @return true if the given player is friendly to this clan
     * @throws NotDefaultImplementationException if the given ClanPlayer is not created by MCClans
     */
    boolean isPlayerFriendlyToThisClan(ClanPlayer clanPlayer);

    /**
     * Returns all the ranks available in this clan
     *
     * @return all the ranks available in this clan
     */
    List<Rank> getRanks();

    /**
     * Sets the home location of this clan
     *
     * @param location position of the new home location, or null
     */
    Result<Location<World>> setHome(@Nullable Location<World> location);

    /**
     * Get the home location of this clan
     *
     * @return position of the home location, or null
     */
    @Nullable
    Location<World> getHome();

    /**
     * Get the total unweighted kills of all the members in this clan
     *
     * @return the unweighted kills of all the members in this clan
     */
    int getTotalKills();

    /**
     * Get the total factored kills of all the members in this clan
     *
     * @param killDeathFactor the factor
     * @return the factored kills of all the members in this clan
     */
    int getKills(KillDeathFactor killDeathFactor);

    /**
     * Get the total unweighted deaths of all the members in this clan
     *
     * @return the unweighted deaths of all the members in this clan
     */
    int getTotalDeaths();

    /**
     * Get the total factored deaths of all the members in this clan
     *
     * @param killDeathFactor the factor
     * @return the factored deaths of all the members in this clan
     */
    int getDeaths(KillDeathFactor killDeathFactor);

    /**
     * Get the average weighted kill/death ratio of all the members in this clan
     *
     * @return the average weighted kill/death ratio of all the members in this clan
     */
    double getKDR();

    /**
     * Change the name of the clan
     *
     * @param name the new name of the clan
     */
    void setName(String name);

    /**
     * Get the name of the clan
     *
     * @return the name of the clan
     */
    String getName();

    /**
     * Get the tag that identifies this clan
     *
     * @return the tag of this clan
     */
    String getTag();

    /**
     * Get the colored tag that identifies this clan
     *
     * @return the colored tag of this clan
     */
    Text getTagColored();

    /**
     * Get a rank object with a given name
     *
     * @param rank name of the rank to be retrieved
     * @return a Rank object
     */
    Rank getRank(String rank);

    /**
     * Check if this clan contains a rank with the given name
     *
     * @param name name of the rank to be checked
     * @return true of the clan contains the given rank
     */
    boolean containsRank(String name);

    /**
     * Set the owner of this clan
     *
     * @param clanPlayer the ClanPlayer of the new owner of this clan
     * @return Result of the action
     * @throws NotDefaultImplementationException if the given ClanPlayer is not created by MCClans
     * @throws IllegalArgumentException          if the provided ClanPlayer is null, or is not a member of this clan
     */
    Result<ClanPlayer> setOwner(ClanPlayer clanPlayer);

    /**
     * Get the owner of this clan
     *
     * @return the ClanPlayer of the owner of this clan
     */
    ClanPlayer getOwner();

    /**
     * Get the color of the tag that identifies this clan
     *
     * @return the color of the tag of this clan
     */
    TextColor getTagColor();

    /**
     * Set the color of the clan tag
     *
     * @param textColor the the color to be set
     */
    void setTagColor(TextColor textColor);

    /**
     * Remove a clan from the allies list of this clan
     *
     * @param clan the clan to be removed from the allies list
     */
    void removeAlly(Clan clan);

    /**
     * Check if the given clan is an ally of this clan
     *
     * @param clan a clan
     * @return true if the given clan is an ally of this clan
     */
    boolean isClanAllyOfThisClan(Clan clan);

    /**
     * Set if the clan is inviteable for ally requests
     *
     * @param inviteable true if the clan is inviteable
     */
    void setAllowingAllyInvites(boolean inviteable);

    /**
     * Get if the clan is inviteable for ally requests
     *
     * @return true if the clan is inviteable
     */
    boolean isAllowingAllyInvites();

    /**
     * Send a message to all the members of this clan
     *
     * @param message the message to be sent
     */
    void sendMessage(Text... message);

    /**
     * Send a message to all the members of this clan with the given clan permission
     *
     * @param message    the message to be sent
     * @param permission the clan permission needed by the members to receive this message
     */
    void sendMessage(String permission, Text... message);

    /**
     * Get a list of all the allies of this clan
     *
     * @return a list with the Clan objects of the allies of this clan
     */
    List<Clan> getAllies();

    /**
     * Get the creation date of this clan
     *
     * @return the creation date of this clan
     */
    Date getCreationDate();

    /**
     * Get the creation date of this clan in a user friendly format
     *
     * @return the creation date of this clan in a user friendly format
     */
    String getCreationDateUserFriendly();

    /**
     * Check if the friendly fire protection is on
     *
     * @return true if the friendly fire protection is on
     */
    boolean isFfProtected();

    /**
     * Set if the friendly fire protection is on
     *
     * @param ffProtection if set to true the friendly fire protection is on
     */
    void setFfProtection(boolean ffProtection);

    /**
     * Get the identifier for the economy account of the clan bank
     *
     * @return the identifier for the economy account of the clan bank
     */
    String getBankId();
}
