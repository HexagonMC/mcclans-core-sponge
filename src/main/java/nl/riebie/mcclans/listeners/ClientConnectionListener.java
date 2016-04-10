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
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanInvite;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.player.LastOnlineImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;

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
