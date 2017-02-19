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
import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.api.KillDeath;
import nl.riebie.mcclans.api.enums.KillDeathFactor;
import nl.riebie.mcclans.api.events.*;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.constraints.ClanNameConstraint;
import nl.riebie.mcclans.commands.constraints.ClanTagConstraint;
import nl.riebie.mcclans.comparators.ClanKdrComparator;
import nl.riebie.mcclans.comparators.ClanPlayerKdrComparator;
import nl.riebie.mcclans.comparators.MemberComparator;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.events.EventDispatcher;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanHomeTeleportTask;
import nl.riebie.mcclans.player.ClanInvite;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.player.LastClanHomeTeleport;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.Row;
import nl.riebie.mcclans.table.TableAdapter;
import nl.riebie.mcclans.table.VerticalTable;
import nl.riebie.mcclans.utils.EconomyUtils;
import nl.riebie.mcclans.utils.UUIDUtils;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by riebie on 13/02/2016.
 */
public class ClanCommands {

    @ChildGroup(ClanAdminCommands.class)
    @Command(name = "admin", description = "Top command for all admin commands", spongePermission = "mcclans.admin.helppage")
    public void clanAdminRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @ChildGroup(ClanTagCommands.class)
    @Command(name = "tag", description = "Top command for all tag commands", spongePermission = "mcclans.user.tag.helppage")
    public void clanTagRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @ChildGroup(ClanAllyCommands.class)
    @Command(name = "ally", description = "Top command for all ally commands", spongePermission = "mcclans.user.ally.helppage")
    public void clanAllyRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @ChildGroup(ClanRankCommands.class)
    @Command(name = "rank", description = "Top command for all rank commands", spongePermission = "mcclans.user.rank.helppage")
    public void clanRankRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @ChildGroup(ClanFriendlyFireCommands.class)
    @Command(name = "friendlyfire", description = "Top command for all friendlyfire commands", spongePermission = "mcclans.user.friendlyfire.helppage")
    public void clanFriendlyFireRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @ChildGroup(ClanChatCommands.class)
    @Command(name = "chat", description = "Top command for all chat commands", spongePermission = "mcclans.user.chat.helppage")
    public void clanChatRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @ChildGroup(ClanPlayerCommands.class)
    @Command(name = "player", description = "Top command for all player commands", spongePermission = "mcclans.user.player.helppage")
    public void clanPlayerRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @ChildGroup(ClanBankCommands.class)
    @Command(name = "bank", description = "Top command for all bank commands", spongePermission = "mcclans.user.bank.helppage")
    public void clanBankRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @Command(name = "create", description = "Create a clan", isPlayerOnly = true, spongePermission = "mcclans.user.create")
    public void clanCreateCommand(
            CommandSource commandSource,
            ClanPlayerImpl clanPlayer,
            @Parameter(name = "clanTag", constraint = ClanTagConstraint.class) String clanTag,
            @Multiline @Parameter(name = "clanName", constraint = ClanNameConstraint.class) String clanName) {
        if (Utils.isClanTagOrNameBlocked(clanTag)) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_TAG_BLOCKED);
            return;
        } else if (Utils.isClanTagOrNameBlocked(clanName)) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_NAME_BLOCKED);
            return;
        }

        if (clanPlayer.getClan() == null) {
            if (ClansImpl.getInstance().isTagAvailable(clanTag)) {
                ClanCreateEvent.User event = EventDispatcher.getInstance().dispatchUserClanCreateEvent(clanTag, clanName, clanPlayer);
                if (event.isCancelled()) {
                    clanPlayer.sendMessage(Messages.getWarningMessage(event.getCancelMessage()));
                } else {
                    if (Config.getBoolean(Config.USE_ECONOMY)) {
                        double clanCreationCost = Config.getDouble(Config.CLAN_CREATION_COST);
                        boolean success = EconomyUtils.withdraw(clanPlayer.getUUID(), clanCreationCost);
                        String currencyName = MCClans.getPlugin().getServiceHelper().currency.getDisplayName().toPlain();

                        if (!success) {
                            Messages.sendYouDoNotHaveEnoughCurrency(commandSource, clanCreationCost, currencyName);
                            return;
                        }
                        Messages.sendYouWereChargedCurrency(commandSource, clanCreationCost, currencyName);
                    }

                    ClanImpl clanImpl = ClansImpl.getInstance().createClanInternal(clanTag, clanName, clanPlayer);
                    Messages.sendBroadcastMessageClanCreatedBy(clanImpl.getName(), clanImpl.getTagColored(), clanPlayer.getName());
                }
            } else {
                Messages.sendWarningMessage(commandSource, Messages.CLAN_TAG_EXISTS_ALREADY);
            }
        } else {
            Messages.sendWarningMessage(commandSource, Messages.YOU_ARE_ALREADY_IN_A_CLAN);
        }
    }

    @Command(name = "list", description = "Lists all the clans", spongePermission = "mcclans.user.list")
    public void clanListCommand(CommandSource commandSource, @PageParameter int page) {
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

        table.draw(clans, page, commandSource);
    }

    @Command(name = "invite", description = "Invite a player to your clan", isPlayerOnly = true, isClanOnly = true, clanPermission = "invite", spongePermission = "mcclans.user.invite")
    public void clanInviteCommand(Player player, ClanPlayerImpl clanPlayer, @Parameter(name = "playerName") String playerName) {
        // TODO SPONGE: Check if command is properly and fully implemented
        ClanImpl clan = clanPlayer.getClan();
        if (clan != null) {
            UUID uuid = UUIDUtils.getUUID(playerName);
            Optional<Player> invitedPlayerOpt = uuid == null ? Optional.empty() : Sponge.getServer().getPlayer(uuid);
            if (!invitedPlayerOpt.isPresent()) {
                Messages.sendPlayerNotOnline(player, playerName);
                return;
            }
            Player invitedPlayer = invitedPlayerOpt.get();
            ClansImpl clansInstance = ClansImpl.getInstance();
            ClanPlayerImpl invitedClanPlayer = clansInstance.getClanPlayer(uuid);

            if (invitedClanPlayer == null) {
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
                Messages.sendClanBroadcastMessagePlayerInvitedToTheClan(clan, invitedClanPlayerName, player.getName(), "invite");
                if (invitedPlayer != null && invitedPlayer.isOnline()) {
                    Messages.sendInvitedToClan(invitedPlayer, clan.getName(), clan.getTagColored());
                }
            }
        } else {
            Messages.sendWarningMessage(player, Messages.YOU_ARE_NOT_IN_A_CLAN);
        }
    }

    @Command(name = "disband", description = "Disband a clan", isPlayerOnly = true, isClanOnly = true, clanPermission = "disband", spongePermission = "mcclans.user.disband")
    public void clanDisbandCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanImpl clan = clanPlayer.getClan();
        ClanDisbandEvent.User event = EventDispatcher.getInstance().dispatchUserClanDisbandEvent(clan);
        if (event.isCancelled()) {
            Messages.sendWarningMessage(commandSource, event.getCancelMessage());
        } else {
            ClansImpl clansImpl = ClansImpl.getInstance();
            Messages.sendBroadcastMessageClanDisbandedBy(clan.getName(), clan.getTagColored(), commandSource.getName());
            clansImpl.disbandClanInternal(clan);
        }
    }

    @Command(name = "remove", description = "Remove a player from your clan", isPlayerOnly = true, isClanOnly = true, clanPermission = "remove", spongePermission = "mcclans.user.remove")
    public void clanRemoveCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "playerName") String removeName) {
        ClanImpl clan = clanPlayer.getClan();
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
            ClanMemberLeaveEvent.User clanMemberLeaveEvent = EventDispatcher.getInstance().dispatchUserClanMemberLeaveEvent(clan, toBeRemovedClanPlayer);
            if (clanMemberLeaveEvent.isCancelled()) {
                Messages.sendWarningMessage(commandSource, clanMemberLeaveEvent.getCancelMessage());
            } else {
                clan.removeMember(toBeRemovedClanPlayer);
                Messages.sendClanBroadcastMessagePlayerRemovedFromTheClanBy(clan, toBeRemovedClanPlayer.getName(), clanPlayer.getName());
                Messages.sendYouHaveBeenRemovedFromClan(toBeRemovedClanPlayer, clan.getName());
            }
        }
    }

    @Command(name = "accept", description = "Accept a pending clan invite", isPlayerOnly = true, spongePermission = "mcclans.user.accept")
    public void clanAcceptCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanInvite clanInvite = clanPlayer.getClanInvite();
        if (clanInvite == null) {
            Messages.sendWarningMessage(commandSource, Messages.NO_PENDING_CLAN_INVITE);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.CLAN_INVITE_ACCEPTED);
            clanInvite.accept();
        }
    }

    @Command(name = "decline", description = "Decline a pending clan invite", isPlayerOnly = true, spongePermission = "mcclans.user.decline")
    public void clanDeclineCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanInvite clanInvite = clanPlayer.getClanInvite();
        if (clanInvite == null) {
            Messages.sendWarningMessage(commandSource, Messages.NO_PENDING_CLAN_INVITE);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.CLAN_INVITE_DECLINED);
            clanInvite.decline();
        }
    }

    @Command(name = "roster", description = "See the members of a clan", spongePermission = "mcclans.user.roster")
    public void clanRosterCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "clanTag") Optional<ClanImpl> clanOpt,
                                  @PageParameter int page) {
        ClanImpl clan;
        if (clanOpt.isPresent()) {
            clan = clanOpt.get();
            printRoster(commandSource, clan, page);
        } else {
            if (commandSource instanceof Player) {
                clan = clanPlayer.getClan();
                if (clan != null) {
                    printRoster(commandSource, clan, page);
                } else {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_ARE_NOT_IN_A_CLAN);
                }
            } else {
                Messages.sendWarningMessage(commandSource, Messages.YOU_NEED_TO_BE_A_PLAYER_TO_PERFORM_THIS_COMMAND);
            }
        }
    }

    private void printRoster(CommandSource commandSource, ClanImpl clan, int page) {
        List<ClanPlayerImpl> members = clan.getMembersImpl();
        java.util.Collections.sort(members, new MemberComparator());

        HorizontalTable<ClanPlayerImpl> table = new HorizontalTable<>("Clan roster " + clan.getName(), 10,
                new TableAdapter<ClanPlayerImpl>() {

                    @Override
                    public void fillRow(Row row, ClanPlayerImpl member, int index) {
                        Optional<Player> playerOpt = Sponge.getServer().getPlayer(member.getUUID());
                        row.setValue("Player", Text.of(member.getName()));
                        row.setValue("Rank", Text.builder(member.getRank().getName()).color(TextColors.BLUE).build()); // todo get rank colored from Rank
                        Text lastOnlineMessage;
                        if (playerOpt.isPresent() && playerOpt.get().isOnline()) {
                            lastOnlineMessage = Text.builder("Online").color(TextColors.GREEN).build();
                        } else {
                            lastOnlineMessage = Text.of(member.getLastOnline().getDifferenceInText());
                        }
                        row.setValue("Last Online", lastOnlineMessage);

                    }
                });
        table.defineColumn("Player", 30);
        table.defineColumn("Rank", 20);
        table.defineColumn("Last Online", 30);

        table.draw(members, page, commandSource);
    }

    @Command(name = "leaderboard", description = "Lists all players sorted by KDR", spongePermission = "mcclans.user.leaderboard")
    public void clanLeaderboardCommand(CommandSource commandSource, @PageParameter int page) {
        List<ClanPlayerImpl> clanPlayers = ClansImpl.getInstance().getClanPlayerImpls();

        HorizontalTable<ClanPlayerImpl> table = new HorizontalTable<ClanPlayerImpl>("Players", 10, new TableAdapter<ClanPlayerImpl>() {

            @Override
            public void fillRow(Row row, ClanPlayerImpl clanPlayer, int i) {
                Text clanTag = Text.builder("None").color(TextColors.GRAY).build();
                ClanImpl clan = clanPlayer.getClan();
                if (clan != null) {
                    clanTag = clan.getTagColored();
                }

                row.setValue("Rank", Text.of(String.valueOf(i + 1)));
                row.setValue("Name", Text.of(clanPlayer.getName()));
                row.setValue("Clan", clanTag);
                row.setValue("KDR", Text.of(String.valueOf(clanPlayer.getKillDeath().getKDR())));

            }
        });
        table.defineColumn("Rank", 10);
        table.defineColumn("Name", 40);
        table.defineColumn("Clan", 15);
        table.defineColumn("KDR", 15);

        table.setComparator(new ClanPlayerKdrComparator());

        table.draw(clanPlayers, page, commandSource);
    }

    @Command(name = "info", description = "Get the info of a clan", spongePermission = "mcclans.user.info")
    public void clanInfoCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "clanTag") Optional<ClanImpl> clanOpt) {
        if (clanOpt.isPresent()) {
            ClanImpl clan = clanOpt.get();
            printInfo(commandSource, clan);
        } else {
            if (commandSource instanceof Player) {
                ClanImpl clan = clanPlayer.getClan();
                if (clan != null) {
                    printInfo(commandSource, clan);
                } else {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_ARE_NOT_IN_A_CLAN);
                }
            } else {
                Messages.sendWarningMessage(commandSource, Messages.YOU_NEED_TO_BE_A_PLAYER_TO_PERFORM_THIS_COMMAND);
            }
        }
    }

    private void printInfo(CommandSource commandSource, ClanImpl clan) {
        VerticalTable table = new VerticalTable(" Clan info " + clan.getTag(), 0);
        table.setValue("Clan", Text.join(clan.getTagColored(), Text.of(" " + clan.getName())));
        table.setValue("Owner", Text.of(clan.getOwner().getName()));
        table.setValue("Members", Text.of(String.valueOf(clan.getMembers().size())));
        table.setValue("Allies", generateAllyList(clan));
        table.setValue("Kills", Utils.formatKdr(clan.getTotalKills(),
                clan.getKills(KillDeathFactor.HIGH), clan.getKills(KillDeathFactor.MEDIUM), clan.getKills(KillDeathFactor.LOW)));
        table.setValue("Deaths", Utils.formatKdr(clan.getTotalDeaths(),
                clan.getDeaths(KillDeathFactor.HIGH), clan.getDeaths(KillDeathFactor.MEDIUM), clan.getDeaths(KillDeathFactor.LOW)));
        table.setValue("KDR", Text.of(String.valueOf(clan.getKDR())));
        table.setValue("Created", Text.of(clan.getCreationDateUserFriendly()));
        table.draw(commandSource, 0);
    }

    // TODO SPONGE make more efficient
    private Text generateAllyList(ClanImpl clan) {
        Text.Builder allyList = Text.builder();
        if (clan.getAlliesImpl().isEmpty()) {
            allyList.color(TextColors.GRAY).append(Text.of("None")).build();
        } else {
            boolean first = true;
            for (ClanImpl ally : clan.getAlliesImpl()) {
                if (!first) {
                    allyList.append(Text.of(", ")).build();
                }
                first = false;
                allyList.append(ally.getTagColored()).build();
            }
        }
        return allyList.build();
    }

    @Command(name = "resign", description = "Resign from a clan", isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.resign")
    public void clanResignCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        RankImpl rank = clanPlayer.getRank();
        ClanImpl clan = clanPlayer.getClan();
        if (rank.getName().equals(RankFactory.getOwnerIdentifier())) {
            Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_RESIGN_FROM_THE_CLAN_AS_THE_OWNER);
        } else {
            ClanMemberLeaveEvent.User clanMemberLeaveEvent = EventDispatcher.getInstance().dispatchUserClanMemberLeaveEvent(clan, clanPlayer);
            if (clanMemberLeaveEvent.isCancelled()) {
                Messages.sendWarningMessage(commandSource, clanMemberLeaveEvent.getCancelMessage());
            } else {
                clan.removeMember(clanPlayer);
                Messages.sendSuccessfullyResignedFromClan(commandSource, clan.getName());
                Messages.sendClanBroadcastMessagePlayerResignedFromTheClan(clan, clanPlayer.getName());
            }
        }
    }

    @Command(name = "coords", description = "See the coordinates of your clan members", isPlayerOnly = true, isClanOnly = true, clanPermission = "coords", spongePermission = "mcclans.user.coords")
    public void clanCoordsCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @PageParameter int page) {
        ClanImpl clan = clanPlayer.getClan();
        List<Player> onlineMembers = new ArrayList<Player>();
        List<ClanPlayerImpl> members = clan.getMembersImpl();
        for (ClanPlayerImpl member : members) {
            Optional<Player> playerOpt = Sponge.getServer().getPlayer(member.getUUID());
            if (playerOpt.isPresent() && playerOpt.get().isOnline()) {
                onlineMembers.add(playerOpt.get());
            }
        }
        java.util.Collections.sort(members, new MemberComparator());

        HorizontalTable<Player> table = new HorizontalTable<Player>("Clan coordinates " + clan.getName(), 10, (row, player, index) -> {
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

    @Command(name = "stats", description = "See the statistics of a clan's members", spongePermission = "mcclans.user.stats")
    public void clanStatsCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer,
                                 @Parameter(name = "clanTag") Optional<ClanImpl> clanOpt, @PageParameter int page) {
        ClanImpl clan;
        if (clanOpt.isPresent()) {
            clan = clanOpt.get();
            printStats(commandSource, clan, page);
        } else {
            if (commandSource instanceof Player) {
                clan = clanPlayer.getClan();
                if (clan != null) {
                    printStats(commandSource, clan, page);
                } else {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_ARE_NOT_IN_A_CLAN);
                }
            } else {
                Messages.sendWarningMessage(commandSource, Messages.YOU_NEED_TO_BE_A_PLAYER_TO_PERFORM_THIS_COMMAND);
            }
        }
    }

    private void printStats(CommandSource commandSource, ClanImpl clan, int page) {
        List<ClanPlayerImpl> members = clan.getMembersImpl();
        java.util.Collections.sort(members, new MemberComparator());

        HorizontalTable<ClanPlayerImpl> table = new HorizontalTable<>("Clan statistics " + clan.getName(), 10,
                (row, member, index) -> {
                    row.setValue("Player", Text.of(member.getName()));
                    KillDeath killDeath = member.getKillDeath();
                    row.setValue("KDR", Text.of(String.valueOf(killDeath.getKDR())));
                    row.setValue("Kills", Utils.formatKdr(killDeath.getTotalKills(),
                            killDeath.getKills(KillDeathFactor.HIGH), killDeath.getKills(KillDeathFactor.MEDIUM), killDeath.getKills(KillDeathFactor.LOW)));
                    row.setValue("Deaths", Utils.formatKdr(killDeath.getTotalDeaths(),
                            killDeath.getDeaths(KillDeathFactor.HIGH), killDeath.getDeaths(KillDeathFactor.MEDIUM), killDeath.getDeaths(KillDeathFactor.LOW)));

                });
        table.defineColumn("Player", 25);
        table.defineColumn("KDR", 10);
        table.defineColumn("Kills", 20);
        table.defineColumn("Deaths", 20);

        table.draw(members, page, commandSource);
    }

    @Command(name = "home", description = "Teleport to your clan home", isPlayerOnly = true, isClanOnly = true, clanPermission = "home", spongePermission = "mcclans.user.home")
    public void clanHomeCommand(Player player, ClanPlayerImpl clanPlayer) {
        ClanImpl clan = clanPlayer.getClan();
        Location<World> teleportLocation = clan.getHome();
        if (teleportLocation == null) {
            Messages.sendWarningMessage(player, Messages.CLAN_HOME_LOCATION_IS_NOT_SET);
            return;
        }

        LastClanHomeTeleport lastClanHomeTeleport = clanPlayer.getLastClanHomeTeleport();
        if (lastClanHomeTeleport == null || lastClanHomeTeleport.canPlayerTeleport()) {
            Location<World> currentPlayerLocation = player.getLocation();
            Location<World> lastTeleportInitiationLocation = clanPlayer.getLastTeleportInitiationLocation();
            if (lastTeleportInitiationLocation == null
                    || !lastTeleportInitiationLocation.getExtent().getName().equalsIgnoreCase(currentPlayerLocation.getExtent().getName())
                    || lastTeleportInitiationLocation.getPosition().distance(currentPlayerLocation.getPosition()) != 0) {
                ClanHomeTeleportEvent.User event = EventDispatcher.getInstance().dispatchUserClanHomeTeleportEvent(clanPlayer, clan);
                if (event.isCancelled()) {
                    Messages.sendWarningMessage(player, event.getCancelMessage());
                } else {
                    startTeleportTask(player, clanPlayer, teleportLocation, currentPlayerLocation);
                }
            } else {
                Messages.sendWarningMessage(player, Messages.YOU_NEED_TO_MOVE_BEFORE_ATTEMPTING_ANOTHER_TELEPORT);
            }
        } else {
            Messages.sendYouCanTeleportInXSeconds(player, lastClanHomeTeleport.secondsBeforePlayerCanTeleport());
        }
    }

    private void startTeleportTask(Player player, ClanPlayerImpl clanPlayer, Location<World> teleportLocation, Location<World> currentPlayerLocation) {
        clanPlayer.setLastTeleportInitiationLocation(currentPlayerLocation);
        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.interval(1, TimeUnit.SECONDS).execute(
                new ClanHomeTeleportTask(player, clanPlayer, teleportLocation, Config.getInteger(Config.TELEPORT_DELAY_SECONDS), false)
        ).submit(MCClans.getPlugin());
    }

    @Command(name = "sethome", description = "Set the location of your clan home", isPlayerOnly = true, isClanOnly = true, clanPermission = "sethome", spongePermission = "mcclans.user.sethome")
    public void clanSetHomeCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        Player player = (Player) commandSource;
        ClanImpl clan = clanPlayer.getClan();
        long setTimeDifference = (System.currentTimeMillis() - clan.getHomeSetTimeStamp()) / 1000;
        if (clan.getHomeSetTimeStamp() == -1 || setTimeDifference > Config.getInteger(Config.RE_SET_CLANHOME_COOLDOWN_SECONDS)) {
            ClanSetHomeEvent.User event = EventDispatcher.getInstance().dispatchClanSetHomeUser(clanPlayer, clan, player.getLocation());
            if (event.isCancelled()) {
                Messages.sendWarningMessage(commandSource, event.getCancelMessage());
            } else {
                if (Config.getBoolean(Config.USE_ECONOMY)) {
                    double setClanhomeBaseCost = Config.getDouble(Config.SET_CLANHOME_COST);
                    double reSetClanhomeCostIncrease = Config.getDouble(Config.RE_SET_CLANHOME_COST_INCREASE);

                    int homeSetTimes = clan.getHomeSetTimes();
                    double setClanhomeCost = setClanhomeBaseCost + (homeSetTimes * reSetClanhomeCostIncrease);
                    String currencyName = MCClans.getPlugin().getServiceHelper().currency.getDisplayName().toPlain();
                    boolean success = EconomyUtils.withdraw(clanPlayer.getUUID(), setClanhomeCost);
                    if (!success) {
                        Messages.sendYouDoNotHaveEnoughCurrency(player, setClanhomeCost, currencyName);
                        return;
                    }
                    Messages.sendYouWereChargedCurrency(player, setClanhomeCost, currencyName);
                }
                Location<World> location = player.getLocation();
                clan.setHomeInternal(location);
                clan.increaseHomeSetTimes();
                clan.setHomeSetTimeStamp(System.currentTimeMillis());
                Messages.sendBasicMessage(player, Messages.CLAN_HOME_LOCATION_SET);
            }
        } else {
            Messages.sendCannotSetClanhomeForAnotherXTime(commandSource, Config.getInteger(Config.RE_SET_CLANHOME_COOLDOWN_SECONDS) - setTimeDifference);
        }
    }

    // TODO command can use ClanPlayer (or rather Clan) info, but should also partly function for console (with less data displayd)
    // TODO allow clanPlayer to be optional mayhaps?
    @Command(name = "price", description = "See all the costs associated with clans", isPlayerOnly = true, spongePermission = "mcclans.user.price")
    public void clanPriceCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanImpl clan = clanPlayer.getClan();

        Text setHomeText = Text.of("0");
        Text clanCreationText = Text.of("0");
        Text teleportToClanHomeText = Text.of("0");

        if (Config.getBoolean(Config.USE_ECONOMY)) {
            double setClanhomeCost = Config.getDouble(Config.SET_CLANHOME_COST);
            setHomeText = Text.of(String.valueOf(setClanhomeCost));
            double reSetClanhomeCostIncrease = Config.getDouble(Config.RE_SET_CLANHOME_COST_INCREASE);
            if (reSetClanhomeCostIncrease != 0 && clan != null) {
                double setHomePriceIncrease = 0;
                setHomePriceIncrease = clan.getHomeSetTimes() * reSetClanhomeCostIncrease;
                setHomeText = Text.join(
                        setHomeText,
                        Text.builder(" + ").color(TextColors.GRAY).build(),
                        Text.of(String.valueOf(setHomePriceIncrease)),
                        Text.builder(" = ").color(TextColors.GRAY).build(),
                        Text.of(String.valueOf(setClanhomeCost + setHomePriceIncrease))
                );
            }
            clanCreationText = Text.of(String.valueOf(Config.getDouble(Config.CLAN_CREATION_COST)));
            teleportToClanHomeText = Text.of(String.valueOf(Config.getDouble(Config.TELEPORT_COST)));
        }

        VerticalTable table = new VerticalTable(" Clan price info", 0);
        table.setValue("Clan creation", clanCreationText);
        table.setValue("Teleport to clan home", teleportToClanHomeText);
        table.setValue("Set clan home", setHomeText);

        table.draw(commandSource, 0);
    }
}
