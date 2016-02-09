package nl.riebie.mcclans.events;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.events.*;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Event;

/**
 * Created by K.Volkers on 9-2-2016.
 */
public class EventDispatcher {
    private static EventDispatcher _instance;

    public static EventDispatcher getInstance() {
        if (_instance == null) {
            _instance = new EventDispatcher();
        }
        return _instance;
    }

    public void dispatchClanCreateEvent(Clan clan) {
        dispatchEvent(new ClanCreateEvent(clan));
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
