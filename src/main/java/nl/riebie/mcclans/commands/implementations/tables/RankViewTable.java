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

package nl.riebie.mcclans.commands.implementations.tables;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.permissions.ClanPermission;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.table.Row;
import nl.riebie.mcclans.table.TableAdapter;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        HorizontalTable<RankImpl> table = new HorizontalTable<>(title, 10, new TableAdapter<RankImpl>() {

            @Override
            public void fillRow(Row row, RankImpl rank, int index) {
                Text.Builder perms = Text.builder();

                List<String> permissions;
                if (rank.getID() == RankFactory.getOwnerID()) {
                    permissions = new ArrayList<>();
                    permissions.addAll(ClansImpl.getInstance().getClanPermissionManager().getClanPermissions().stream().map(ClanPermission::getName).collect(Collectors.toList()));
                } else {
                    permissions = rank.getPermissions();
                }

                int i = 0;
                for (String perm : permissions) {
                    if (i != 0) {
                        perms.append(Text.of(", "));
                    }
                    if (ClansImpl.getInstance().getClanPermissionManager().isActiveClanPermission(perm)) {
                        perms.append(Text.builder().color(TextColors.GRAY).append(Text.of(perm)).build());
                    } else {
                        perms.append(Text.builder().color(TextColors.RED).append(Text.of(perm)).build());
                    }

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