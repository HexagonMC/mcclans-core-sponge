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

package nl.riebie.mcclans.events;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.events.*;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;

/**
 * Created by Kippers on 9-2-2016.
 */
public class EventDispatcher {
    private static EventDispatcher _instance;

    public static EventDispatcher getInstance() {
        if (_instance == null) {
            _instance = new EventDispatcher();
        }
        return _instance;
    }

    public void dispatchClanCreateEvent(Clan clan, ClanPlayer owner) {
        dispatchEvent(new ClanCreateEvent(clan, owner));
    }

    public void dispatchClanDisbandEvent(Clan clan) {
        dispatchEvent(new ClanDisbandEvent(clan));
    }

    public void dispatchClanOwnerChangeEvent(Clan clan, ClanPlayer previousOwner, ClanPlayer newOwner) {
        dispatchEvent(new ClanOwnerChangeEvent(clan, previousOwner, newOwner));
    }

    public void dispatchClanMemberJoinEvent(Clan clan, ClanPlayer clanMember) {
        dispatchEvent(new ClanMemberJoinEvent(clan, clanMember));
    }

    public void dispatchClanMemberLeaveEvent(Clan clan, ClanPlayer clanMember) {
        dispatchEvent(new ClanMemberLeaveEvent(clan, clanMember));
    }

    public void dispatchClanPlayerKillEvent(ClanPlayerImpl killer, ClanPlayerImpl victim) {
        dispatchEvent(new ClanPlayerKillEvent(killer, victim));
    }

    private void dispatchEvent(Event event) {
        Sponge.getEventManager().post(event);
    }
}
