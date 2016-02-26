package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.Toggle;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by Mirko on 26/02/2016.
 */
public class ClanFriendlyFireCommands {

    private final static String CLAN_FRIENDLY_FIRE_DESCRIPTION = "Toggle the clan's friendly fire protection";
    private final static String PLAYER_FRIENDLY_FIRE_DESCRIPTION = "Toggle your personal friendly fire protection";

    @Command(name = "clan", isPlayerOnly = true, permission = Permission.friendlyfire, description = CLAN_FRIENDLY_FIRE_DESCRIPTION)
    public void clanFriendlyFireCommand(ClanPlayerImpl clanPlayer, @Parameter Toggle friendlyFireToggle) {
        ClanImpl clan = clanPlayer.getClan();
        boolean ffProtected = friendlyFireToggle.getBoolean(clan.isFfProtected());
        clan.setFfProtection(ffProtected);
        if (ffProtected) {
            Messages.sendClanBroadcastMessageClanFriendlyFireProtectionHasBeenDeactivatedByPlayer(clan, clanPlayer.getName());
        } else {
            Messages.sendClanBroadcastMessageClanFriendlyFireProtectionHasBeenActivatedByPlayer(clan, clanPlayer.getName());
        }
    }

    @Command(name = "personal", isPlayerOnly = true, permission = Permission.friendlyfire, description = CLAN_FRIENDLY_FIRE_DESCRIPTION)
    public void personalFriendlyFireCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter Toggle friendlyFireToggle) {
        boolean ffProtected = friendlyFireToggle.getBoolean(clanPlayer.isFfProtected());
        clanPlayer.setFfProtection(ffProtected);
        if (ffProtected) {
            Messages.sendBasicMessage(sender, Messages.DEACTIVATED_PERSONAL_FRIENDLY_FIRE_PROTECTION);
        } else {
            Messages.sendBasicMessage(sender, Messages.ACTIVATED_PERSONAL_FRIENDLY_FIRE_PROTECTION);
        }
    }
}
