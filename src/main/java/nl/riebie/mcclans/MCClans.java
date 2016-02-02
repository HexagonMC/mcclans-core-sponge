package nl.riebie.mcclans;

import com.google.inject.Inject;
import nl.riebie.mcclans.config.Config;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;

/**
 * Created by K.Volkers on 8-12-2015.
 */
@Plugin(id = "nl.riebie.MCClans", name = "MCClans", version = "1.0")
public class MCClans {

    private static MCClans sPlugin;
    @Inject
    private Logger mLogger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File mConfigDir;

    public File getXmlDataFolder() {
        return new File(mConfigDir, "data");
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        sPlugin = this;
        if (!Config.load(mConfigDir)) {
            getLogger().error("Config failed to load");
            // todo stop plugin?
            return;
        }

//        Sponge.getCommandDispatcher().register(this, CommandFactory.create(), "armorhud");
//        Sponge.getEventManager().registerListeners(this, new UpdateArmorListener());
    }

    public static MCClans getPlugin() {
        return sPlugin;
    }

    public Logger getLogger() {
        return mLogger;
    }
}
