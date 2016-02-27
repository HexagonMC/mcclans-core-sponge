package nl.riebie.mcclans.commands.implementations.tables;

import java.util.ArrayList;
import java.util.List;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.Row;
import nl.riebie.mcclans.table.TableAdapter;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class RankViewTable {
    int page;
    CommandSource sender;
    List<RankImpl> ranks = new ArrayList<RankImpl>();
    RankImpl rank;
    boolean single;

    public RankViewTable(int page, CommandSource sender, List<RankImpl> ranks) {
        this.page = page;
        this.sender = sender;
        this.ranks = ranks;
        this.single = false;
    }

    public RankViewTable(int page, CommandSource sender, RankImpl rank) {
        this.page = page;
        this.sender = sender;
        this.ranks.add(rank);
        this.rank = rank;
        this.single = true;
    }

    public void print() {
        String title = single ? "Clan rank " + rank.getName() : "Clan ranks";

        HorizontalTable<RankImpl> table = new HorizontalTable<RankImpl>(title, 10, new TableAdapter<RankImpl>() {

            @Override
            public void fillRow(Row row, RankImpl rank, int index) {
                Text.Builder perms = Text.builder();
                int i = 0;
                for (Permission perm : rank.getPermissions()) {
                    if (i != 0) {
                        perms.append(Text.of(", "));
                    }
                    perms.append(Text.builder().color(TextColors.GRAY).append(Text.of(perm.name())).build());
                    i++;
                }
                row.setValue("Rank", Text.of(rank.getName()));
                row.setValue("Permissions", perms.build());
            }
        });

        table.defineColumn("Rank", 18);
        table.defineColumn("Permissions", 30);

        table.draw(ranks, page, sender);
    }
}