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

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;

/**
 * Event fired when an owner of a clan is changed.
 * <p>
 * Created by Kippers on 19-1-2016.
 */
public class ClanOwnerChangeEvent extends CancellableClanEvent {

    private Clan clan;
    private ClanPlayer previousOwner;
    private ClanPlayer newOwner;

    private ClanOwnerChangeEvent(Clan clan, ClanPlayer previousOwner, ClanPlayer newOwner) {
        super("Clan owner change cancelled by an external plugin", Cause.of(NamedCause.owner(newOwner)));
        this.clan = clan;
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }

    /**
     * Get the clan whose owner changed.
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * Get the player who will no longer be the owner of the clan.
     */
    public ClanPlayer getPreviousOwner() {
        return previousOwner;
    }

    /**
     * Get the player who will be the new owner of the clan.
     */
    public ClanPlayer getNewOwner() {
        return newOwner;
    }


    /**
     * A user command was used to change the owner of the clan.
     */
    public static class User extends ClanOwnerChangeEvent {
        public User(Clan clan, ClanPlayer previousOwner, ClanPlayer newOwner) {
            super(clan, previousOwner, newOwner);
        }
    }

    /**
     * An admin command was used to change the owner of the clan.
     */
    public static class Admin extends ClanOwnerChangeEvent {
        public Admin(Clan clan, ClanPlayer previousOwner, ClanPlayer newOwner) {
            super(clan, previousOwner, newOwner);
        }
    }

    /**
     * An external plugin changed the owner of the clan.
     */
    public static class Plugin extends ClanOwnerChangeEvent {
        public Plugin(Clan clan, ClanPlayer previousOwner, ClanPlayer newOwner) {
            super(clan, previousOwner, newOwner);
        }
    }
}
