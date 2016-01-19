package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.Clan;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanCreateEvent extends ClanEvent {

    private Clan clan;

    public ClanCreateEvent(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }
}
