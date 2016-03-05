package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.channels.AllyMessageChannel;
import nl.riebie.mcclans.channels.ClanMessageChannel;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Multiline;
import nl.riebie.mcclans.commands.annotations.OptionalParameter;
import nl.riebie.mcclans.enums.PlayerChatState;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.Optional;

/**
 * Created by Mirko on 14/02/2016.
 */
public class ClanChatCommands {
    @Command(name = "clan", description = "Talk in clan chat", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.clanchat, spongePermission = "mcclans.user.chat.clan")
    public void clanChatCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Multiline @OptionalParameter(value = String.class, name = "message") Optional<String> messageOpt) {
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

    @Command(name = "ally", description = "Talk in ally chat", isPlayerOnly = true, isClanOnly = true, clanPermission = Permission.allychat, spongePermission = "mcclans.user.chat.ally")
    public void allyChatCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Multiline @OptionalParameter(value = String.class, name = "message") Optional<String> optionalMessage) {
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
    public void globalChatCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @Multiline @OptionalParameter(value = String.class, name = "message") Optional<String> optionalMessage) {
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
