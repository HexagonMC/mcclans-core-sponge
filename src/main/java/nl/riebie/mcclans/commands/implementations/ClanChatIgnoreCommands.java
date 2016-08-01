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

import nl.riebie.mcclans.commands.Toggle;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by Kippers on 31/07/2016.
 */
public class ClanChatIgnoreCommands {

    @Command(name = "clan", description = "Ignore clan chat", isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.chat.ignore.clan")
    public void clanChatCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "toggle") Toggle toggle) {
        boolean ignore = toggle.getBoolean(clanPlayer.getIgnoreClanChat());
        clanPlayer.setIgnoreClanChat(ignore);
        if (ignore) {
            Messages.sendBasicMessage(commandSource, Messages.YOU_ARE_NOW_IGNORING_CLAN_CHAT);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.YOU_HAVE_STOPPED_IGNORING_CLAN_CHAT);
        }
    }

    @Command(name = "ally", description = "Ignore ally chat", isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.chat.ignore.ally")
    public void allyChatCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Parameter(name = "toggle") Toggle toggle) {
        boolean ignore = toggle.getBoolean(clanPlayer.getIgnoreAllyChat());
        clanPlayer.setIgnoreAllyChat(ignore);
        if (ignore) {
            Messages.sendBasicMessage(commandSource, Messages.YOU_ARE_NOW_IGNORING_ALLY_CHAT);
        } else {
            Messages.sendBasicMessage(commandSource, Messages.YOU_HAVE_STOPPED_IGNORING_ALLY_CHAT);
        }
    }
}
