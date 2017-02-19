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
import org.spongepowered.api.text.Text;

import java.util.UUID;

/**
 * A player that may or may not be a member of a clan.
 * <p>
 * Created by Kippers on 19-1-2016.
 */
public interface ClanPlayer {

    /**
     * Returns the player UUID
     *
     * @return the UUID of this player
     */
    UUID getUUID();

    /**
     * Returns the player name
     *
     * @return the name of this player
     */
    String getName();

    /**
     * Get the clan of this ClanPlayer
     *
     * @return the clan of this ClanPlayer
     */
    Clan getClan();

    /**
     * Returns if this player is a member of a clan
     *
     * @return true if this player is a member of a clan
     */
    boolean isMemberOfAClan();


    /**
     * Get the kill/death ratio of this clanPlayer
     *
     * @return the  kill/death ratio of this clanPlayer
     */
    KillDeath getKillDeath();

    /**
     * Get the rank of the player
     *
     * @return the Rank object of the player
     */
    Rank getRank();

    /**
     * Set the rank of the player
     *
     * @param rank the Rank object to be set
     * @throws NotDefaultImplementationException when using an implementation not from MCClans
     * @throws IllegalArgumentException          if rank is null, or the new rank is the owner rank
     * @throws IllegalStateException             if the clanplayer is not in a clan, or if the clanplayer has the owner rank
     */
    void setRank(Rank rank);

    /**
     * Get the lastonline time of the clanplayer
     *
     * @return the lastonline time of the clanplayer
     */
    LastOnline getLastOnline();

    /**
     * Checks if the player has friendly fire protection on
     *
     * @return true if the player has friendly fire protection on
     */
    boolean isFfProtected();

    /**
     * Set if the player has friendly fire protection on
     *
     * @param ffProtection set to true to set the friendly fire protection on for this player
     */
    void setFfProtection(boolean ffProtection);

    /**
     * Send a message to the ClanPlayer
     *
     * @param message the message for the ClanPlayer
     */
    void sendMessage(Text... message);
}
