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

package nl.riebie.mcclans.commands;

import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by riebie on 13/02/2016.
 */
public class CommandRoot implements CommandCallable {

    private final Optional<Text> desc;

    private final CommandManager commandManager;
    private final String root;

    private CommandRoot(String root, CommandManager commandExecutor, String description) {
        this.root = root;
        this.commandManager = commandExecutor;

        desc = Optional.of(Text.of(description));
    }

    public static CommandRoot newAllias(String alias, String[] fullCommand, CommandManager commandExecutor) {
        return new CommandRoot(alias, commandExecutor, String.format("alias for /clan %s", commandArrayToString(fullCommand)));
    }

    public static CommandRoot newRoot(String root, String name, CommandManager commandExecutor) {
        return new CommandRoot(root, commandExecutor, String.format("%s top command", name));
    }

    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        commandManager.executeCommand(source, root, "".equals(arguments) ? new String[0] : arguments.split(" "));
        return CommandResult.success();
    }

    public boolean testPermission(CommandSource source) {
        return true;
    }

    public Optional<Text> getShortDescription(CommandSource source) {
        return desc;
    }

    public Optional<Text> getHelp(CommandSource source) {
        return desc;
    }

    public Text getUsage(CommandSource source) {
        return desc.get();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments, @Nullable Location<World> targetPosition) throws CommandException {
        return Collections.emptyList();
    }

    public String getRoot() {
        return root;
    }

    private static String commandArrayToString(String[] array) {
        int iMax = array.length - 1;
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            b.append(String.valueOf(array[i]));
            if (i != iMax) {
                b.append(" ");
            }
        }
        return b.toString();
    }
}
