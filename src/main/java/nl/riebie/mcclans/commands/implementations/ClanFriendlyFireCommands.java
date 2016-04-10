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

import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.Toggle;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by riebie on 26/02/2016.
 */
public class ClanFriendlyFireCommands {

    private final static String CLAN_FRIENDLY_FIRE_DESCRIPTION = "Toggle the clan's friendly fire protection";
    private final static String PLAYER_FRIENDLY_FIRE_DESCRIPTION = "Toggle your personal friendly fire protection";

    @Command(name = "clan", description = CLAN_FRIENDLY_FIRE_DESCRIPTION, isPlayerOnly = true, isClanOnly = true, clanPermission = "friendlyfire", spongePermission = "mcclans.user.friendlyfire.clan")
    public void clanFriendlyFireCommand(ClanPlayerImpl clanPlayer, @Parameter(name = "toggle") Toggle friendlyFireToggle) {
        ClanImpl clan = clanPlayer.getClan();
        boolean ffProtected = friendlyFireToggle.getBoolean(clan.isFfProtected());
        clan.setFfProtection(ffProtected);
        if (ffProtected) {
            Messages.sendClanBroadcastMessageClanFriendlyFireProtectionHasBeenActivatedByPlayer(clan, clanPlayer.getName());
        } else {
            Messages.sendClanBroadcastMessageClanFriendlyFireProtectionHasBeenDeactivatedByPlayer(clan, clanPlayer.getName());
        }
    }

    @Command(name = "personal", description = PLAYER_FRIENDLY_FIRE_DESCRIPTION, isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.friendlyfire.personal")
    public void personalFriendlyFireCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "toggle") Toggle friendlyFireToggle) {
        boolean ffProtected = friendlyFireToggle.getBoolean(clanPlayer.isFfProtected());
        clanPlayer.setFfProtection(ffProtected);
        if (ffProtected) {
            Messages.sendBasicMessage(sender, Messages.ACTIVATED_PERSONAL_FRIENDLY_FIRE_PROTECTION);
        } else {
            Messages.sendBasicMessage(sender, Messages.DEACTIVATED_PERSONAL_FRIENDLY_FIRE_PROTECTION);
        }
    }
}
