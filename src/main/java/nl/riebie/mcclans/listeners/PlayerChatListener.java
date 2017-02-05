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

package nl.riebie.mcclans.listeners;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.channels.AllyMessageChannelImpl;
import nl.riebie.mcclans.channels.ClanMessageChannelImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.enums.PlayerChatState;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.Optional;

/**
 * Created by Kippers on 13/02/2016.
 */
public class PlayerChatListener {

    @Listener(order = Order.LATE)
    public void onMessageChannelChat(MessageChannelEvent.Chat event) {
        if (event.isCancelled()) {
            return;
        }

        Optional<Player> playerOpt = event.getCause().first(Player.class);
        if (!playerOpt.isPresent()) {
            return;
        }
        Player player = playerOpt.get();

        ClanPlayerImpl clanPlayer = ClansImpl.getInstance().getClanPlayer(player.getUniqueId());
        if (clanPlayer != null && clanPlayer.getClan() != null) {
            PlayerChatState chatState = clanPlayer.getChatState();
            PlayerChatState tempChatState = clanPlayer.getTempChatState();

            if (tempChatState == null) {
                handleChat(player, clanPlayer, chatState, event);
            } else {
                handleChat(player, clanPlayer, tempChatState, event);
                clanPlayer.setTempChatState(null);
            }
        }
    }

    private void handleChat(Player player, ClanPlayerImpl clanPlayer, PlayerChatState chatState, MessageChannelEvent.Chat event) {
        Optional<MessageChannel> messageChannelOpt = event.getChannel();
        if (!messageChannelOpt.isPresent()) {
            return;
        }

        switch (chatState) {
            case GLOBAL:
                if (Config.getBoolean(Config.USE_CHAT_CLAN_TAGS)) {
                    Text newMessage = Text.join(
                            clanPlayer.getClan().getTagColored(),
                            Text.of(" "),
                            event.getMessage()
                    );
                    event.setMessage(newMessage);
                }
                break;
            case CLAN:
                if (clanPlayer.getRank().hasPermission("clanchat")) {
                    if (clanPlayer.getIgnoreClanChat()) {
                        event.setCancelled(true);
                        clanPlayer.setChatState(PlayerChatState.GLOBAL);
                        Messages.sendYouNeedToUnignoreClanChatBeforeTalking(player);
                    } else {
                        event.setMessage(event.getRawMessage());
                        event.setChannel(ClanMessageChannelImpl.getFor(clanPlayer));
                    }
                } else {
                    event.setCancelled(true);
                    clanPlayer.setChatState(PlayerChatState.GLOBAL);
                    Messages.sendYouDoNotHaveTheRequiredPermission(player, "clanchat");
                }
                break;
            case ALLY:
                if (clanPlayer.getRank().hasPermission("allychat")) {
                    if (clanPlayer.getIgnoreAllyChat()) {
                        event.setCancelled(true);
                        clanPlayer.setChatState(PlayerChatState.GLOBAL);
                        Messages.sendYouNeedToUnignoreAllyChatBeforeTalking(player);
                    } else {
                        event.setMessage(event.getRawMessage());
                        event.setChannel(AllyMessageChannelImpl.getFor(clanPlayer));
                    }
                } else {
                    event.setCancelled(true);
                    clanPlayer.setChatState(PlayerChatState.GLOBAL);
                    Messages.sendYouDoNotHaveTheRequiredPermission(player, "allychat");
                }
                break;
        }
    }
}
