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
import nl.riebie.mcclans.api.events.*;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.commands.Toggle;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.constraints.ClanNameConstraint;
import nl.riebie.mcclans.commands.constraints.ClanTagConstraint;
import nl.riebie.mcclans.comparators.MemberComparator;
import nl.riebie.mcclans.events.EventDispatcher;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.persistence.DatabaseHandler;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.TableAdapter;
import nl.riebie.mcclans.utils.UUIDUtils;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by riebie on 28/02/2016.
 */
public class ClanAdminCommands {

    @ChildGroup(ClanAdminClanPlayerCommands.class)
    @Command(name = "clanplayer", description = "Top command for all admin clanplayer commands", spongePermission = "mcclans.admin.clanplayer.helppage")
    public void adminClanPlayerRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @ChildGroup(ClanAdminTagCommands.class)
    @Command(name = "tag", description = "Top command for all admin tag commands", spongePermission = "mcclans.admin.tag.helppage")
    public void adminTagRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @Command(name = "coords", description = "See the coordinates of a clan's members", spongePermission = "mcclans.admin.coords")
    public void adminCoordsCommand(CommandSource commandSource, @Parameter(name = "clanTag") ClanImpl clan, @PageParameter int page) {
        List<Player> onlineMembers = new ArrayList<Player>();
        List<ClanPlayerImpl> members = clan.getMembersImpl();
        for (ClanPlayerImpl member : members) {
            Optional<Player> playerOpt = Sponge.getServer().getPlayer(member.getUUID());
            if (playerOpt.isPresent() && playerOpt.get().isOnline()) {
                onlineMembers.add(playerOpt.get());
            }
        }
        java.util.Collections.sort(members, new MemberComparator());

        HorizontalTable<Player> table = new HorizontalTable<>("Clan coordinates " + clan.getName(), 10, (TableAdapter<Player>) (row, player, index) -> {
            if (player.isOnline()) {
                Location<World> location = player.getLocation();
                row.setValue("Player", Text.of(player.getName()));
                row.setValue("Location", Utils.formatLocation(location));

            }
        });
        table.defineColumn("Player", 30);
        table.defineColumn("Location", 30);

        table.draw(onlineMembers, page, commandSource);
    }

    @Command(name = "backup", description = "Backup the database", spongePermission = "mcclans.admin.backup")
    public void adminBackupCommand(CommandSource commandSource) {
        Messages.sendBasicMessage(commandSource, Messages.SYSTEM_BACKUP_INITIATED);
        DatabaseHandler.getInstance().backup();
    }

    @Command(name = "create", description = "Create a clan", spongePermission = "mcclans.admin.create")
    public void adminCreateCommand(
            CommandSource commandSource,
            @Parameter(name = "owner") String owner,
            @Parameter(name = "clanTag", constraint = ClanTagConstraint.class) String clanTag,
            @Multiline @Parameter(name = "clanName", constraint = ClanNameConstraint.class) String clanName
    ) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanPlayerImpl targetClanPlayer = clansImpl.getClanPlayer(owner);

        if (targetClanPlayer == null) {
            UUID uuid = UUIDUtils.getUUID(owner);
            Optional<Player> playerOpt;
            if (uuid == null) {
                playerOpt = Optional.empty();
            } else {
                playerOpt = Sponge.getServer().getPlayer(uuid);
            }
            if (playerOpt.isPresent() && playerOpt.get().isOnline()) {
                targetClanPlayer = clansImpl.createClanPlayer(playerOpt.get().getUniqueId(), owner);
            }
        }

