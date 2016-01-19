package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanOwnerChangeEvent extends ClanEvent {

    private Clan clan;
    private ClanPlayer previousOwner;
    private ClanPlayer newOwner;

    public ClanOwnerChangeEvent(Clan clan, ClanPlayer previousOwner, ClanPlayer newOwner) {
        this.clan = clan;
        this.previousOwner = previousOwner;
        this.newOwner = newOwner;
    }

    public Clan getClan() {
        return clan;
    }

    public ClanPlayer getPreviousOwner() {
        return previousOwner;
    }

    public ClanPlayer getNewOwner() {
        return newOwner;
    }
}
