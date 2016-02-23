package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.channels.AllyMessageChannel;
import nl.riebie.mcclans.channels.ClanMessageChannel;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.OptionalParameter;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.enums.PlayerChatState;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;

import java.util.Optional;

/**
 * Created by Mirko on 14/02/2016.
 */
public class ClanChatCommands {
    @Command(name = "clan")
    public void clanChatRootCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @OptionalParameter(String.class) Optional<String> messageOpt) {
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

    @Command(name = "ally")
    public void allyChatRootCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @OptionalParameter(String.class) Optional<String> optionalMessage) {
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

    @Command(name = "global")
    public void globalChatRootCommand(CommandSource commandSource, ClanPlayerImpl clanPlayer, @OptionalParameter(String.class) Optional<String> optionalMessage) {
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
