package nl.riebie.mcclans.listeners;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.api.enums.Permission;
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
 * Created by Koen on 13/02/2016.
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
                if (clanPlayer.getRank().hasPermission(Permission.ally)) {
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
