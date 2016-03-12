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

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.messages.Messages;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.format.TextColor;

/**
 * Created by Kippers on 1-3-2016.
 */
public class ClanAdminTagCommands {

    @Command(name = "color", description = "Change a clan tag color", spongePermission = "mcclans.admin.tag.color")
    public void tagColorCommand(CommandSource commandSource, @Parameter(name = "clanTag") ClanImpl clan, @Parameter(name = "tagColor") TextColor tagColor) {
        clan.setTagColor(tagColor);
        Messages.sendSuccessfullyChangedTheClanTagColorTo(commandSource, clan.getTagColored());
    }
}
