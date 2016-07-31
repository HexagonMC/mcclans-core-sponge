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

import nl.riebie.mcclans.channels.AllyMessageChannel;
import nl.riebie.mcclans.channels.ClanMessageChannel;
import nl.riebie.mcclans.commands.annotations.ChildGroup;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Multiline;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.enums.PlayerChatState;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.Optional;

/**
 * Created by riebie on 14/02/2016.
 */
public class ClanChatCommands {

    @ChildGroup(ClanChatIgnoreCommands.class)
    @Command(name = "ignore", description = "Top command for all chat ignore commands", spongePermission = "mcclans.user.chat.ignore.helppage")
    public void clanChatIgnoreRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @Command(name = "clan", description = "Talk in clan chat", isPlayerOnly = true, isClanOnly = true, clanPermission = "clanchat", spongePermission = "mcclans.user.chat.clan")
    public void clanChatCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Multiline @Parameter(name = "message") Optional<String> messageOpt) {
        if (messageOpt.isPresent()) {
            String message = messageOpt.get();
            if (clanPlayer.getTempChatState() == null) {
                clanPlayer.setTempChatState(PlayerChatState.CLAN);
                ClanMessageChannel.getFor(clanPlayer).send(commandSource, Text.of(message));
                clanPlayer.setTempChatState(null);
            }
        } else {
            PlayerChatState chatState = clanPlayer.getChatState();
            if (chatState.equals(PlayerChatState.CLAN)) {
                clanPlayer.setChatState(PlayerChatState.GLOBAL);
                Messages.sendNowTalkingInGlobal(commandSource);
            } else {
                clanPlayer.setChatState(PlayerChatState.CLAN);
                Messages.sendNowTalkingInClanChat(commandSource);
            }
        }
    }

    @Command(name = "ally", description = "Talk in ally chat", isPlayerOnly = true, isClanOnly = true, clanPermission = "allychat", spongePermission = "mcclans.user.chat.ally")
    public void allyChatCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Multiline @Parameter(name = "message") Optional<String> optionalMessage) {
        if (optionalMessage.isPresent()) {
            String message = optionalMessage.get();
            if (clanPlayer.getTempChatState() == null) {
                clanPlayer.setTempChatState(PlayerChatState.ALLY);
                AllyMessageChannel.getFor(clanPlayer).send(commandSource, Text.of(message));
                clanPlayer.setTempChatState(null);
            }
        } else {
            PlayerChatState chatState = clanPlayer.getChatState();
            if (chatState.equals(PlayerChatState.ALLY)) {
                clanPlayer.setChatState(PlayerChatState.GLOBAL);
                Messages.sendNowTalkingInGlobal(commandSource);
            } else {
                clanPlayer.setChatState(PlayerChatState.ALLY);
                Messages.sendNowTalkingInAllyChat(commandSource);
            }
        }
    }

    @Command(name = "global", description = "Talk in global chat", isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.chat.global")
    public void globalChatCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Multiline @Parameter(name = "message") Optional<String> optionalMessage) {
        if (optionalMessage.isPresent()) {
            String message = optionalMessage.get();
            if (clanPlayer.getTempChatState() == null) {
                clanPlayer.setTempChatState(PlayerChatState.GLOBAL);
                MessageChannel messageChannel = Sponge.getServer().getBroadcastChannel();
                // TODO not fake out <name> message
                messageChannel.send(
                        commandSource,
                        Text.join(
                                (clanPlayer.getClan() == null) ? Text.of("") : Text.join(clanPlayer.getClan().getTagColored(), Text.of(" ")),
                                Text.of("<", commandSource.getName(), "> ", message)
                        )
                );
                clanPlayer.setTempChatState(null);
            }
        } else {
            clanPlayer.setChatState(PlayerChatState.GLOBAL);
            Messages.sendNowTalkingInGlobal(commandSource);
        }
    }
}
