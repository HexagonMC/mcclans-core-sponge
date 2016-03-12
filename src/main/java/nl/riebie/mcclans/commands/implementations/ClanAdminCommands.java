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
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.constraints.length.LengthConstraints;
import nl.riebie.mcclans.commands.constraints.regex.RegexConstraints;
import nl.riebie.mcclans.comparators.MemberComparator;
import nl.riebie.mcclans.database.DatabaseHandler;
import nl.riebie.mcclans.messages.Messages;
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
            @Parameter(name = "clanTag", length = LengthConstraints.CLAN_TAG, regex = RegexConstraints.CLAN_TAG) String clanTag,
            @Multiline @Parameter(name = "clanName", length = LengthConstraints.CLAN_NAME, regex = RegexConstraints.CLAN_NAME) String clanName
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
                if (clansImpl.tagIsFree(clanTag)) {
                    ClanImpl clanImpl = clansImpl.createClan(clanTag, clanName, targetClanPlayer);
                    Messages.sendBroadcastMessageClanCreatedBy(clanImpl.getName(), clanImpl.getTagColored(), commandSource.getName());
                } else {
                    Messages.sendWarningMessage(commandSource, Messages.CLANTAG_EXISTS_ALREADY);
                }
            } else {
                Messages.sendPlayerAlreadyInClan(commandSource, targetClanPlayer.getName());
            }
        }
    }

    @Command(name = "disband", description = "Disband a clan", spongePermission = "mcclans.admin.disband")
    public void adminDisbandCommand(CommandSource commandSource, @Parameter(name = "clanTag") String clanTag) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(clanTag);
        if (clan == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
        } else {
            Messages.sendBroadcastMessageClanDisbandedBy(clan.getName(), clan.getTagColored(), commandSource.getName());
            clansImpl.disbandClan(clan.getTag());
        }
    }

    @Command(name = "home", description = "Teleport to a clan home", isPlayerOnly = true, spongePermission = "mcclans.admin.home")
    public void adminHomeCommand(CommandSource commandSource, @Parameter(name = "clanTag")  String clanTag) {
        Player player = (Player) commandSource;
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(clanTag);
        if (clan == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
        } else {
            Location<World> teleportLocation = clan.getHome();
            if (teleportLocation == null) {
                Messages.sendWarningMessage(commandSource, Messages.CLAN_HOME_LOCATION_IS_NOT_SET);
            } else {
                player.setLocation(teleportLocation);
            }
        }
    }

    @Command(name = "invite", description = "Invite a player to a clan", spongePermission = "mcclans.admin.invite")
    public void adminInviteCommand(CommandSource commandSource, @Parameter(name = "clanTag") String clanTag, @Parameter(name = "playerName") String playerName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(clanTag);
        if (clan == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
        } else {
            UUID uuid = UUIDUtils.getUUID(playerName);
            Optional<Player> playerOpt = null;
            if (uuid == null) {
                playerOpt = Optional.empty();
            } else {
                playerOpt = Sponge.getServer().getPlayer(uuid);
            }
            if (!playerOpt.isPresent()) {
                Messages.sendPlayerNotOnline(commandSource, playerName);
                return;
            }

            Player player = playerOpt.get();
            ClanPlayerImpl invitedClanPlayer = clansImpl.getClanPlayer(player.getUniqueId());
            if (invitedClanPlayer == null) {
                invitedClanPlayer = clansImpl.createClanPlayer(player.getUniqueId(), player.getName());
            }
            if (invitedClanPlayer.getClan() != null) {
                Messages.sendPlayerAlreadyInClan(commandSource, player.getName());
            } else if (invitedClanPlayer.getClanInvite() != null) {
                Messages.sendPlayerAlreadyInvitedByAnotherClan(commandSource, player.getName());
            } else {
                invitedClanPlayer.inviteToClan(clan);
                clan.addInvitedPlayer(invitedClanPlayer);
                Messages.sendInvitedToClan(player, clan.getName(), clan.getTagColored());
                Messages.sendClanBroadcastMessagePlayerInvitedToTheClan(clan, player.getName(), commandSource.getName(), Permission.invite);
            }
        }
    }

    @Command(name = "remove", description = "Remove a player from a clan", spongePermission = "mcclans.admin.remove")
    public void adminRemoveCommand(CommandSource commandSource, @Parameter(name = "clanTag") String clanTag, @Parameter(name = "playerName") String playerName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(clanTag);
        if (clan == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
        } else {
            ClanPlayerImpl toBeRemovedClanPlayer = clan.getMember(playerName);
            if (toBeRemovedClanPlayer != null) {
                if (toBeRemovedClanPlayer.getName().equals(commandSource.getName())) {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_REMOVE_YOURSELF_FROM_THE_CLAN);
                } else if (toBeRemovedClanPlayer.getName().equalsIgnoreCase(clan.getOwner().getName())) {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_REMOVE_THE_OWNER_FROM_THE_CLAN);
                } else {
                    clan.removeMember(playerName);
                    Messages.sendClanBroadcastMessagePlayerRemovedFromTheClanBy(clan, toBeRemovedClanPlayer.getName(), commandSource.getName());
                    Messages.sendYouHaveBeenRemovedFromClan(toBeRemovedClanPlayer, clan.getName());
                }
            } else {
                Messages.sendPlayerNotAMemberOfThisClan(commandSource, playerName);
            }
        }
    }

    @Command(name = "sethome", description = "Set the location of a clan home", isPlayerOnly = true, spongePermission = "mcclans.admin.sethome")
    public void adminSetHomeCommand(CommandSource commandSource, @Parameter(name = "clanTag") String clanTag) {
        Player player = (Player) commandSource;
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(clanTag);
        if (clan == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
        } else {
            Location<World> location = player.getLocation();
            clan.setHome(location);
            Messages.sendBasicMessage(commandSource, Messages.CLAN_HOME_LOCATION_SET);
        }
    }

    @Command(name = "setowner", description = "Set the owner of a clan", spongePermission = "mcclans.admin.setowner")
    public void adminSetOwnerCommand(CommandSource commandSource, @Parameter(name = "clanTag") String clanTag, @Parameter(name = "playerName") String playerName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(clanTag);
        if (clan == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
        } else {
            if (!clan.isPlayerMember(playerName)) {
                Messages.sendPlayerNotAMemberOfThisClan(commandSource, playerName);
                return;
            }

            RankImpl rank = clan.getRank(RankFactory.getOwnerIdentifier());
            ClanPlayerImpl targetClanPlayer = clan.getMember(playerName);

            if (targetClanPlayer.getRank().getName().toLowerCase().equals(RankFactory.getOwnerIdentifier().toLowerCase())) {
                Messages.sendWarningMessage(commandSource, Messages.THIS_PLAYER_IS_ALREADY_THE_OWNER);
            } else {
                clan.getOwner().setRank(clan.getRank(RankFactory.getRecruitIdentifier()));
                clan.setOwner(targetClanPlayer);
                targetClanPlayer.setRank(rank);
                Messages.sendRankOfPlayerSuccessfullyChangedToRank(commandSource, playerName, rank.getName());
            }
        }
    }
}
