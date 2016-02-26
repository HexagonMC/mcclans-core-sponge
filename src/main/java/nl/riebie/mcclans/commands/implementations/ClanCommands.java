package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.Toggle;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.constraints.length.LengthConstraints;
import nl.riebie.mcclans.commands.constraints.regex.RegexConstraints;
import nl.riebie.mcclans.comparators.ClanKdrComparator;
import nl.riebie.mcclans.comparators.ClanPlayerKdrComparator;
import nl.riebie.mcclans.comparators.MemberComparator;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanInvite;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.Row;
import nl.riebie.mcclans.table.TableAdapter;
import nl.riebie.mcclans.table.VerticalTable;
import nl.riebie.mcclans.utils.UUIDUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ClanCommands {
    private final static String CLAN_CREATE_DESCRIPTION = "Create a clan";
    private static final String CLAN_FRIENDLY_FIRE_DESCRIPTION = "";

    @ChildGroup(ClanFriendlyFireCommands.class)
    @Command(name = "friendlyfire", isPlayerOnly = true, permission = Permission.friendlyfire, description = CLAN_FRIENDLY_FIRE_DESCRIPTION)
    public void clanFriendlyFireCommand(ClanPlayerImpl clanPlayer, @Parameter Toggle friendlyFireToggle) {

    }

    @Command(name = "test")
    public void clanTestCommand(ClanPlayerImpl clanPlayer, @Multiline(listType = Permission.class) @Parameter List<Permission> test) {
        String message = "";
        for (Permission permission : test) {
            message += permission.toString();
        }
        clanPlayer.sendMessage(Text.of(message));
    }

    @Command(name = "hoi")
    public void clanHoiCommand(ClanPlayerImpl clanPlayer, @Multiline @Parameter String test) {
        clanPlayer.sendMessage(Text.of(test));
    }

    @Command(name = "optlist")
    public void clanOptListCommand(ClanPlayerImpl clanPlayer, @Multiline(listType = Permission.class) @OptionalParameter(List.class) Optional<List<Permission>> test) {
        if (test.isPresent()) {
            String message = "";
            for (Permission permission : test.get()) {
                message += permission.toString();
            }
            clanPlayer.sendMessage(Text.of(message));
        } else {
            clanPlayer.sendMessage(Text.of("leeg"));
        }
    }

    @Command(name = "opt")
    public void clanOptCommand(ClanPlayerImpl clanPlayer, @Multiline @OptionalParameter(String.class) Optional<String> test) {
        if (test.isPresent()) {
            clanPlayer.sendMessage(Text.of(test.get()));
        } else {
            clanPlayer.sendMessage(Text.of("leeg"));
        }
    }

    @Command(name = "create", description = CLAN_CREATE_DESCRIPTION)
    public void clanCreateCommand(
            ClanPlayerImpl clanPlayer,
            @Parameter(length = LengthConstraints.CLAN_TAG, regex = RegexConstraints.CLAN_TAG) String clanTag,
            @Multiline @Parameter(length = LengthConstraints.CLAN_NAME, regex = RegexConstraints.CLAN_NAME) String clanName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        if (clansImpl.tagIsFree(clanTag)) {
            ClanImpl clanImpl = clansImpl.createClan(clanTag, clanName, clanPlayer);
            Messages.sendBroadcastMessageClanCreatedBy(clanImpl.getName(), clanImpl.getTagColored(), clanPlayer.getName());
        }
    }

    @Command(name = "list")
    public void clanListCommand(ClanPlayerImpl clanPlayer, @PageParameter int page) {
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

        table.draw(clans, page, clanPlayer);
    }

    @Command(name = "invite")
    public void clanInviteCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter String playerName) {
        ClanImpl clan = clanPlayer.getClan();
        Player player = (Player) commandSource;       //TODO add check if it is a player
        if (clan != null) {
            UUID uuid = UUIDUtils.getUUID(playerName);
            if (uuid == null) {
                Messages.sendPlayerNotOnline(player, playerName);
                return;
            }

            ClansImpl clansInstance = ClansImpl.getInstance();
            ClanPlayerImpl invitedClanPlayer = clansInstance.getClanPlayer(uuid);
            Player invitedPlayer = Sponge.getServer().getPlayer(uuid).get();  //handle optional :)
            if (invitedClanPlayer == null) {
                if (invitedPlayer == null) {
                    Messages.sendPlayerNotOnline(invitedPlayer, playerName);
                    return;
                }
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
                Messages.sendClanBroadcastMessagePlayerInvitedToTheClan(clan, invitedClanPlayerName, player.getName(), Permission.invite);
                if (invitedPlayer != null && invitedPlayer.isOnline()) {
                    Messages.sendInvitedToClan(invitedPlayer, clan.getName(), clan.getTagColored());
                }
            }
        } else {
            Messages.sendWarningMessage(player, Messages.YOU_ARE_NOT_IN_A_CLAN);
        }
    }

    @ChildGroup(ClanChatCommands.class)
    @Command(name = "chat")
    public void clanChatRootCommand(ClanPlayerImpl clanPlayer) {
        clanPlayer.sendMessage(Text.of("TODO"));
    }

    @Command(name = "disband")
    public void clanDisbandCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clanPlayer.getClan();
        Messages.sendBroadcastMessageClanDisbandedBy(clan.getName(), clan.getTagColored(), commandSource.getName());
        clansImpl.disbandClan(clan.getTag());
    }

    @Command(name = "remove")
    public void clanRemoveCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter String playerName) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan != null) {
            ClanPlayerImpl toBeRemovedClanPlayer = clan.getMember(playerName);
            if (toBeRemovedClanPlayer != null) {
                if (playerName.equalsIgnoreCase(clanPlayer.getName())) {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_REMOVE_YOURSELF_FROM_THE_CLAN);
                } else if (playerName.equalsIgnoreCase(clan.getOwner().getName())) {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_CANNOT_REMOVE_THE_OWNER_FROM_THE_CLAN);
                } else {
                    clan.removeMember(toBeRemovedClanPlayer.getName());
                    Messages.sendClanBroadcastMessagePlayerRemovedFromTheClanBy(clan, toBeRemovedClanPlayer.getName(), clanPlayer.getName());
                    Messages.sendYouHaveBeenRemovedFromClan(toBeRemovedClanPlayer, clan.getName());
                }
            } else {
                Messages.sendPlayerNotAMemberOfThisClan(commandSource, playerName);
            }
        } else {
            Messages.sendWarningMessage(commandSource, Messages.YOU_ARE_NOT_IN_A_CLAN);
        }
    }

    @Command(name = "accept")
    public void clanAcceptCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanInvite clanInvite = clanPlayer.getClanInvite();
        if (clanInvite == null) {
            Messages.sendWarningMessage(commandSource, Messages.NO_PENDING_CLAN_INVITE);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.CLAN_INVITE_ACCEPTED);
            clanInvite.accept();
        }
    }

    @Command(name = "decline")
    public void clanDeclineCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer) {
        ClanInvite clanInvite = clanPlayer.getClanInvite();
        if (clanInvite == null) {
            Messages.sendWarningMessage(commandSource, Messages.NO_PENDING_CLAN_INVITE);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.CLAN_INVITE_DECLINED);
            clanInvite.decline();
        }
    }

    @Command(name = "roster")
    public void clanRosterCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @OptionalParameter(String.class) Optional<String> clanTagOpt, @PageParameter int page) {
        ClanImpl clan;
        if (clanTagOpt.isPresent()) {
            String clanTag = clanTagOpt.get();
            clan = ClansImpl.getInstance().getClan(clanTag.toLowerCase());
            if (clan != null) {
                printRoster(clanPlayer, clan, page);
            } else {
                Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
            }
        } else {
            if (commandSource instanceof Player) {
                clan = clanPlayer.getClan();
                if (clan != null) {
                    printRoster(clanPlayer, clan, page);
                } else {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_ARE_NOT_IN_A_CLAN);
                }
            } else {
                Messages.sendWarningMessage(commandSource, Messages.YOU_NEED_TO_BE_A_PLAYER_TO_PERFORM_THIS_COMMAND);
            }
        }
    }

    private void printRoster(ClanPlayerImpl clanPlayer, ClanImpl clan, int page) {
        List<ClanPlayerImpl> members = clan.getMembersImpl();
        java.util.Collections.sort(members, new MemberComparator());

        HorizontalTable<ClanPlayerImpl> table = new HorizontalTable<ClanPlayerImpl>("Clan roster " + clan.getName(), 10,
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

        table.draw(members, page, clanPlayer);
    }

    @Command(name = "leaderboard")
    public void clanLeaderboardCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @PageParameter int page) {
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
                row.setValue("KDR", Text.of(String.valueOf(clanPlayer.getKDR())));

            }
        });
        table.defineColumn("Rank", 10);
        table.defineColumn("Name", 40);
        table.defineColumn("Clan", 15);
        table.defineColumn("KDR", 15);

        table.setComparator(new ClanPlayerKdrComparator());

        table.draw(clanPlayers, page, clanPlayer);
    }

    @Command(name = "info")
    public void clanInfoCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @OptionalParameter(String.class) Optional<String> clanTagOpt, @PageParameter int page) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        if (clanTagOpt.isPresent()) {
            String clanTag = clanTagOpt.get();
            ClanImpl clan = clansImpl.getClan(clanTag);
            if (clan != null) {
                printInfo(clanPlayer, clan);
            } else {
                Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
            }
        } else {
            if (commandSource instanceof Player) {
                ClanImpl clan = clanPlayer.getClan();
                if (clan != null) {
                    printInfo(clanPlayer, clan);
                } else {
                    Messages.sendWarningMessage(commandSource, Messages.YOU_ARE_NOT_IN_A_CLAN);
                }
            } else {
                Messages.sendWarningMessage(commandSource, Messages.YOU_NEED_TO_BE_A_PLAYER_TO_PERFORM_THIS_COMMAND);
            }
        }
    }

    private void printInfo(ClanPlayerImpl clanPlayer, ClanImpl clan) {
        VerticalTable table = new VerticalTable(" Clan info " + clan.getTag(), 0);
        table.setValue("Clan", Text.join(clan.getTagColored(), Text.of(" " + clan.getName())));
        table.setValue("Owner", Text.of(clan.getOwner().getName()));
        table.setValue("Members", Text.of(String.valueOf(clan.getMembers().size())));
        table.setValue("Allies", generateAllyList(clan));
        table.setValue("Kills", formatKdr(clan.getKills(), clan.getKillsHigh(), clan.getKillsMedium(), clan.getKillsLow()));
        table.setValue("Deaths", formatKdr(clan.getDeaths(), clan.getDeathsHigh(), clan.getDeathsMedium(), clan.getDeathsLow()));
        table.setValue("KDR", Text.of(String.valueOf(clan.getKDR())));
        table.setValue("Created", Text.of(clan.getCreationDateUserFriendly()));
        table.draw(clanPlayer, 0);
    }

    // TODO make more efficient
    private Text generateAllyList(ClanImpl clan) {
        Text allyList = null;
        for (ClanImpl ally : clan.getAlliesImpl()) {
            if (allyList == null) {
                allyList = Text.of();
            } else {
                allyList.toBuilder().append(Text.of(", ")).build();
            }
            allyList.toBuilder().append(ally.getTagColored()).build();
        }
        if (allyList == null) {
            allyList = Text.builder("None").color(TextColors.GRAY).build();
        }
        return allyList;
    }

    // TODO move to utils?
    private Text formatKdr(int total, int high, int medium, int low) {
        return Text.join(
                Text.of(String.valueOf(total)),
                Text.builder(" [").color(TextColors.GRAY).build(),
                Text.of(String.valueOf(high)),
                Text.builder(" : ").color(TextColors.GRAY).build(),
                Text.of(String.valueOf(medium)),
                Text.builder(" : ").color(TextColors.GRAY).build(),
                Text.of(String.valueOf(low)),
                Text.builder("]").color(TextColors.GRAY).build()
        );
    }
}
