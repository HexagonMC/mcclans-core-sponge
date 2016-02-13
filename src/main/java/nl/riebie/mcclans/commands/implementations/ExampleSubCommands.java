package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.player.ClanPlayerImpl;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ExampleSubCommands {

    @Command(name = "subCommand1")
    public void exampleCommand(ClanPlayerImpl clanPlayer, @Parameter(minimalLength = 5, maximalLength = 15) int message) {
        System.out.println(message);
    }
}