        if (targetClanPlayer == null) {
            Messages.sendPlayerNotOnline(commandSource, owner);
        } else {
            if (targetClanPlayer.getClan() == null) {
                if (clansImpl.isTagAvailable(clanTag)) {
                    ClanCreateEvent.Admin clanCreateEvent = EventDispatcher.getInstance().dispatchAdminClanCreateEvent(clanTag, clanName, targetClanPlayer);
                    if (clanCreateEvent.isCancelled()) {
                        Messages.sendWarningMessage(commandSource, clanCreateEvent.getCancelMessage());
                        return;
                    }
                    ClanImpl clanImpl = clansImpl.createClanInternal(clanTag, clanName, targetClanPlayer);
                    Messages.sendBroadcastMessageClanCreatedBy(clanImpl.getName(), clanImpl.getTagColored(), commandSource.getName());
                } else {
                    Messages.sendWarningMessage(commandSource, Messages.CLAN_TAG_EXISTS_ALREADY);
                }
            } else {
                Messages.sendPlayerAlreadyInClan(commandSource, targetClanPlayer.getName());
            }
        }
    }

    @Command(name = "disband", description = "Disband a clan", spongePermission = "mcclans.admin.disband")
    public void adminDisbandCommand(CommandSource commandSource, @Parameter(name = "clanTag") ClanImpl clan) {
        ClanDisbandEvent.Admin event = EventDispatcher.getInstance().dispatchAdminClanDisbandEvent(clan);
        if (event.isCancelled()) {
            Messages.sendWarningMessage(commandSource, event.getCancelMessage());
        } else {
            ClansImpl clansImpl = ClansImpl.getInstance();

            Messages.sendBroadcastMessageClanDisbandedBy(clan.getName(), clan.getTagColored(), commandSource.getName());
            clansImpl.disbandClanInternal(clan);
        }
    }

    @Command(name = "home", description = "Teleport to a clan home", isPlayerOnly = true, spongePermission = "mcclans.admin.home")
    public void adminHomeCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "clanTag") ClanImpl clan) {
        Player player = (Player) commandSource;
        Location<World> teleportLocation = clan.getHome();
        if (teleportLocation == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_HOME_LOCATION_IS_NOT_SET);
            return;
        }
        ClanHomeTeleportEvent.Admin event = EventDispatcher.getInstance().dispatchAdminClanHomeTeleportEvent(clanPlayer, clan);
        if (event.isCancelled()) {
            Messages.sendWarningMessage(player, event.getCancelMessage());
        } else {
            player.setLocation(teleportLocation);
        }
    }

    @Command(name = "invite", description = "Invite a player to a clan", spongePermission = "mcclans.admin.invite")
    public void adminInviteCommand(CommandSource commandSource, @Parameter(name = "clanTag") ClanImpl clan, @Parameter(name = "playerName") ClanPlayerImpl invitedClanPlayer) {
        Optional<Player> playerOpt = Sponge.getServer().getPlayer(invitedClanPlayer.getUUID());
        if (!playerOpt.isPresent()) {
            Messages.sendPlayerNotOnline(commandSource, invitedClanPlayer.getName());
            return;
        }

        Player player = playerOpt.get();

        if (invitedClanPlayer.getClan() != null) {
            Messages.sendPlayerAlreadyInClan(commandSource, player.getName());
        } else if (invitedClanPlayer.getClanInvite() != null) {
            Messages.sendPlayerAlreadyInvitedByAnotherClan(commandSource, player.getName());
        } else {
            invitedClanPlayer.inviteToClan(clan);
            clan.addInvitedPlayer(invitedClanPlayer);
            Messages.sendInvitedToClan(player, clan.getName(), clan.getTagColored());
            Messages.sendClanBroadcastMessagePlayerInvitedToTheClan(clan, player.getName(), commandSource.getName(), "invite");
        }
    }

    @Command(name = "remove", description = "Remove a player from a clan", spongePermission = "mcclans.admin.remove")
    public void adminRemoveCommand(CommandSource commandSource, @Parameter(name = "clanTag") ClanImpl clan, @Parameter(name = "playerName") String removeName) {
        ClanPlayerImpl toBeRemovedClanPlayer = ClansImpl.getInstance().getClanPlayer(removeName);
        if (toBeRemovedClanPlayer == null || !clan.equals(toBeRemovedClanPlayer.getClan())) {
            toBeRemovedClanPlayer = clan.getMember(removeName);
        }
        if (toBeRemovedClanPlayer == null) {
            Messages.sendPlayerNotAMemberOfThisClan(commandSource, removeName);
            return;
        }

        if (toBeRemovedClanPlayer.equals(clan.getOwner())) {
            Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_REMOVE_THE_OWNER_FROM_THE_CLAN);
        } else {
            ClanMemberLeaveEvent.Admin clanMemberLeaveEvent = EventDispatcher.getInstance().dispatchAdminClanMemberLeaveEvent(clan, toBeRemovedClanPlayer);
            if (clanMemberLeaveEvent.isCancelled()) {
                Messages.sendWarningMessage(commandSource, clanMemberLeaveEvent.getCancelMessage());
            } else {
                clan.removeMember(toBeRemovedClanPlayer);
                Messages.sendClanBroadcastMessagePlayerRemovedFromTheClanBy(clan, toBeRemovedClanPlayer.getName(), commandSource.getName());
                Messages.sendYouHaveBeenRemovedFromClan(toBeRemovedClanPlayer, clan.getName());
            }
        }
    }

    @Command(name = "sethome", description = "Set the location of a clan home", isPlayerOnly = true, spongePermission = "mcclans.admin.sethome")
    public void adminSetHomeCommand(CommandSource commandSource, @Parameter(name = "clanTag") ClanImpl clan) {
        Player player = (Player) commandSource;
        Location<World> location = player.getLocation();
        ClanSetHomeEvent.Admin clanSetHomeEvent = EventDispatcher.getInstance().dispatchClanSetHomeAdmin(clan, location, commandSource);
        if (clanSetHomeEvent.isCancelled()) {
            Messages.sendWarningMessage(commandSource, clanSetHomeEvent.getCancelMessage());
        } else {
            clan.setHomeInternal(location);
            Messages.sendBasicMessage(commandSource, Messages.CLAN_HOME_LOCATION_SET);
        }
    }

    @Command(name = "setrank", description = "Set the rank of a member of a clan", spongePermission = "mcclans.admin.setrank")
    public void adminSetRankCommand(CommandSource sender, @Parameter(name = "clanTag") ClanImpl clan, @Parameter(name = "playerName") ClanPlayerImpl targetClanPlayer,
                                    @Parameter(name = "rankName") String rankName) {
        if (!clan.equals(targetClanPlayer.getClan())) {
            Messages.sendPlayerNotAMemberOfThisClan(sender, targetClanPlayer.getName());
            return;
        }

        RankImpl rank = clan.getRank(rankName);

        if (rank == null) {
            Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
        } else if (targetClanPlayer.getRank().getName().toLowerCase().equals(RankFactory.getOwnerIdentifier().toLowerCase())) {
            Messages.sendWarningMessage(sender, Messages.YOU_CANNOT_OVERWRITE_THE_OWNER_RANK);
        } else {
            if (RankFactory.getOwnerIdentifier().toLowerCase().equals(rank.getName().toLowerCase())) {
                ClanOwnerChangeEvent.Admin clanOwnerChangeEvent = EventDispatcher.getInstance().dispatchAdminClanOwnerChangeEvent(clan, clan.getOwner(), targetClanPlayer);
                if (clanOwnerChangeEvent.isCancelled()) {
                    Messages.sendWarningMessage(sender, clanOwnerChangeEvent.getCancelMessage());
                    return;
                } else {
                    clan.setOwnerInternal(targetClanPlayer);
                }
            } else {
                targetClanPlayer.setRank(rank);
            }

            Messages.sendRankOfPlayerSuccessfullyChangedToRank(sender, targetClanPlayer.getName(), rank.getName());

            targetClanPlayer.sendMessage(Messages.getYourRankHasBeenChangedToRank(rank.getName()));
        }
    }

    @Command(name = "spy", description = "Spy on all the clan chats", isPlayerOnly = true, spongePermission = "mcclans.admin.spy")
    public void adminSpyCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "toggle") Toggle toggle) {
        if (toggle.getBoolean(clanPlayer.isSpy())) {
            Messages.sendBasicMessage(commandSource, Messages.YOU_ARE_NOW_SPYING_ON_ALL_CLAN_CHATS);
            clanPlayer.setSpy(true);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.YOU_HAVE_STOPPED_SPYING_ON_ALL_CLAN_CHATS);
            clanPlayer.setSpy(false);
        }
    }
}
