package nl.riebie.mcclans.listeners;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.channels.AllyMessageChannel;
import nl.riebie.mcclans.channels.ClanMessageChannel;
import nl.riebie.mcclans.clan.ClanImpl;
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
import org.spongepowered.api.text.channel.MutableMessageChannel;

import java.util.Optional;

/**
 * Created by Koen on 13/02/2016.
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

        // TODO SPONGE which message to get, getMessage() or getRawMessage()?
        Text message = event.getMessage();
//        Text message = event.getRawMessage();

        ClanImpl clan = clanPlayer.getClan();
        MutableMessageChannel channel = messageChannelOpt.get().asMutable();

        switch (chatState) {
            case GLOBAL:
                if (Config.getBoolean(Config.USE_CHAT_CLAN_TAGS)) {
                    Text newMessage = Text.join(
                            clanPlayer.getClan().getTagColored(),
                            Text.of(" "),
                            message
                    );
                    event.setMessage(newMessage);
                }
                break;
            case CLAN:
                if (clanPlayer.getRank().hasPermission(Permission.clanchat)) {
                    event.setMessage(event.getRawMessage());
                    event.setChannel(ClanMessageChannel.getFor(clanPlayer));
//                    Text newMessage = Text.join(
//                            Text.builder("[").color(TextColors.GRAY).build(),
//                            Text.builder("CC").color(TextColors.YELLOW).build(),
//                            Text.builder("] [").color(TextColors.GRAY).build(),
//                            Text.builder(clanPlayer.getRank().getName()).color(TextColors.BLUE).build(),
//                            Text.builder("] ").color(TextColors.GRAY).build(),
//                            Text.of(player.getName() + ": "),
//                            message.toBuilder().color(TextColors.YELLOW).build()
//                    );
//                    event.setMessage(newMessage);
//
//                    channel.clearMembers();
//                    for (ClanPlayerImpl recipient : clan.getMembersImpl()) {
//                        Optional<Player> recipientPlayerOpt = Sponge.getServer().getPlayer(recipient.getUUID());
//                        if (recipientPlayerOpt.isPresent()) {
//                            channel.addMember(recipientPlayerOpt.get());
//                        }
//                    }
                } else {
                    event.setCancelled(true);
                    clanPlayer.setChatState(PlayerChatState.GLOBAL);
                    Messages.sendYouDoNotHaveTheRequiredPermission(player, Permission.clanchat.name());
                }
                break;
            case ALLY:
                if (clanPlayer.getRank().hasPermission(Permission.allychat)) {
                    event.setMessage(event.getRawMessage());
                    event.setChannel(AllyMessageChannel.getFor(clanPlayer));
//                    Text newMessage = Text.join(
//                            Text.builder("[").color(TextColors.GRAY).build(),
//                            Text.builder("AC").color(TextColors.GOLD).build(),
//                            Text.builder("] [").color(TextColors.GRAY).build(),
//                            clan.getTagColored(),
//                            Text.builder("] ").color(TextColors.GRAY).build(),
//                            Text.of(player.getName() + ": "),
//                            message.toBuilder().color(TextColors.GOLD).build()
//                    );
//                    event.setMessage(newMessage);
//
//                    channel.clearMembers();
//                    for (ClanPlayerImpl recipient : clan.getMembersImpl()) {
//                        Optional<Player> recipientPlayerOpt = Sponge.getServer().getPlayer(recipient.getUUID());
//                        if (recipientPlayerOpt.isPresent()) {
//                            channel.addMember(recipientPlayerOpt.get());
//                        }
//                    }
//                    for (ClanImpl ally : clan.getAlliesImpl()) {
//                        for (ClanPlayerImpl recipient : ally.getMembersImpl()) {
//                            Optional<Player> recipientPlayerOpt = Sponge.getServer().getPlayer(recipient.getUUID());
//                            if (recipientPlayerOpt.isPresent()) {
//                                channel.addMember(recipientPlayerOpt.get());
//                            }
//                        }
//                    }
                } else {
                    event.setCancelled(true);
                    clanPlayer.setChatState(PlayerChatState.GLOBAL);
                    Messages.sendYouDoNotHaveTheRequiredPermission(player, Permission.allychat.name());
                }
                break;
        }
    }

}
