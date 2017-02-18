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

package nl.riebie.mcclans.player;

import nl.riebie.mcclans.api.events.ClanMemberJoinEvent;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.events.EventDispatcher;
import nl.riebie.mcclans.messages.Messages;

/**
 * Created by Kippers on 19-1-2016.
 */
public class ClanInvite {
    private ClanImpl clan;
    private ClanPlayerImpl clanPlayer;

    public ClanInvite(ClanImpl clan, ClanPlayerImpl clanPlayer) {
        this.clan = clan;
        this.clanPlayer = clanPlayer;
    }

    public void accept() {
        ClanMemberJoinEvent event = EventDispatcher.getInstance().dispatchClanMemberJoinEvent(clan, clanPlayer);
        if (event.isCancelled()) {
            clanPlayer.sendMessage(Messages.getWarningMessage(event.getCancelMessage()));
        } else {
            Messages.sendClanBroadcastMessagePlayerJoinedTheClan(clan, clanPlayer.getName());
            clanPlayer.setRankInternal(clan.getRank(RankFactory.getRecruitIdentifier()));
            clan.addMember(clanPlayer);
            clanPlayer.setClan(clan);
            clan.removeInvitedPlayer(clanPlayer.getName());
            clanPlayer.resetClanInvite();
        }
    }

    public void decline() {
        clan.removeInvitedPlayer(clanPlayer.getName());
        clanPlayer.resetClanInvite();
        Messages.sendClanBroadcastMessagePlayerDeclinedClanInvite(clan, clanPlayer.getName(), "invite");
    }

    public ClanImpl getClan() {
        return clan;
    }
}
