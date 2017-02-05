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
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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

    public ClanCreateEvent.Plugin dispatchPluginClanCreateEvent(String clanTag, String clanName, ClanPlayer owner) {
        ClanCreateEvent.Plugin event = new ClanCreateEvent.Plugin(clanTag, clanName, owner);
        dispatchEvent(event);
        return event;
    }

    public ClanCreateEvent.User dispatchUserClanCreateEvent(String clanTag, String clanName, ClanPlayer owner) {
        ClanCreateEvent.User event = new ClanCreateEvent.User(clanTag, clanName, owner);
        dispatchEvent(event);
        return event;
    }

    public ClanCreateEvent.Admin dispatchAdminClanCreateEvent(String clanTag, String clanName, ClanPlayer owner) {
        ClanCreateEvent.Admin event = new ClanCreateEvent.Admin(clanTag, clanName, owner);
        dispatchEvent(event);
        return event;
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

    public ClanSetHomeEvent.Admin dispatchClanSetHomeAdmin(Location<World> location, CommandSource commandSource) {
        ClanSetHomeEvent.Admin event = new ClanSetHomeEvent.Admin(commandSource, location);
        dispatchEvent(event);
        return event;
    }

    public ClanSetHomeEvent.User dispatchClanSetHomeUser(ClanPlayerImpl player, Location<World> location) {
        ClanSetHomeEvent.User event = new ClanSetHomeEvent.User(player, location);
        dispatchEvent(event);
        return event;
    }

    public ClanHomeTeleportEvent.User dispatchUserClanHomeTeleportEvent(ClanPlayerImpl player, ClanImpl clan) {
        ClanHomeTeleportEvent.User event = new ClanHomeTeleportEvent.User(clan, player);
        dispatchEvent(event);
        return event;
    }

    public ClanHomeTeleportEvent.Admin dispatchAdminClanHomeTeleportEvent(ClanPlayerImpl player, ClanImpl clan) {
        ClanHomeTeleportEvent.Admin event = new ClanHomeTeleportEvent.Admin(clan, player);
        dispatchEvent(event);
        return event;
    }

    private void dispatchEvent(Event event) {
        Sponge.getEventManager().post(event);
    }
}
