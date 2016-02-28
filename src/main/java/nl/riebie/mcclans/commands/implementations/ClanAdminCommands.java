package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.PageParameter;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.comparators.MemberComparator;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.Row;
import nl.riebie.mcclans.table.TableAdapter;
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

/**
 * Created by Mirko on 28/02/2016.
 */
public class ClanAdminCommands {

    @Command(name = "coords", spongePermission = "mcclans.admin.coords")
    public void clanAdminCoordsCommand(CommandSource commandSource, @Parameter ClanImpl clan, @PageParameter int page) {
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


}
