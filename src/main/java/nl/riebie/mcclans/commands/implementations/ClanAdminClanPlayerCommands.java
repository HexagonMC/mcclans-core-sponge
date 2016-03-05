package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.database.DatabaseHandler;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.UUIDUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by K.Volkers on 1-3-2016.
 */
public class ClanAdminClanPlayerCommands {

    @Command(name = "remove", description = "Remove all player properties", spongePermission = "mcclans.admin.clanplayer.remove")
    public void clanPlayerRemoveCommand(CommandSource commandSource, @Parameter(name = "playerName") String playerName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanPlayerImpl targetClanPlayer = clansImpl.getClanPlayer(playerName);

        if (targetClanPlayer == null) {
            Messages.sendWarningMessage(commandSource, Messages.PLAYER_DOES_NOT_EXIST);
        } else {
            clansImpl.removeClanPlayer(targetClanPlayer);
            Messages.sendBasicMessage(commandSource, Messages.PLAYER_PROPERTIES_REMOVED);
        }
    }

    @Command(name = "setdeaths", description = "Set the deaths of a player", spongePermission = "mcclans.admin.clanplayer.setdeaths")
    public void clanPlayerSetDeathsCommand(CommandSource commandSource, @Parameter(name = "playerName") String playerName,
                                           @Parameter(name = "highDeaths") int high, @Parameter(name = "mediumDeaths") int medium, @Parameter(name = "lowDeaths") int low) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanPlayerImpl targetClanPlayer = clansImpl.getClanPlayer(playerName);

        if (targetClanPlayer == null) {
            Messages.sendWarningMessage(commandSource, Messages.PLAYER_DOES_NOT_EXIST);
        } else {
            targetClanPlayer.setDeathsHigh(high);
            targetClanPlayer.setDeathsMedium(medium);
            targetClanPlayer.setDeathsLow(low);
            Messages.sendBasicMessage(commandSource, Messages.PLAYER_STATISTICS_SUCCESSFULLY_MODIFIED);
        }
    }

    @Command(name = "setkills", description = "Set the kills of a player", spongePermission = "mcclans.admin.clanplayer.setkills")
    public void clanPlayerSetKillsCommand(CommandSource commandSource, @Parameter(name = "playerName") String playerName,
                                          @Parameter(name = "highDeaths") int high, @Parameter(name = "mediumDeaths") int medium, @Parameter(name = "lowDeaths") int low) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanPlayerImpl targetClanPlayer = clansImpl.getClanPlayer(playerName);

        if (targetClanPlayer == null) {
            Messages.sendWarningMessage(commandSource, Messages.PLAYER_DOES_NOT_EXIST);
        } else {
            targetClanPlayer.setKillsHigh(high);
            targetClanPlayer.setKillsMedium(medium);
            targetClanPlayer.setKillsLow(low);
            Messages.sendBasicMessage(commandSource, Messages.PLAYER_STATISTICS_SUCCESSFULLY_MODIFIED);
        }
    }

    @Command(name = "transfer", description = "Transfer all player properties to a different player", spongePermission = "mcclans.admin.clanplayer.transfer")
    public void clanPlayerTransferCommand(CommandSource commandSource, @Parameter(name = "playerName") String targetClanPlayer, @Parameter(name = "newPlayerName") String newPlayerName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanPlayerImpl oldClanPlayer = clansImpl.getClanPlayer(targetClanPlayer);
        ClanPlayerImpl newClanPlayer = clansImpl.getClanPlayer(newPlayerName); // Needs to be null to prevent information from being overwritten /

        UUID newPlayerUuid = UUIDUtils.getUUID(newPlayerName);
        Optional<Player> newPlayerOpt;
        if (newPlayerUuid == null) {
            newPlayerOpt = Optional.empty();
        } else {
            newPlayerOpt = Sponge.getServer().getPlayer(newPlayerUuid);
        }

        if (oldClanPlayer == null) {
            Messages.sendWarningMessage(commandSource, Messages.PLAYER_DOES_NOT_EXIST);
        } else if (!newPlayerOpt.isPresent() || !newPlayerOpt.get().isOnline()) {
            Messages.sendPlayerNotOnline(commandSource, newPlayerName);
        } else if (targetClanPlayer.equalsIgnoreCase(newPlayerName)) {
            Messages.sendWarningMessage(commandSource, Messages.THESE_ARE_THE_SAME_PLAYERS);
        } else if (newClanPlayer != null) {
            Messages.sendPlayerHasAClanPlayerPleaseRemoveThisFirst(commandSource, newClanPlayer.getName());
        } else {
            clansImpl.transferClanPlayer(oldClanPlayer, newPlayerOpt.get().getUniqueId(), newPlayerOpt.get().getName());
            Messages.sendAllPlayerPropertiesTransferredFromPlayerToPlayer(commandSource, targetClanPlayer, newPlayerOpt.get().getName());
        }
    }
}
