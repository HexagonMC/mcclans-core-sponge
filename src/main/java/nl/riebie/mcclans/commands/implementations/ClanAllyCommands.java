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

package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.Toggle;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by Kippers on 28/02/2016.
 */
public class ClanAllyCommands {

    @Command(name = "accept", description = "Accept a pending ally invite", isPlayerOnly = true, isClanOnly = true, clanPermission = "ally", spongePermission = "mcclans.user.ally.accept")
    public void allyAcceptCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanImpl invitedClan = clanPlayer.getClan();
        ClanImpl invitingClan = invitedClan.getInvitingAlly();
        if (invitingClan == null) {
            Messages.sendWarningMessage(commandSource, Messages.NO_PENDING_ALLY_INVITE);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.ALLY_INVITE_ACCEPTED);
            invitedClan.resetInvitingAlly();
            invitedClan.addAlly(invitingClan);
            invitingClan.addAlly(invitedClan);
            Messages.sendClanBroadcastMessageYourClanHasBecomeAlliesWithClan(invitedClan, invitingClan.getName());
            Messages.sendClanBroadcastMessageYourClanHasBecomeAlliesWithClan(invitingClan, invitedClan.getName());
        }
    }

    @Command(name = "decline", description = "Decline a pending ally invite", isPlayerOnly = true, isClanOnly = true, clanPermission =  "ally", spongePermission = "mcclans.user.ally.decline")
    public void allyDeclineCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanImpl invitedClan = clanPlayer.getClan();
        ClanImpl invitingClan = invitedClan.getInvitingAlly();
        if (invitingClan == null) {
            Messages.sendWarningMessage(commandSource, Messages.NO_PENDING_ALLY_INVITE);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.ALLY_INVITE_DECLINED);
            invitedClan.resetInvitingAlly();
            Messages.sendClanBroadcastMessageClanHasDeclinedToBecomeAllies(invitingClan, invitedClan.getName(),  "ally");
            Messages.sendClanBroadcastMessagePlayerHasDeclinedToBecomeAlliesWithClan(invitedClan, clanPlayer.getName(), invitingClan.getName(),
                    "ally");
        }
    }

    @Command(name = "invite", description = "Invite another clan to become an ally", isPlayerOnly = true, isClanOnly = true, clanPermission =  "ally", spongePermission = "mcclans.user.ally.invite")
    public void allyInviteCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "clanTag") ClanImpl invitedClan) {
        ClanImpl invitingClan = clanPlayer.getClan();
        if (invitingClan.equals(invitedClan)) {
            Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_BECOME_ALLIES_WITH_YOUR_OWN_CLAN);
        } else if (invitingClan.equals(invitedClan)) {
            Messages.sendWarningMessage(commandSource, Messages.YOUR_CLANS_ARE_ALREADY_ALLIES);
        } else if (!invitedClan.isAllowingAllyInvites()) {
            Messages.sendWarningMessage(commandSource, Messages.THIS_CLAN_IS_NOT_ACCEPTING_ALLY_INVITES);
        } else if (invitedClan.getInvitingAlly() != null) {
            Messages.sendThisClanHasAlreadyBeenInvitedToBecomeAlliesWithClan(commandSource, invitedClan.getInvitingAlly().getName());
        } else {
            invitedClan.setInvitingAlly(invitingClan);
            Messages.sendClanBroadcastMessageClanHasBeenInvitedToBecomeAlliesBy(invitingClan, invitedClan.getName(), clanPlayer.getName(),
                    "ally");

            Messages.sendClanBroadcastMessageYourClanHasBeenInvitedToBecomeAlliesWithClan(invitedClan, invitingClan.getName(),
                    invitingClan.getTagColored(),  "ally");
        }
    }

    @Command(name = "inviteable", description = "Change if the clan is accepting ally invites", isPlayerOnly = true, isClanOnly = true, clanPermission = "ally", spongePermission = "mcclans.user.ally.inviteable")
    public void allyInviteableCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "toggle") Toggle toggle) {
        ClanImpl clan = clanPlayer.getClan();

        boolean allowAllyInvites = toggle.getBoolean(clan.isAllowingAllyInvites());
        if (allowAllyInvites) {
            clan.setAllowingAllyInvites(true);
            Messages.sendBasicMessage(commandSource, Messages.YOUR_CLAN_NOW_ACCEPTS_ALLY_INVITES);
        } else {
            clan.setAllowingAllyInvites(false);
            Messages.sendBasicMessage(commandSource, Messages.YOUR_CLAN_NO_LONGER_ACCEPTS_ALLY_INVITES);
        }
    }

    @Command(name = "remove", description = "Remove an allied clan", isPlayerOnly = true, isClanOnly = true, clanPermission = "ally", spongePermission = "mcclans.user.ally.remove")
    public void allyRemoveCommand(ClanPlayerImpl clanPlayer, @Parameter(name = "clanTag") ClanImpl ally) {
        ClanImpl clan = clanPlayer.getClan();
        if(clan.isClanAllyOfThisClan(ally)){
            clan.removeAlly(ally);
            ally.removeAlly(clan);
            Messages.sendClanBroadcastMessagePlayerHasEndedTheAllianceWithClan(clan, clanPlayer.getName(), ally.getName());
            Messages.sendClanBroadcastMessageClanHasEndedTheAllianceWithYourClan(ally, clan.getName());
        } else{
            clanPlayer.sendMessage(Messages.getWarningMessage(Messages.THIS_CLAN_IS_NOT_AN_ALLY));
        }
    }
}
