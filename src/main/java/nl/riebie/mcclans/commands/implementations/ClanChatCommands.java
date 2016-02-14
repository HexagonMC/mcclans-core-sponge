package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.enums.PlayerChatState;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Optional;

/**
 * Created by Mirko on 14/02/2016.
 */
public class ClanChatCommands {
    @Command(name = "clan")
    public void clanChatRootCommand(ClanPlayerImpl clanPlayer, @Parameter(optional = true) String message) {
//        if (parameters.isOptionalUsed("message")) {
        if (clanPlayer.getTempChatState() == null) {
            Optional<Player> playerOpt = Sponge.getServer().getPlayer(clanPlayer.getUUID());
            if (!playerOpt.isPresent()) {
                return;
            }
            Player player = playerOpt.get();

            clanPlayer.setTempChatState(PlayerChatState.CLAN);
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
}
