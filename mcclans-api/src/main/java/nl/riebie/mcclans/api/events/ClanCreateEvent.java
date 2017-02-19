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

package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.ClanPlayer;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;

/**
 * A clan event which is fired when a clan is created.
 * <p>
 * Created by Kippers on 19-1-2016.
 */
public class ClanCreateEvent extends CancellableClanEvent {

    private final String clanTag;
    private final String clanName;
    private final ClanPlayer owner;

    public ClanCreateEvent(String clanTag, String clanName, ClanPlayer owner) {
        super("Clan creation cancelled by an external plugin", Cause.of(NamedCause.owner(owner)));

        this.clanTag = clanTag;
        this.clanName = clanName;
        this.owner = owner;
    }

    /**
     * Get the clan tag.
     */
    public String getClanTag() {
        return clanTag;
    }

    /**
     * Get the clan name.
     */
    public String getClanName() {
        return clanName;
    }

    /**
     * Get the owner of the clan.
     */
    public ClanPlayer getOwner() {
        return owner;
    }

    /**
     * A user command was used to create then clan.
     */
    public static class User extends ClanCreateEvent {

        public User(String clanTag, String clanName, ClanPlayer owner) {
            super(clanTag, clanName, owner);
        }
    }

    /**
     * An admin commmand was used to create the clan.
     */
    public static class Admin extends ClanCreateEvent {

        public Admin(String clanTag, String clanName, ClanPlayer owner) {
            super(clanTag, clanName, owner);
        }
    }

    /**
     * An external plugin created the clan.
     */
    public static class Plugin extends ClanCreateEvent {

        public Plugin(String clanTag, String clanName, ClanPlayer owner) {
            super(clanTag, clanName, owner);
        }
    }
}
