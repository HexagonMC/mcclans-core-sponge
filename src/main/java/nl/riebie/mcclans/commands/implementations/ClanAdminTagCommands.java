package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.format.TextColor;

/**
 * Created by K.Volkers on 1-3-2016.
 */
public class ClanAdminTagCommands {

    @Command(name = "color", description = "Change a clan tag color", spongePermission = "mcclans.admin.tag.color")
    public void tagColorCommand(CommandSource commandSource, @Parameter(name = "clanTag") String clanTag, @Parameter(name = "tagColor") TextColor tagColor) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(clanTag);

        if (clan == null) {
            Messages.sendWarningMessage(commandSource, Messages.CLAN_DOES_NOT_EXIST);
        } else {
            clan.setTagColor(tagColor);
            Messages.sendSuccessfullyChangedTheClanTagColorTo(commandSource, clan.getTagColored());
        }
    }
}
