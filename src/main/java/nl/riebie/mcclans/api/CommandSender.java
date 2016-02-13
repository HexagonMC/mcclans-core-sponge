package nl.riebie.mcclans.api;

import org.spongepowered.api.text.Text;

/**
 * Created by Mirko on 13/02/2016.
 */
public interface CommandSender {

    void sendMessage(Text... message);
}
