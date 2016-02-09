package nl.riebie.mcclans.events;

import nl.riebie.mcclans.api.events.ClanEvent;
import nl.riebie.mcclans.player.ClanPlayerImpl;

/**
 * Created by K.Volkers on 9-2-2016.
 */
public class ClanPlayerKillEvent extends ClanEvent {

    private ClanPlayerImpl killer;
    private ClanPlayerImpl victim;

    public ClanPlayerKillEvent(ClanPlayerImpl killer, ClanPlayerImpl victim) {
        this.killer = killer;
        this.victim = victim;
    }

    public ClanPlayerImpl getKiller() {
        return killer;
    }

    public ClanPlayerImpl getVictim() {
        return victim;
    }

}
