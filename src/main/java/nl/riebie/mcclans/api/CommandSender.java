package nl.riebie.mcclans.api;

import nl.riebie.mcclans.api.enums.Permission;
import org.spongepowered.api.text.Text;

/**
 * Created by Mirko on 13/02/2016.
 */
public interface CommandSender {

    void sendMessage(Text... message);

    boolean checkPermission(Permission permission);
}
