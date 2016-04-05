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

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.enums.KillDeathFactor;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.UUIDUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Kippers on 1-3-2016.
 */
public class ClanAdminClanPlayerCommands {

    @Command(name = "remove", description = "Remove all player properties", spongePermission = "mcclans.admin.clanplayer.remove")
    public void clanPlayerRemoveCommand(CommandSource commandSource, @Parameter(name = "playerName") ClanPlayerImpl targetClanPlayer) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        clansImpl.removeClanPlayer(targetClanPlayer);
        Messages.sendBasicMessage(commandSource, Messages.PLAYER_PROPERTIES_REMOVED);
    }

    @Command(name = "setdeaths", description = "Set the deaths of a player", spongePermission = "mcclans.admin.clanplayer.setdeaths")
    public void clanPlayerSetDeathsCommand(CommandSource commandSource, @Parameter(name = "playerName") ClanPlayerImpl targetClanPlayer,
                                           @Parameter(name = "highDeaths") int high, @Parameter(name = "mediumDeaths") int medium, @Parameter(name = "lowDeaths") int low) {
        targetClanPlayer.getKillDeath().setDeaths(KillDeathFactor.HIGH, high);
        targetClanPlayer.getKillDeath().setDeaths(KillDeathFactor.MEDIUM, medium);
        targetClanPlayer.getKillDeath().setDeaths(KillDeathFactor.LOW, low);
        Messages.sendBasicMessage(commandSource, Messages.PLAYER_STATISTICS_SUCCESSFULLY_MODIFIED);
    }

    @Command(name = "setkills", description = "Set the kills of a player", spongePermission = "mcclans.admin.clanplayer.setkills")
    public void clanPlayerSetKillsCommand(CommandSource commandSource, @Parameter(name = "playerName") ClanPlayerImpl targetClanPlayer,
                                          @Parameter(name = "highDeaths") int high, @Parameter(name = "mediumDeaths") int medium, @Parameter(name = "lowDeaths") int low) {
        targetClanPlayer.getKillDeath().setKills(KillDeathFactor.HIGH, high);
        targetClanPlayer.getKillDeath().setKills(KillDeathFactor.MEDIUM, medium);
        targetClanPlayer.getKillDeath().setKills(KillDeathFactor.LOW, low);
        Messages.sendBasicMessage(commandSource, Messages.PLAYER_STATISTICS_SUCCESSFULLY_MODIFIED);
    }

    @Command(name = "transfer", description = "Transfer all player properties to a different player", spongePermission = "mcclans.admin.clanplayer.transfer")
    public void clanPlayerTransferCommand(CommandSource commandSource, @Parameter(name = "playerName") ClanPlayerImpl oldClanPlayer, @Parameter(name = "newPlayerName") String newPlayerName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
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
        } else if (oldClanPlayer.getName().equalsIgnoreCase(newPlayerName)) {
            Messages.sendWarningMessage(commandSource, Messages.THESE_ARE_THE_SAME_PLAYERS);
        } else if (newClanPlayer != null) {
            Messages.sendPlayerHasAClanPlayerPleaseRemoveThisFirst(commandSource, newClanPlayer.getName());
        } else {
            clansImpl.transferClanPlayer(oldClanPlayer, newPlayerOpt.get().getUniqueId(), newPlayerOpt.get().getName());
            Messages.sendAllPlayerPropertiesTransferredFromPlayerToPlayer(commandSource, oldClanPlayer.getName(), newPlayerOpt.get().getName());
        }
    }
}
