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

import nl.riebie.mcclans.api.enums.PermissionModifyResponse;
import nl.riebie.mcclans.api.permissions.ClanPermission;
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
 * Created by riebie on 27/02/2016.
 */
public class ClanRankCommands {

    @ChildGroup(ClanRankPermissionCommands.class)
    @Command(name = "permission", description = "Top command for all rank permission commands", spongePermission = "mcclans.user.rank.permission.helppage")
    public void clanRankPermissionRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @Command(name = "create", description = "Create a rank", isPlayerOnly = true, isClanOnly = true, clanPermission = "rank", spongePermission = "mcclans.user.rank.create")
    public void clanRankCreateCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "rankName") String rankName,
                                      @Parameter(name = "permissions") Optional<List<ClanPermission>> permissions) {
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
                for (ClanPermission permission : permissions.get()) {
                    PermissionModifyResponse response = rank.addPermission(permission.getName());
                    switch (response) {
                        case ALREADY_CONTAINS_PERMISSION:
                            Messages.sendAddingPermissionFailedRankAlreadyHasThisPermission(sender, permission.getName());
                            break;
                        case SUCCESSFULLY_MODIFIED:
                            Messages.sendSuccessfullyAddedThisPermission(sender, permission.getName());
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

    @Command(name = "rename", description = "Rename a rank", isPlayerOnly = true, isClanOnly = true, clanPermission = "rank", spongePermission = "mcclans.user.rank.rename")
    public void clanRankRenameCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "oldRankName") String rankName,
                                      @Parameter(name = "newRankName") String newRankName) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan.containsRank(newRankName)) {
            Messages.sendRankExistsAlready(sender, newRankName);
        } else if (clan.containsRank(rankName)) {
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

    @Command(name = "remove", description = "Remove a rank", isPlayerOnly = true, isClanOnly = true, clanPermission = "rank", spongePermission = "mcclans.user.rank.remove")
    public void clanRankRemoveCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "rankName") String rankName) {
        if (clanPlayer.getClan().containsRank(rankName)) {
            ClanImpl clan = clanPlayer.getClan();
            RankImpl rank = clan.getRank(rankName);
            if (rank.isChangeable()) {
                for (ClanPlayerImpl member : clan.getMembersImpl()) {
                    if (member.getRank().getName().toLowerCase().equals(rankName.toLowerCase())) {
                        member.setRankInternal(clan.getRank(RankFactory.getRecruitIdentifier()));
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

    @Command(name = "view", description = "View the properties of the rank or all ranks", isPlayerOnly = true, isClanOnly = true, clanPermission = "rank", spongePermission = "mcclans.user.rank.view")
    public void clanRankViewCommand(CommandSource sender, ClanPlayerImpl clanPlayer,
                                    @Parameter(name = "rankName") Optional<String> rankName, @PageParameter int page) {
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
