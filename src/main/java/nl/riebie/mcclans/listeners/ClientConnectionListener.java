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

package nl.riebie.mcclans.listeners;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanInvite;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.player.LastOnlineImpl;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by Kippers on 13/02/2016.
 */
public class ClientConnectionListener {

    @Listener
    public void onClientConnectionJoin(ClientConnectionEvent.Join event) {
        final Player player = event.getTargetEntity();
        final ClanPlayerImpl clanPlayer = ClansImpl.getInstance().getClanPlayer(player.getUniqueId());
        if (clanPlayer != null) {
            // Check if player has a pending clan invite
            final ClanInvite clanInvite = clanPlayer.getClanInvite();
            if (clanInvite != null) {
                Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
                taskBuilder.execute(new Runnable() {
                    @Override
                    public void run() {
                        Messages.sendInvitedToClan(player, clanInvite.getClan().getName(), clanInvite.getClan().getTagColored());
                    }
                });
                taskBuilder.delayTicks(20L).submit(MCClans.getPlugin());
            }

            // Check if player's clan has a pending ally invite
            ClanImpl clan = clanPlayer.getClan();
            if (clan != null) {
                if (clanPlayer.getRank().hasPermission("ally")) {
                    final ClanImpl invitingAlly = clan.getInvitingAlly();
                    if (invitingAlly != null) {
                        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
                        taskBuilder.execute(new Runnable() {
                            @Override
                            public void run() {
                                Messages.sendYourClanHasBeenInvitedToBecomeAlliesWithClan(clanPlayer, invitingAlly.getName(), invitingAlly.getTagColored());
                            }
                        });
                        taskBuilder.delayTicks(20L).submit(MCClans.getPlugin());
                    }
                }
            }

            // TODO messages
            if (Config.getBoolean(Config.USE_ECONOMY)) {
                Currency currency = MCClans.getPlugin().getServiceHelper().currency;
                double debt = clanPlayer.getEconomyStats().getDebt();
                double clanDebt = clan == null ? 0 : clan.getBank().getDebt();
                if (debt > 0) {
                    clanPlayer.sendMessage(
                            Text.join(
                                    Text.builder("You are ").color(TextColors.RED).build(),
                                    Text.builder(String.valueOf(Utils.round(debt, 2)) + " ").color(TextColors.WHITE).build(),
                                    Utils.getDisplayName(currency, debt).toBuilder().color(TextColors.RED).build(),
                                    Text.builder(" in debt to your clan!").color(TextColors.RED).build()
                            )
                    );
                }
                if (clanDebt > 0) {
                    clanPlayer.sendMessage(
                            Text.join(
                                    Text.builder("Your clan is ").color(TextColors.RED).build(),
                                    Text.builder(String.valueOf(Utils.round(clanDebt, 2)) + " ").color(TextColors.WHITE).build(),
                                    Utils.getDisplayName(currency, clanDebt).toBuilder().color(TextColors.RED).build(),
                                    Text.builder(" in debt!").color(TextColors.RED).build()
                            )
                    );
                }
            }
        }
    }

    @Listener
    public void onClientConnectionDisconnect(ClientConnectionEvent.Disconnect event) {
        ClanPlayerImpl clanPlayer = ClansImpl.getInstance().getClanPlayer(event.getTargetEntity().getUniqueId());
        if (clanPlayer != null) {
            clanPlayer.setLastOnline(new LastOnlineImpl());
        }
    }

}
