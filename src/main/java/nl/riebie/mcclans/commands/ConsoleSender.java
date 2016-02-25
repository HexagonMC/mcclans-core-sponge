package nl.riebie.mcclans.commands;

import nl.riebie.mcclans.api.CommandSender;
import nl.riebie.mcclans.api.enums.Permission;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.text.Text;

/**
 * Created by Mirko on 25/02/2016.
 */
public class ConsoleSender implements CommandSender {

    private final ConsoleSource consoleSource;

    public ConsoleSender(ConsoleSource consoleSource){
        this.consoleSource = consoleSource;
    }

    @Override
    public void sendMessage(Text... message) {
        consoleSource.sendMessages(message);
    }

    @Override
    public boolean checkPermission(Permission permission) {
        return true;
    }
}
