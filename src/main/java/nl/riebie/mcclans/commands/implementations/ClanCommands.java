package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.annotations.ChildGroup;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.PageParameter;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.commands.constraints.length.ClanNameLengthConstraint;
import nl.riebie.mcclans.commands.constraints.length.ClanTagLengthConstraint;
import nl.riebie.mcclans.commands.constraints.regex.ClanNameRegexConstraint;
import nl.riebie.mcclans.commands.constraints.regex.ClanTagRegexConstraint;
import nl.riebie.mcclans.comparators.ClanKdrComparator;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.table.HorizontalTable;
import org.spongepowered.api.text.Text;

import java.util.List;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ClanCommands {
    private final static String CLAN_CREATE_DESCRIPTION = "Create a clan";

    @Command(name = "create", description = CLAN_CREATE_DESCRIPTION)
    public void clanCreateCommand(
            ClanPlayerImpl clanPlayer,
            @Parameter(length = ClanTagLengthConstraint.class, regex = ClanTagRegexConstraint.class) String clanTag,
            @Parameter(length = ClanNameLengthConstraint.class, regex = ClanNameRegexConstraint.class) String clanName) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        if (clansImpl.tagIsFree(clanTag)) {
            ClanImpl clanImpl = clansImpl.createClan(clanTag, clanName, clanPlayer);
            Messages.sendBroadcastMessageClanCreatedBy(clanImpl.getName(), clanImpl.getTagColored(), clanPlayer.getName());
        }
    }

    @Command(name = "list")
    public void clanChatCommand(ClanPlayerImpl clanPlayer, @PageParameter int page) {
        List<ClanImpl> clans = ClansImpl.getInstance().getClanImpls();

        HorizontalTable<ClanImpl> table = new HorizontalTable<>("Clans", 10, (row, clan, i) -> {
            row.setValue("Rank", Text.of(i + 1));
            row.setValue("Clan", Text.join(Text.builder().color(clan.getTagColor()).append(Text.of(clan.getTag())).build(), Text.of(" ", clan.getName())));
            row.setValue("KDR",Text.of(clan.getKDR()));
            row.setValue("Members", Text.of(clan.getMemberCount()));

        });
        table.defineColumn("Rank", 10);
        table.defineColumn("Clan", 40, true);
        table.defineColumn("KDR", 15);
        table.defineColumn("Members", 15);

        table.setComparator(new ClanKdrComparator());

        table.draw(clans, page, clanPlayer);
    }

    @ChildGroup(ClanChatCommands.class)
    @Command(name = "chat")
    public void clanChatRootCommand(ClanPlayerImpl clanPlayer){
        clanPlayer.sendMessage(Text.of("NIET DIT ROOTCOMMAND AANROEPEN,FLIKKER"));
    }

}
