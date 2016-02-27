package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.api.enums.PermissionModifyResponse;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.commands.implementations.tables.RankViewTable;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * Created by Mirko on 27/02/2016.
 */
public class ClanRankCommands {

    @ChildGroup(ClanRankPermissionCommands.class)
    @Command(name = "permission", description = "Top command for all rank permission commands", spongePermission = "mcclans.user.rank.permission.helppage")
    public void clanRankPermissionRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @Command(name = "create", description = "Create a rank", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.rank, spongePermission = "mcclans.user.rank.create")
    public void clanRankCreateCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter String rankName,
                                      @Multiline(listType = Permission.class) @OptionalParameter(List.class) Optional<List<Permission>> permissions) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan == null) {
            Messages.sendWarningMessage(sender, Messages.YOU_ARE_NOT_IN_A_CLAN);
            return;
        }
        if (!clan.containsRank(rankName)) {
            clanPlayer.getClan().addRank(rankName);
            Messages.sendRankSuccessfullyCreated(sender, rankName);
            if (permissions.isPresent()) {
                RankImpl rank = clanPlayer.getClan().getRank(rankName);
                for (Permission permission : permissions.get()) {
                    PermissionModifyResponse response = rank.addPermission(permission);
                    switch (response) {
                        case ALREADY_CONTAINS_PERMISSION:
                            Messages.sendAddingPermissionFailedRankAlreadyHasThisPermission(sender, permission.name());
                            break;
                        case SUCCESSFULLY_MODIFIED:
                            Messages.sendSuccessfullyAddedThisPermission(sender, permission.name());
                            break;
                        default:
                            break;
                    }
                }
            }
        } else {
            Messages.sendRankExistsAlready(sender, rankName);
        }
    }

    @Command(name = "rename", description = "Rename a rank", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.rank, spongePermission = "mcclans.user.rank.rename")
    public void clanRankRenameCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter String rankName,
                                      @Parameter String newRankName) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan.containsRank(rankName)) {
            RankImpl rank = clan.getRank(rankName);
            if (rank.isChangeable()) {
                clan.renameRank(rankName, newRankName);
                Messages.sendRankSuccessfullyRenamed(sender, rankName, newRankName);
            } else {
                Messages.sendWarningMessage(sender, Messages.RANK_IS_NOT_CHANGEABLE);
            }
        } else {
            Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
        }
    }

    @Command(name = "remove", description = "Remove a rank", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.rank, spongePermission = "mcclans.user.rank.remove")
    public void clanRankRemoveCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter String rankName) {
        if (clanPlayer.getClan().containsRank(rankName)) {
            ClanImpl clan = clanPlayer.getClan();
            RankImpl rank = clan.getRank(rankName);
            if (rank.isChangeable()) {
                for (ClanPlayerImpl member : clan.getMembersImpl()) {
                    if (member.getRank().getName().toLowerCase().equals(rankName.toLowerCase())) {
                        member.setRank(clan.getRank(RankFactory.getRecruitIdentifier()));
                    }
                }
                clan.removeRank(rankName);
                Messages.sendRankRemoved(sender, rankName);
            } else {
                Messages.sendWarningMessage(sender, Messages.RANK_IS_NOT_CHANGEABLE);
            }
        } else {
            Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
        }
    }

    @Command(name = "view", description = "View the properties of the rank or all ranks", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.rank, spongePermission = "mcclans.user.rank.view")
    public void clanRankViewCommand(CommandSource sender, ClanPlayerImpl clanPlayer,
                                    @OptionalParameter(String.class) Optional<String> rankName, @PageParameter int page) {
        if (rankName.isPresent()) {
            RankImpl rank = clanPlayer.getClan().getRank(rankName.get());
            if (rank == null) {
                Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
            } else {
                RankViewTable rankView = new RankViewTable(page, sender, rank);
                rankView.print();
            }
        } else {
            RankViewTable rankView = new RankViewTable(page, sender, clanPlayer.getClan().getRankImpls());
            rankView.print();
        }
    }
}
