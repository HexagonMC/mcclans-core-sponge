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

import nl.riebie.mcclans.api.KillDeath;
import nl.riebie.mcclans.api.enums.KillDeathFactor;
import nl.riebie.mcclans.api.events.ClanOwnerChangeEvent;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.events.EventDispatcher;
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
 * Created by Kippers on 26/02/2016.
 */
public class ClanPlayerCommands {

    @Command(name = "setrank", description = "Set the rank of a member of your clan", isPlayerOnly = true, isClanOnly = true, clanPermission = "setrank", spongePermission = "mcclans.user.player.setrank")
    public void playerSetRankCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "playerName") ClanPlayerImpl targetPlayer,
                                     @Parameter(name = "rankName") String rankName) {
        ClanImpl clan = clanPlayer.getClan();
        if (!clan.equals(targetPlayer.getClan())) {
            Messages.sendPlayerNotAMemberOfThisClan(sender, targetPlayer.getName());
            return;
        }

        RankImpl rank = clanPlayer.getClan().getRank(rankName);

        if (rank == null) {
            Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
        } else if (targetPlayer.getRank().getName().toLowerCase().equals(RankFactory.getOwnerIdentifier().toLowerCase())) {
            Messages.sendWarningMessage(sender, Messages.YOU_CANNOT_OVERWRITE_THE_OWNER_RANK);
        } else {
            if (RankFactory.getOwnerIdentifier().toLowerCase().equals(rank.getName().toLowerCase())) {
                if (!clan.getOwner().equals(clanPlayer)) {
                    Messages.sendWarningMessage(sender, Messages.ONLY_THE_OWNER_CAN_CHANGE_OWNER);
                    return;
                }
                ClanOwnerChangeEvent.User clanOwnerChangeEvent = EventDispatcher.getInstance().dispatchUserClanOwnerChangeEvent(clan, clan.getOwner(), targetPlayer);
                if (clanOwnerChangeEvent.isCancelled()) {
                    Messages.sendWarningMessage(sender, clanOwnerChangeEvent.getCancelMessage());
                    return;
                } else {
                    clan.setOwnerInternal(targetPlayer);
                }
            } else {
                targetPlayer.setRankInternal(rank);
            }

            Messages.sendRankOfPlayerSuccessfullyChangedToRank(sender, targetPlayer.getName(), rank.getName());

            targetPlayer.sendMessage(Messages.getYourRankHasBeenChangedToRank(rank.getName()));
        }
    }

    @Command(name = "info", description = "Get the info of yourself or another player", spongePermission = "mcclans.user.player.info")
    public void playerInfoCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "playerName") Optional<ClanPlayerImpl> playerOpt) {
        if (playerOpt.isPresent()) {
            ClanPlayerImpl targetClanPlayer = playerOpt.get();

            printInfo(commandSource, clanPlayer, targetClanPlayer);

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
        KillDeath kdr = targetClanPlayer.getKillDeath();
        table.setValue("Kills", Utils.formatKdr(kdr.getTotalKills(), kdr.getKills(KillDeathFactor.HIGH), kdr.getKills(KillDeathFactor.MEDIUM), kdr.getKills(KillDeathFactor.LOW)));
        table.setValue("Deaths", Utils.formatKdr(kdr.getTotalDeaths(), kdr.getDeaths(KillDeathFactor.HIGH), kdr.getDeaths(KillDeathFactor.MEDIUM), kdr.getDeaths(KillDeathFactor.LOW)));
        table.setValue("KDR", Text.of(String.valueOf(kdr.getKDR())));

        if (clanPlayer == null || clanPlayer.equals(targetClanPlayer)
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
