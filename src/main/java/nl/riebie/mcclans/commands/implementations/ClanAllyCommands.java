package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.Toggle;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by Koen on 28/02/2016.
 */
public class ClanAllyCommands {

    @Command(name = "accept", description = "Accept a pending ally invite", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.ally, spongePermission = "mcclans.user.ally.accept")
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

    @Command(name = "decline", description = "Decline a pending ally invite", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.ally, spongePermission = "mcclans.user.ally.decline")
    public void allyDeclineCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanImpl invitedClan = clanPlayer.getClan();
        ClanImpl invitingClan = invitedClan.getInvitingAlly();
        if (invitingClan == null) {
            Messages.sendWarningMessage(commandSource, Messages.NO_PENDING_ALLY_INVITE);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.ALLY_INVITE_DECLINED);
            invitedClan.resetInvitingAlly();
            Messages.sendClanBroadcastMessageClanHasDeclinedToBecomeAllies(invitingClan, invitedClan.getName(), Permission.ally);
            Messages.sendClanBroadcastMessagePlayerHasDeclinedToBecomeAlliesWithClan(invitedClan, clanPlayer.getName(), invitingClan.getName(),
                    Permission.ally);
        }
    }

    @Command(name = "invite", description = "Invite another clan to become an ally", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.ally, spongePermission = "mcclans.user.ally.invite")
    public void allyInviteCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter String clanTag) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl invitedClan = clansImpl.getClan(clanTag);
        ClanImpl invitingClan = clanPlayer.getClan();
        if (invitedClan == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
        } else {
            if (invitedClan.getTag().toLowerCase().equals(invitingClan.getTag().toLowerCase())) {
                Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_BECOME_ALLIES_WITH_YOUR_OWN_CLAN);
            } else if (invitingClan.isClanAllyOfThisClan(invitedClan.getTag())) {
                Messages.sendWarningMessage(commandSource, Messages.YOUR_CLANS_ARE_ALREADY_ALLIES);
            } else if (!invitedClan.isAllowingAllyInvites()) {
                Messages.sendWarningMessage(commandSource, Messages.THIS_CLAN_IS_NOT_ACCEPTING_ALLY_INVITES);
            } else if (invitedClan.getInvitingAlly() != null) {
                Messages.sendThisClanHasAlreadyBeenInvitedToBecomeAlliesWithClan(commandSource, invitedClan.getInvitingAlly().getName());
            } else {
                invitedClan.setInvitingAlly(invitingClan);
                Messages.sendClanBroadcastMessageClanHasBeenInvitedToBecomeAlliesBy(invitingClan, invitedClan.getName(), clanPlayer.getName(),
                        Permission.ally);

                Messages.sendClanBroadcastMessageYourClanHasBeenInvitedToBecomeAlliesWithClan(invitedClan, invitingClan.getName(),
                        invitingClan.getTagColored(), Permission.ally);
            }
        }
    }

    @Command(name = "inviteable", description = "Change if the clan is accepting ally invites", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.ally, spongePermission = "mcclans.user.ally.inviteable")
    public void allyInviteableCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter Toggle toggle) {
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

    @Command(name = "remove", description = "Remove an allied clan", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.ally, spongePermission = "mcclans.user.ally.remove")
    public void allyRemoveCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter String clanTag) {
        ClanImpl clan = clanPlayer.getClan();
        ClanImpl ally = clan.getAlly(clanTag);
        if (ally == null) {
            Messages.sendWarningMessage(commandSource, Messages.THIS_CLAN_IS_NOT_AN_ALLY);
        } else {
            clan.removeAlly(ally.getTag());
            ally.removeAlly(clan.getTag());
            Messages.sendClanBroadcastMessagePlayerHasEndedTheAllianceWithClan(clan, clanPlayer.getName(), ally.getName());
            Messages.sendClanBroadcastMessageClanHasEndedTheAllianceWithYourClan(ally, clan.getName());
        }
    }
}
