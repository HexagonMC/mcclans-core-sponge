package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.OptionalParameter;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.player.KillDeathFactorHandler;
import nl.riebie.mcclans.table.VerticalTable;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

/**
 * Created by Koen on 26/02/2016.
 */
public class ClanPlayerCommands {

    @Command(name = "setrank", isPlayerOnly = true, permission = Permission.setrank, description = "Set the rank of a member of your clan")
    public void playerSetRankCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter String playerName,
                                     @Parameter String rankName) {

        ClanImpl clan = clanPlayer.getClan();
        if (!clan.isPlayerMember(playerName)) {
            Messages.sendPlayerNotAMemberOfThisClan(sender, playerName);
            return;
        }

        RankImpl rank = clanPlayer.getClan().getRank(rankName);
        ClanPlayerImpl targetClanPlayer = clan.getMember(playerName);

        if (rank == null) {
            Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
        } else if (targetClanPlayer.getRank().getName().toLowerCase().equals(RankFactory.getOwnerIdentifier().toLowerCase())) {
            Messages.sendWarningMessage(sender, Messages.YOU_CANNOT_OVERWRITE_THE_OWNER_RANK);
        } else {
            targetClanPlayer.setRank(rank);
            if (RankFactory.getOwnerIdentifier().toLowerCase().equals(rankName.toLowerCase())) {
                clan.setOwner(targetClanPlayer);
                clanPlayer.setRank(clan.getRank(RankFactory.getRecruitIdentifier()));
            }
            Messages.sendRankOfPlayerSuccessfullyChangedToRank(sender, playerName, rank.getName());

            targetClanPlayer.sendMessage(Messages.getYourRankHasBeenChangedToRank(rank.getName()));
        }
    }

    @Command(name = "info", isPlayerOnly = false, description = "Get the info of yourself or another player")
    public void playerInfoCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @OptionalParameter(String.class) Optional<String> playerNameOpt) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        if (playerNameOpt.isPresent()) {
            String playerName = playerNameOpt.get();
            ClanPlayerImpl targetClanPlayer = clansImpl.getClanPlayer(playerName);
            if (targetClanPlayer != null) {
                printInfo(commandSource, clanPlayer, targetClanPlayer);
            } else {
                Messages.sendWarningMessage(commandSource, Messages.PLAYER_DOES_NOT_EXIST);
            }
        } else {
            if (commandSource instanceof Player) {
                printInfo(commandSource, clanPlayer, clanPlayer);
            } else {
                Messages.sendWarningMessage(commandSource, Messages.YOU_NEED_TO_BE_A_PLAYER_TO_PERFORM_THIS_COMMAND);
            }
        }
    }

    private void printInfo(CommandSource commandSource, ClanPlayerImpl clanPlayer, ClanPlayerImpl targetClanPlayer) {
        ClanImpl clan = targetClanPlayer.getClan();
        Optional<Player> playerOpt = Sponge.getServer().getPlayer(targetClanPlayer.getUUID());
        VerticalTable table = new VerticalTable(" Player info " + targetClanPlayer.getName(), 0);
        if (clan != null) {
            table.setValue("Clan", Text.join(clan.getTagColored(), Text.of(" " + clan.getName())));
            table.setValue("Rank", Text.of(targetClanPlayer.getRank().getName()));
        } else {
            table.setValue("Clan", Text.builder("None").color(TextColors.GRAY).build());
            table.setValue("Rank", Text.builder("None").color(TextColors.GRAY).build());
        }
        Text lastOnlineMessage;
        if (playerOpt.isPresent() && playerOpt.get().isOnline()) {
            lastOnlineMessage = Text.builder("Online").color(TextColors.GREEN).build();
        } else {
            lastOnlineMessage = Text.of(targetClanPlayer.getLastOnline().getDifferenceInText());
        }
        table.setValue("Last Online", Text.of(lastOnlineMessage));
        table.setValue("Kills", Utils.formatKdr(targetClanPlayer.getKills(), targetClanPlayer.getKillsHigh(), targetClanPlayer.getKillsMedium(), targetClanPlayer.getKillsLow()));
        table.setValue("Deaths", Utils.formatKdr(targetClanPlayer.getDeaths(), targetClanPlayer.getDeathsHigh(), targetClanPlayer.getDeathsMedium(), targetClanPlayer.getDeathsLow()));
        table.setValue("KDR", Text.of(String.valueOf(targetClanPlayer.getKDR())));

        if (clanPlayer.equals(targetClanPlayer)
                || (clanPlayer.getClan() != null && targetClanPlayer.getClan() != null && clanPlayer.getClan().isPlayerFriendlyToThisClan(
                targetClanPlayer))) {
            table.setValue("Kill Factor", Text.builder("None").color(TextColors.GRAY).build());
            table.setValue("Death Factor", Text.builder("None").color(TextColors.GRAY).build());
        } else {
            table.setValue("Kill Factor", Text.of(KillDeathFactorHandler.getInstance().getKillFactor(clanPlayer, targetClanPlayer).getUserFriendlyName()));
            table.setValue("Death Factor", Text.of(KillDeathFactorHandler.getInstance().getDeathFactor(targetClanPlayer, clanPlayer).getUserFriendlyName()));
        }
        table.draw(commandSource, 0);
    }

}
