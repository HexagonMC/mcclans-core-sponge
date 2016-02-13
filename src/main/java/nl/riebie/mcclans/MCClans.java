package nl.riebie.mcclans;

import com.google.inject.Inject;
import nl.riebie.mcclans.commands.CommandHandler;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.database.DatabaseConnectionOwner;
import nl.riebie.mcclans.database.DatabaseHandler;
import nl.riebie.mcclans.database.TaskExecutor;
import nl.riebie.mcclans.enums.DBMSType;
import nl.riebie.mcclans.listeners.ClientConnectionListener;
import nl.riebie.mcclans.listeners.FriendlyFireListener;
import nl.riebie.mcclans.listeners.KillDeathRatioListener;
import nl.riebie.mcclans.listeners.UpdateLastPlayerDamageListener;
import nl.riebie.mcclans.utils.FileUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

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
        // TODO SPONGE reloadSettings stuff
        //reloadSettings();
        if (Config.getBoolean(Config.USE_DATABASE) && DBMSType.getType(Config.getString(Config.DBMS_TYPE)).equals(DBMSType.UNRECOGNISED)) {
            Config.setValue(Config.USE_DATABASE, false);
            getLogger().warn("Could not recognise 'dbms-type' in config. Deactivating database usage for MCClans", true);
        }
        if (Config.getBoolean(Config.USE_DATABASE) && !DatabaseHandler.getInstance().setupConnection()) {
            Config.setValue(Config.USE_DATABASE, false);
            getLogger().warn("Failed to setup connection with database " + Config.getString(Config.DATABASE_NAME) + ". Deactivating database usage for MCClans", true);
        }
        if (Config.getBoolean(Config.USE_DATABASE)) {
            DatabaseHandler.getInstance().setupDatabase();
            getLogger().info("Starting load from database...", true);
            long databaseLoadStartTime = System.currentTimeMillis();
            DatabaseHandler.getInstance().load();
            getLogger().info("Finished loading in: " + (System.currentTimeMillis() - databaseLoadStartTime) + "ms", true);
            DatabaseHandler.getInstance().removeMarkedInactiveClanPlayers();
            getLogger().info("Database updater starting...", false);
            if (TaskExecutor.getInstance().initialize()) {
                getLogger().info("Database updater successfully started", false);
            }
            registerDatabasePollingTask();
        } else {
            getLogger().info("Starting load from xml...", true);
            long databaseLoadStartTime = System.currentTimeMillis();
            if (DatabaseHandler.getInstance().load()) {
                getLogger().info("Finished loading in: " + (System.currentTimeMillis() - databaseLoadStartTime) + "ms", true);
                DatabaseHandler.getInstance().removeMarkedInactiveClanPlayers();
            } else {
                getLogger().info("No data loaded from xml", true);
            }
        }

        if (Config.getInteger(Config.CREATE_BACKUP_AFTER_HOURS) != 0) {
            registerBackupTask();
        }

        ClansImpl.getInstance().updateClanTagCache();

        // TODO SPONGE something wtih command/event registering
//        this.getServer().getPluginManager().registerEvents(ClansImpl.getInstance(), this);
//        this.getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);
//        this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
//        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
//        this.getServer().getPluginManager().registerEvents(new PlayerDamageByPlayerListener(), this);
//        this.getServer().getPluginManager().registerEvents(new FriendlyFireListener(), this);
//        this.getServer().getPluginManager().registerEvents(new KillDeathRatioListener(), this);
//        this.getServer().getPluginManager().registerEvents(PlayerCommandPreprocessListener.getInstance(), this);
//        Bukkit.getServer().getPluginCommand("clan").setExecutor(new ClanCommandExecutor());

//        Sponge.getCommandDispatcher().register(this, CommandFactory.create(), "armorhud");
        Sponge.getEventManager().registerListeners(this, ClansImpl.getInstance());
        Sponge.getEventManager().registerListeners(this, new ClientConnectionListener());
        Sponge.getEventManager().registerListeners(this, new UpdateLastPlayerDamageListener());
        Sponge.getEventManager().registerListeners(this, new FriendlyFireListener());
        Sponge.getEventManager().registerListeners(this, new KillDeathRatioListener());

        CommandManager cmdService = Sponge.getCommandManager();
        cmdService.register(this, new CommandHandler(Sponge.getServer()), "clan");
    }

    public void reloadSettings() {
        // TODO SPONGE
//        Configuration.reset();
//        reloadConfig();
//        loadSettings();
//        setupEconomy();
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event) {
        if (Config.getBoolean(Config.USE_DATABASE)) {
            getLogger().info("Database updater shutting down...", false);
            TaskExecutor.getInstance().terminate();
            getLogger().info("Database updater successfully shut down", false);
            getLogger().info("Starting save to database...", true);
            long databaseSaveStartTime = System.currentTimeMillis();
            if (DatabaseHandler.getInstance().save()) {
                getLogger().info("Successfully saved to database in: " + (System.currentTimeMillis() - databaseSaveStartTime) + "ms", true);
            } else {
                getLogger().error("Error saving database!", true);
            }
            DatabaseConnectionOwner.getInstance().close();
        } else {
            getLogger().info("Starting save to xml...", true);
            long databaseSaveStartTime = System.currentTimeMillis();
            if (DatabaseHandler.getInstance().save()) {
                getLogger().info("Successfully saved to xml in: " + (System.currentTimeMillis() - databaseSaveStartTime) + "ms", true);
            } else {
                getLogger().error("Error saving xml!", true);
            }
        }
    }

    private void registerBackupTask() {
        File backupFolder = new File(getXmlDataFolder(), "backup");
        File lastBackup = FileUtils.getLastModifiedFileInFolder(backupFolder);
        long nextBackupInTicks;
        if (lastBackup != null) {
            long milisSinceLastBackup = System.currentTimeMillis() - lastBackup.lastModified();
            long milisTillNextBackup = (Config.getInteger(Config.CREATE_BACKUP_AFTER_HOURS) * 3600000) - milisSinceLastBackup;
            if (milisTillNextBackup < 50) {
                milisTillNextBackup = 50;
            }
            nextBackupInTicks = milisTillNextBackup / 50;
        } else {
            nextBackupInTicks = 1;
        }

        long delayBetweenBackupsInTicks = Config.getInteger(Config.CREATE_BACKUP_AFTER_HOURS) * 72000; // hours * 3600 * 20 = ticks

        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseHandler.getInstance().backup();
            }
        });
        taskBuilder.delayTicks(nextBackupInTicks).intervalTicks(delayBetweenBackupsInTicks).submit(MCClans.getPlugin());

        getLogger().info("Registered backup task to run every " + delayBetweenBackupsInTicks / 20 + "s (" + delayBetweenBackupsInTicks / 72000 + "h), starting in " + nextBackupInTicks / 20 + "s", false);

    }

    private void registerDatabasePollingTask() {
        int delayInSeconds = 3600;
        int delayInTicks = delayInSeconds * 20;

        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.execute(new Runnable() {
            @Override
            public void run() {
                if (!DatabaseConnectionOwner.getInstance().isValid()) {
                    DatabaseConnectionOwner.getInstance().setupConnection();
                }
            }
        });
        taskBuilder.delayTicks(delayInTicks).intervalTicks(delayInTicks).submit(MCClans.getPlugin());

        getLogger().info("Registered database polling task to run every " + delayInSeconds + "s (" + delayInSeconds / 3600 + "h)", false);
    }

    public static MCClans getPlugin() {
        return sPlugin;
    }

    public Logger getLogger() {
        return mLogger;
    }
}
