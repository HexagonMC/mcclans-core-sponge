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

import java.util.Optional;

/**
 * Created by Mirko on 14/02/2016.
 */
public class ClanChatCommands {
    @Command(name = "clan")
    public void clanChatRootCommand(ClanPlayerImpl clanPlayer, @OptionalParameter(String.class) Optional<String> message) {
//        if (parameters.isOptionalUsed("message")) {
        if (clanPlayer.getTempChatState() == null) {
            Optional<Player> playerOpt = Sponge.getServer().getPlayer(clanPlayer.getUUID());
            if (!playerOpt.isPresent()) {
                return;
            }
            Player player = playerOpt.get();

            clanPlayer.setTempChatState(PlayerChatState.CLAN);
            ClanMessageChannel.getFor(clanPlayer).send(player, Text.of(message));
            clanPlayer.setTempChatState(null);

            //Sponge.getServer().getBroadcastChannel().send(player, Text.of(message));
            // TODO SPONGE ERRYTHIN IN DIS CLASS

        }
//        } else {
//            PlayerChatState chatState = clanPlayer.getChatState();
//            if (chatState.equals(PlayerChatState.CLAN)) {
//                clanPlayer.setChatState(PlayerChatState.GLOBAL);
//                Messages.sendNowTalkingInGlobal(sender);
//            } else {
//                clanPlayer.setChatState(PlayerChatState.CLAN);
//                Messages.sendNowTalkingInClanChat(sender);
//            }
//        }
    }

    @Command(name = "ally")
    public void allyChatRootCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @OptionalParameter(String.class) Optional<String> optionalMessage) {
        if (optionalMessage.isPresent()) {
            String message = optionalMessage.get();
            if (clanPlayer.getTempChatState() == null) {
                Optional<Player> playerOpt = Sponge.getServer().getPlayer(clanPlayer.getUUID());
                if (!playerOpt.isPresent()) {
                    return;
                }
                Player player = playerOpt.get();

                clanPlayer.setTempChatState(PlayerChatState.ALLY);
                AllyMessageChannel.getFor(clanPlayer).send(player, Text.of(message));
                clanPlayer.setTempChatState(null);
            }
        } else {
            PlayerChatState chatState = clanPlayer.getChatState();
            if (chatState.equals(PlayerChatState.ALLY)) {
                clanPlayer.setChatState(PlayerChatState.GLOBAL);
                Messages.sendNowTalkingInGlobal(sender);
            } else {
                clanPlayer.setChatState(PlayerChatState.ALLY);
                Messages.sendNowTalkingInAllyChat(sender);
            }
        }
    }
}
