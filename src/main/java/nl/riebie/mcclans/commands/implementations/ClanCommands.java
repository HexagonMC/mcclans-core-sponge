package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.constraints.length.LengthConstraints;
import nl.riebie.mcclans.commands.constraints.regex.RegexConstraints;
import nl.riebie.mcclans.comparators.ClanKdrComparator;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.utils.UUIDUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ClanCommands {
    private final static String CLAN_CREATE_DESCRIPTION = "Create a clan";

    @Command(name = "test")
    public void clanTestCommand(ClanPlayerImpl clanPlayer, @Multiline(listType = Permission.class) @Parameter List<Permission> test) {
        String message = "";
        for (Permission permission : test) {
            message += permission.toString();
        }
        clanPlayer.sendMessage(Text.of(message));
    }

    @Command(name = "hoi")
    public void clanHoiCommand(ClanPlayerImpl clanPlayer, @Multiline @Parameter String test) {
        clanPlayer.sendMessage(Text.of(test));
    }

    @Command(name = "optlist")
    public void clanOptListCommand(ClanPlayerImpl clanPlayer, @Multiline(listType = Permission.class) @OptionalParameter(List.class) Optional<List<Permission>> test) {
        if (test.isPresent()) {
            String message = "";
            for (Permission permission : test.get()) {
                message += permission.toString();
            }
            clanPlayer.sendMessage(Text.of(message));
        } else {
            clanPlayer.sendMessage(Text.of("leeg"));
        }
    }

    @Command(name = "opt")
    public void clanOptCommand(ClanPlayerImpl clanPlayer, @Multiline @OptionalParameter(String.class) Optional<String> test) {
        if (test.isPresent()) {
            clanPlayer.sendMessage(Text.of(test.get()));
        } else {
            clanPlayer.sendMessage(Text.of("leeg"));
        }
    }

    @Command(name = "create", description = CLAN_CREATE_DESCRIPTION)
    public void clanCreateCommand(
            ClanPlayerImpl clanPlayer,
            @Parameter(length = LengthConstraints.CLAN_TAG, regex = RegexConstraints.CLAN_TAG) String clanTag,
            @Multiline @Parameter(length = LengthConstraints.CLAN_NAME, regex = RegexConstraints.CLAN_NAME) String clanName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        if (clansImpl.tagIsFree(clanTag)) {
            ClanImpl clanImpl = clansImpl.createClan(clanTag, clanName, clanPlayer);
            Messages.sendBroadcastMessageClanCreatedBy(clanImpl.getName(), clanImpl.getTagColored(), clanPlayer.getName());
        }
    }

    @Command(name = "list")
    public void clanListCommand(ClanPlayerImpl clanPlayer, @PageParameter int page) {
        List<ClanImpl> clans = ClansImpl.getInstance().getClanImpls();

        HorizontalTable<ClanImpl> table = new HorizontalTable<>("Clans", 10, (row, clan, i) -> {
            row.setValue("Rank", Text.of(i + 1));
            row.setValue(
                    "Clan",
                    Text.join(
                            clan.getTagColored(),
                            Text.of(" ", clan.getName())
                    )
            );
            row.setValue("KDR", Text.of(clan.getKDR()));
            row.setValue("Members", Text.of(clan.getMemberCount()));

        });
        table.defineColumn("Rank", 10);
        table.defineColumn("Clan", 40, true);
        table.defineColumn("KDR", 15);
        table.defineColumn("Members", 15);

        table.setComparator(new ClanKdrComparator());

        table.draw(clans, page, clanPlayer);
    }

    @Command(name = "invite")
    public void clanInviteCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter String playerName) {
        ClanImpl clan = clanPlayer.getClan();
        Player player = (Player) commandSource;       //TODO add check if it is a player
        if (clan != null) {
            UUID uuid = UUIDUtils.getUUID(playerName);
            if (uuid == null) {
                Messages.sendPlayerNotOnline(player, playerName);
                return;
            }

            ClansImpl clansInstance = ClansImpl.getInstance();
            ClanPlayerImpl invitedClanPlayer = clansInstance.getClanPlayer(uuid);
            Player invitedPlayer = Sponge.getServer().getPlayer(uuid).get();  //handle optional :)
            if (invitedClanPlayer == null) {
                if (invitedPlayer == null) {
                    Messages.sendPlayerNotOnline(invitedPlayer, playerName);
                    return;
                }
                invitedClanPlayer = clansInstance.createClanPlayer(invitedPlayer.getUniqueId(), invitedPlayer.getName());
            }
            String invitedClanPlayerName = invitedClanPlayer.getName();

            if (invitedClanPlayer.getClan() != null) {
                Messages.sendPlayerAlreadyInClan(player, invitedClanPlayerName);
            } else if (invitedClanPlayer.getClanInvite() != null) {
                Messages.sendPlayerAlreadyInvitedByAnotherClan(player, invitedClanPlayerName);
            } else {
                invitedClanPlayer.inviteToClan(clan);
                clan.addInvitedPlayer(invitedClanPlayer);
                Messages.sendClanBroadcastMessagePlayerInvitedToTheClan(clan, invitedClanPlayerName, player.getName(), Permission.invite);
                if (invitedPlayer != null && invitedPlayer.isOnline()) {
                    Messages.sendInvitedToClan(invitedPlayer, clan.getName(), clan.getTagColored());
                }
            }
            invitedClanPlayer.getClanInvite().accept();
        } else {
            Messages.sendWarningMessage(player, Messages.YOU_ARE_NOT_IN_A_CLAN);
        }
    }

    @ChildGroup(ClanChatCommands.class)
    @Command(name = "chat")
    public void clanChatRootCommand(ClanPlayerImpl clanPlayer) {
        clanPlayer.sendMessage(Text.of("TODO"));
    }

    @Command(name = "disband")
    public void clanDisbandCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clanPlayer.getClan();
        Messages.sendBroadcastMessageClanDisbandedBy(clan.getName(), clan.getTagColored(), commandSource.getName());
        clansImpl.disbandClan(clan.getTag());
    }

    @Command(name = "remove")
    public void clanRemoveCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter String playerName) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan != null) {
            ClanPlayerImpl toBeRemovedClanPlayer = clan.getMember(playerName);
            if (toBeRemovedClanPlayer != null) {
                if (playerName.equalsIgnoreCase(clanPlayer.getName())) {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_REMOVE_YOURSELF_FROM_THE_CLAN);
                } else if (playerName.equalsIgnoreCase(clan.getOwner().getName())) {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_REMOVE_THE_OWNER_FROM_THE_CLAN);
                } else {
                    clan.removeMember(toBeRemovedClanPlayer.getName());
                    Messages.sendClanBroadcastMessagePlayerRemovedFromTheClanBy(clan, toBeRemovedClanPlayer.getName(), clanPlayer.getName());
                    Messages.sendYouHaveBeenRemovedFromClan(toBeRemovedClanPlayer, clan.getName());
                }
            } else {
                Messages.sendPlayerNotAMemberOfThisClan(commandSource, playerName);
            }
        } else {
            Messages.sendWarningMessage(commandSource, Messages.YOU_ARE_NOT_IN_A_CLAN);
        }
    }
}
