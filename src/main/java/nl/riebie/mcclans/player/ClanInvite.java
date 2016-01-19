package nl.riebie.mcclans.player;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.messages.Messages;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanInvite {
    private ClanImpl clan;
    private ClanPlayerImpl clanPlayer;

    public ClanInvite(ClanImpl clan, ClanPlayerImpl clanPlayer) {
        this.clan = clan;
        this.clanPlayer = clanPlayer;
    }

    public void accept() {
        Messages.sendClanBroadcastMessagePlayerJoinedTheClan(clan, clanPlayer.getName());
        clanPlayer.setRank(clan.getRank(RankFactory.getRecruitIdentifier()));
        clan.addMember(clanPlayer);
        clanPlayer.setClan(clan);
        clan.removeInvitedPlayer(clanPlayer.getName());
        clanPlayer.resetClanInvite();
    }

    public void decline() {
        clan.removeInvitedPlayer(clanPlayer.getName());
        clanPlayer.resetClanInvite();
        Messages.sendClanBroadcastMessagePlayerDeclinedClanInvite(clan, clanPlayer.getName(), Permission.invite);
    }

    public ClanImpl getClan() {
        return clan;
    }
}
