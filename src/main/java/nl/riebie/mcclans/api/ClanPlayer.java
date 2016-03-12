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

import java.util.UUID;

/**
 * Created by Kippers on 19-1-2016.
 */
public interface ClanPlayer {

    /**
     * Returns the player UUID
     *
     * @return the UUID of this player
     */
    public UUID getUUID();

    /**
     * Returns the player name
     *
     * @return the name of this player
     */
    public String getName();

    /**
     * Gets the clan which this player is a member of
     *
     * @return the Clan object which this player is a member of
     */
    public Clan getClan();

    /**
     * Returns true if this player is a member of the given clan
     *
     * @param clanTag
     *            the tag of a clan
     * @return true if this player is a member of the given clan
     */
    public boolean isMemberOf(String clanTag);

    /**
     * Returns if this player is a member of a clan
     *
     * @return true if this player is a member of a clan
     */
    public boolean isMemberOfAClan();

    /**
     * Get the weighted kills of this player
     *
     * @return the weighted kills of this player
     */
    public double getKillsWeighted();

    /**
     * Get the total unweighted kills of this clanPlayer
     *
     * @return the unweighted kills of this clanPlayer
     */
    public int getKills();

    /**
     * Get the high factored kills of this clanPlayer
     *
     * @return the high factored kills of this clanPlayer
     */
    public int getKillsHigh();

    /**
     * Get the medium factored kills of this clanPlayer
     *
     * @return the medium factored kills of this clanPlayer
     */
    public int getKillsMedium();

    /**
     * Get the low factored kills of this clanPlayer
     *
     * @return the low factored kills of this clanPlayer
     */
    public int getKillsLow();

    /**
     * Get the weighted deaths of this player
     *
     * @return the weighted deaths of this player
     */
    public double getDeathsWeighted();

    /**
     * Get the unweighted deaths of this player
     *
     * @return the unweighted deaths of this player
     */
    public int getDeaths();

    /**
     * Get the high factored deaths of this clanPlayer
     *
     * @return the high factored deaths of this clanPlayer
     */
    public int getDeathsHigh();

    /**
     * Get the medium factored deaths of this clanPlayer
     *
     * @return the medium factored deaths of this clanPlayer
     */
    public int getDeathsMedium();

    /**
     * Get the low factored deaths of this clanPlayer
     *
     * @return the low factored deaths of this clanPlayer
     */
    public int getDeathsLow();

    /**
     * Get the average weighted kill/death ratio of this clanPlayer
     *
     * @return the average weighted kill/death ratio of this clanPlayer
     */
    public double getKDR();

    /**
     * Set the high factored kills of this clanPlayer
     *
     * @param kills
     *            the high factored kills of this clanPlayer
     */
    public void setKillsHigh(int kills);

    /**
     * Set the medium factored kills of this clanPlayer
     *
     * @param kills
     *            the medium factored kills of this clanPlayer
     */
    public void setKillsMedium(int kills);

    /**
     * Set the low factored kills of this clanPlayer
     *
     * @param kills
     *            the low factored kills of this clanPlayer
     */
    public void setKillsLow(int kills);

    /**
     * Set the high factored deaths of this clanPlayer
     *
     * @param kills
     *            the high factored deaths of this clanPlayer
     */
    public void setDeathsHigh(int kills);

    /**
     * Set the medium factored deaths of this clanPlayer
     *
     * @param kills
     *            the medium factored deaths of this clanPlayer
     */
    public void setDeathsMedium(int kills);

    /**
     * Set the low factored deaths of this clanPlayer
     *
     * @param kills
     *            the low factored deaths of this clanPlayer
     */
    public void setDeathsLow(int kills);

    /**
     * Get the rank of the player
     *
     * @return the Rank object of the player
     */
    public Rank getRank();

    /**
     * Set the rank of the player
     *
     * @param rank
     *            the Rank object to be set
     * @throws NotDefaultImplementationException
     *             when using a wrong implementation of Rank
     */
    public void setRank(Rank rank);

    /**
     * Get the lastonline time of the clanplayer
     *
     * @return the lastonline time of the clanplayer
     */
    public LastOnline getLastOnline();

    /**
     * Checks if the player has friendly fire protection on
     *
     * @return true if the player has friendly fire protection on
     */
    public boolean isFfProtected();

    /**
     * Set if the player has friendly fire protection on
     *
     * @return ffProtection set to true to set the friendly fire protection on for this player
     */
    public void setFfProtection(boolean ffProtection);

}
