package nl.riebie.mcclans.commands;

import nl.riebie.mcclans.commands.implementations.ClanCommands;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Mirko on 13/02/2016.
 */
public class CommandHandler implements CommandCallable {

    private final Optional<Text> desc = Optional.of(Text.of("Displays a message to all players"));
    private final Optional<Text> help = Optional.of(Text.of("Displays a message to all players. It has no color support!"));
    private final Text usage = Text.of("<message>");

    private final Server server;
    private CommandManager commandManager;

    public CommandHandler(Server server) {
        this.server = server;
        commandManager = new CommandManager();
        commandManager.registerCommandStructure("clan", ClanCommands.class);
    }

    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        commandManager.executeCommand(source, arguments.split(" "));
        return CommandResult.success();
    }

    public boolean testPermission(CommandSource source) {
        return source.hasPermission("myplugin.broadcast");
    }

    public Optional<Text> getShortDescription(CommandSource source) {
        return desc;
    }

    public Optional<Text> getHelp(CommandSource source) {
        return help;
    }

    public Text getUsage(CommandSource source) {
        return usage;
    }

    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return Collections.emptyList();
    }
}