package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.commands.annotations.ChildGroup;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ExampleCommands {
    private final static String CLAN_CHAT_DESCRIPTION = "Example description";

    @Command(name = "exampleCommand", description = CLAN_CHAT_DESCRIPTION, permission = Permission.clanchat)
    public void exampleCommand(ClanPlayer clanPlayer, @Parameter(minimalLength = 5, maximalLength = 15) int message) {
        System.out.println(message);
    }

    @ChildGroup(ExampleSubCommands.class)
    @Command(name = "exampleSubCommand")
    public void clanChatCommand() {
        //Handle if subCommand is called without a child command
    }

}
