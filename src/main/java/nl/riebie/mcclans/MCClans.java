package nl.riebie.mcclans;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import nl.riebie.mcclans.config.Config;
import org.slf4j.Logger;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        sPlugin = this;
        loadConfig();

//        Sponge.getCommandDispatcher().register(this, CommandFactory.create(), "armorhud");
//        Sponge.getEventManager().registerListeners(this, new UpdateArmorListener());
    }

    public void loadConfig() {
        if (!mConfigDir.exists()) {
            mConfigDir.mkdir();
        }

        File config = new File(mConfigDir, "config.conf");
        ConfigurationLoader<CommentedConfigurationNode> configLoader;
        CommentedConfigurationNode configNode;

        try {
            if (config.exists()) {
                configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                configNode = configLoader.load();
            } else {
                config.createNewFile();
                configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                configNode = configLoader.createEmptyNode(ConfigurationOptions.defaults());
            }

            Config.load(configNode);

            configLoader.save(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean bla = Config.getBoolean(Config.USE_PERMISSIONS);
        String bla2 = Config.getString(Config.SOME_STRING);
        int bla3 = Config.getInteger(Config.TELEPORT_DELAY_SECONDS);
        List<String> bla4 = Config.getList(Config.SOME_LIST, String.class);
    }

    public static MCClans getPlugin() {
        return sPlugin;
    }

    public Logger getLogger() {
        return mLogger;
    }
}
