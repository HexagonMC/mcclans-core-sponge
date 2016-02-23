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
    public void clanTestCommand(ClanPlayerImpl clanPlayer, @Parameter Permission test, @OptionalParameter(Integer.class) Optional<Integer> optionalMessage) {
        String message = test.toString() + " ";
        if (optionalMessage.isPresent()) {
            int value = optionalMessage.get();
            message += value;
        }
        clanPlayer.sendMessage(Text.of(message));
    }

    @Command(name = "create", description = CLAN_CREATE_DESCRIPTION)
    public void clanCreateCommand(
            ClanPlayerImpl clanPlayer,
            @Parameter(length = LengthConstraints.CLAN_TAG, regex = RegexConstraints.CLAN_TAG) String clanTag,
            @Parameter(length = LengthConstraints.CLAN_NAME, regex = RegexConstraints.CLAN_NAME) String clanName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        if (clansImpl.tagIsFree(clanTag)) {
            ClanImpl clanImpl = clansImpl.createClan(clanTag, clanName, clanPlayer);
            Messages.sendBroadcastMessageClanCreatedBy(clanImpl.getName(), clanImpl.getTagColored(), clanPlayer.getName());
        }
    }

    @Command(name = "list")
    public void clanChatCommand(ClanPlayerImpl clanPlayer, @PageParameter int page) {
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
        clanPlayer.sendMessage(Text.of("NIET DIT ROOTCOMMAND AANROEPEN, FLIKKER"));
    }

}
