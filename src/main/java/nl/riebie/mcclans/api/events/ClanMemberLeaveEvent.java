package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanMemberLeaveEvent extends ClanEvent {

    private Clan clan;
    private ClanPlayer clanMember;

    public ClanMemberLeaveEvent(Clan clan, ClanPlayer clanMember) {
        this.clan = clan;
        this.clanMember = clanMember;
    }

    public Clan getClan() {
        return clan;
    }

    public ClanPlayer getClanMember() {
        return clanMember;
    }
}
