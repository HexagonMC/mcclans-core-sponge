package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.Clan;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanDisbandEvent extends ClanEvent {

    private Clan clan;

    public ClanDisbandEvent(Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }
}
