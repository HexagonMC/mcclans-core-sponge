package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.api.enums.PermissionModifyResponse;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.commands.annotations.*;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.Row;
import nl.riebie.mcclans.table.TableAdapter;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * Created by Mirko on 27/02/2016.
 */
public class ClanRankPermissionCommands {

    @Command(name = "add", permission = Permission.rank, description = "Adds the given permissions to a rank")
    public void clanRankPermissionAddCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter String rankName,
                                             @Multiline(listType = Permission.class) @Parameter List<Permission> permissions) {

        ClanImpl clan = clanPlayer.getClan();
        if (clan == null) {
            Messages.sendWarningMessage(sender, Messages.YOU_ARE_NOT_IN_A_CLAN);
            return;
        }
        RankImpl rank = clan.getRank(rankName);
        if (rank == null) {
            Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
            return;
        }
        if (!rank.isChangeable()) {
            Messages.sendWarningMessage(sender, Messages.RANK_IS_NOT_CHANGEABLE);
            return;
        }

        Messages.sendRankSuccessfullyModified(sender, rankName);
        for (Permission permission : permissions) {
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

    @Command(name = "set", permission = Permission.rank, description = "Sets the given permissions to a rank")
    public void clanRankPermissionSetCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter String rankName,
                                             @Multiline(listType = Permission.class) @Parameter List<Permission> permissions) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan == null) {
            Messages.sendWarningMessage(sender, Messages.YOU_ARE_NOT_IN_A_CLAN);
            return;
        }
        RankImpl rank = clan.getRank(rankName);
        if (rank == null) {
            Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
            return;
        }
        if (!rank.isChangeable()) {
            Messages.sendWarningMessage(sender, Messages.RANK_IS_NOT_CHANGEABLE);
            return;
        }

        Messages.sendRankSuccessfullyModified(sender, rankName);

        List<Permission> oldPermissions = rank.getPermissions();

        for (Permission permission : oldPermissions) {
            rank.removePermission(permission.name());
        }


        for (Permission permission : permissions) {
            rank.addPermission(permission);
            Messages.sendSuccessfullySetThisPermission(sender, permission.name());
        }
    }

    @Command(name = "remove", isPlayerOnly = true, description = "Removes the given permissions from a rank")
    public void canPermissionRemoveCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter String rankName,
                                           @Multiline(listType = Permission.class) @Parameter List<Permission> permissions) {
        ClanImpl clan = clanPlayer.getClan();
        if (clan == null) {
            Messages.sendWarningMessage(sender, Messages.YOU_ARE_NOT_IN_A_CLAN);
            return;
        }
        RankImpl rank = clan.getRank(rankName);
        if (rank == null) {
            Messages.sendWarningMessage(sender, Messages.RANK_DOES_NOT_EXIST);
            return;
        }
        if (!rank.isChangeable()) {
            Messages.sendWarningMessage(sender, Messages.RANK_IS_NOT_CHANGEABLE);
            return;
        }

        Messages.sendRankSuccessfullyModified(sender, rankName);

        for (Permission permission : permissions) {
            PermissionModifyResponse response = rank.removePermission(permission);

            switch (response) {
                case DOES_NOT_CONTAIN_PERMISSION:
                    Messages.sendRemovingPermissionFailedRankDoesNotHaveThisPermission(sender, permission.name());
                    break;
                case SUCCESSFULLY_MODIFIED:
                    Messages.sendSuccessfullyRemovedThisPermission(sender, permission.name());
                    break;
                default:
                    break;
            }
        }
    }

    @Command(name = "view", description = "View all available permissions")
    public void clanPermissonViewCommand(CommandSource sender, @PageParameter int page) {
        HorizontalTable<Permission> table = new HorizontalTable<>("Permissions", 10,
                (TableAdapter<Permission>) (row, permission, index) -> {
                    row.setValue("Permission", Text.of(permission.name()));
                    row.setValue("Description", Text.of(permission.getDescription()));
                });
        table.defineColumn("Permission", 20);
        table.defineColumn("Description", 20);

        List<Permission> permissions = Permission.getUsablePermissions();

        table.draw(permissions, page, sender);
    }
}
