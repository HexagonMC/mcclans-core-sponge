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
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;

/**
 * Event fired when a clan is disbanded.
 * <p>
 * Created by Kippers on 19-1-2016.
 */
public class ClanDisbandEvent extends CancellableClanEvent {

    private Clan clan;

    private ClanDisbandEvent(Clan clan) {
        super("Clan disband cancelled by an external plugin", Cause.of(NamedCause.owner(clan.getOwner())));
        this.clan = clan;
    }

    /**
     * Get the disbanded clan.
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * A user command was used to disband the clan.
     */
    public static class User extends ClanDisbandEvent {
        public User(Clan clan) {
            super(clan);
        }
    }

    /**
     * An admin command was used to disband the clan.
     */
    public static class Admin extends ClanDisbandEvent {
        public Admin(Clan clan) {
            super(clan);
        }
    }

    /**
     * An external plugin disbanded the clan.
     */
    public static class Plugin extends ClanDisbandEvent {
        public Plugin(Clan clan) {
            super(clan);
        }
    }
}
