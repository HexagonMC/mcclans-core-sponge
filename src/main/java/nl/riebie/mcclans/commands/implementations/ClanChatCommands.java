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

import com.google.common.collect.ImmutableMap;
import nl.riebie.mcclans.channels.AllyMessageChannelImpl;
import nl.riebie.mcclans.channels.ClanMessageChannelImpl;
import nl.riebie.mcclans.commands.annotations.ChildGroup;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Multiline;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.enums.PlayerChatState;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;

import java.util.Optional;

/**
 * Created by riebie on 14/02/2016.
 */
public class ClanChatCommands {

    private final TextTemplate chatTemplate = TextTemplate.of(TextTemplate.arg(MessageEvent.PARAM_MESSAGE_HEADER).build(),
            TextTemplate.arg(MessageEvent.PARAM_MESSAGE_BODY).build(), TextTemplate.arg(MessageEvent.PARAM_MESSAGE_FOOTER).build());

    @ChildGroup(ClanChatIgnoreCommands.class)
    @Command(name = "ignore", description = "Top command for all chat ignore commands", spongePermission = "mcclans.user.chat.ignore.helppage")
    public void clanChatIgnoreRootCommand(CommandSource commandSource) {
        commandSource.sendMessage(Text.of("TODO"));
    }

    @Command(name = "clan", description = "Talk in clan chat", isPlayerOnly = true, isClanOnly = true, clanPermission = "clanchat", spongePermission = "mcclans.user.chat.clan")
    public void clanChatCommand(Player player, ClanPlayerImpl clanPlayer, @Multiline @Parameter(name = "message") Optional<String> messageOpt) {
        if (messageOpt.isPresent()) {
            String message = messageOpt.get();
            if (clanPlayer.getTempChatState() == null) {
                if (clanPlayer.getIgnoreClanChat()) {
                    Messages.sendYouNeedToUnignoreClanChatBeforeTalking(player);
                } else {
                    clanPlayer.setTempChatState(PlayerChatState.CLAN);
                    ClanMessageChannelImpl.getFor(clanPlayer).send(player, Text.of(message));
                    clanPlayer.setTempChatState(null);
                }
            }
        } else {
            PlayerChatState chatState = clanPlayer.getChatState();
            if (chatState.equals(PlayerChatState.CLAN)) {
                clanPlayer.setChatState(PlayerChatState.GLOBAL);
                Messages.sendNowTalkingInGlobal(player);
            } else {
                clanPlayer.setChatState(PlayerChatState.CLAN);
                Messages.sendNowTalkingInClanChat(player);
            }
        }
    }

    @Command(name = "ally", description = "Talk in ally chat", isPlayerOnly = true, isClanOnly = true, clanPermission = "allychat", spongePermission = "mcclans.user.chat.ally")
    public void allyChatCommand(Player player, ClanPlayerImpl clanPlayer, @Multiline @Parameter(name = "message") Optional<String> optionalMessage) {
        if (optionalMessage.isPresent()) {
            String message = optionalMessage.get();
            if (clanPlayer.getTempChatState() == null) {
                if (clanPlayer.getIgnoreAllyChat()) {
                    Messages.sendYouNeedToUnignoreAllyChatBeforeTalking(player);
                } else {
                    clanPlayer.setTempChatState(PlayerChatState.ALLY);
                    AllyMessageChannelImpl.getFor(clanPlayer).send(player, Text.of(message));
                    clanPlayer.setTempChatState(null);
                }
            }
        } else {
            PlayerChatState chatState = clanPlayer.getChatState();
            if (chatState.equals(PlayerChatState.ALLY)) {
                clanPlayer.setChatState(PlayerChatState.GLOBAL);
                Messages.sendNowTalkingInGlobal(player);
            } else {
                clanPlayer.setChatState(PlayerChatState.ALLY);
                Messages.sendNowTalkingInAllyChat(player);
            }
        }
    }

    @Command(name = "global", description = "Talk in global chat", isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.chat.global")
    public void globalChatCommand(Player player, ClanPlayerImpl clanPlayer, @Multiline @Parameter(name = "message") Optional<String> optionalMessage) {
        if (optionalMessage.isPresent()) {
            String message = optionalMessage.get();
            if (clanPlayer.getTempChatState() == null) {
                clanPlayer.setTempChatState(PlayerChatState.GLOBAL);
                sendChat(player, Text.of(message));
            }
        } else {
            clanPlayer.setChatState(PlayerChatState.GLOBAL);
            Messages.sendNowTalkingInGlobal(player);
        }
    }

    private void sendChat(Player player, Text message) {
        Text rawMessage = Text.of(message);
        MessageChannelEvent.Chat event = SpongeEventFactory.createMessageChannelEventChat(
                Cause.source(player).named(NamedCause.notifier(player)).build(),
                player.getMessageChannel(),
                Optional.of(player.getMessageChannel()),
                new MessageEvent.MessageFormatter(
                        Text.builder(player.getName()).build(), rawMessage
                ),
                rawMessage,
                false
        );
        if (!Sponge.getEventManager().post(event)) {
            MessageEvent.MessageFormatter formatter = event.getFormatter();
            player.getMessageChannel().send(player, chatTemplate.apply(
                    ImmutableMap.of(MessageEvent.PARAM_MESSAGE_HEADER, formatter.getHeader(),
                            MessageEvent.PARAM_MESSAGE_BODY, formatter.getBody(),
                            MessageEvent.PARAM_MESSAGE_FOOTER, formatter.getFooter())).build());
        }
    }
}
