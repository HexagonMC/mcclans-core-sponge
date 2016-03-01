package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.format.TextColor;

/**
 * Created by K.Volkers on 1-3-2016.
 */
public class ClanTagCommands {

    @Command(name = "color", description = "Change the clan tag color", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.tag, spongePermission = "mcclans.user.tag.color")
    public void tagColorCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter TextColor textColor) {
        ClanImpl clan = clanPlayer.getClan();
        clan.setTagColor(textColor);
        Messages.sendSuccessfullyChangedTheClanTagColorTo(commandSource, clan.getTagColored());
    }

}
