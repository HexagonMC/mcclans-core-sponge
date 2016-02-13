package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.text.Text;

/**
 * Created by Mirko on 14/02/2016.
 */
public class ClanChatCommands {
    @Command(name = "ally")
    public void clanChatRootCommand(ClanPlayerImpl clanPlayer, @Parameter String text){
        //TODO Koen doe je kunstje
    }
}
