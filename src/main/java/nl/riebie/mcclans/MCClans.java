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

package nl.riebie.mcclans;

import com.google.inject.Inject;
import io.github.nucleuspowered.nucleus.api.exceptions.PluginAlreadyRegisteredException;
import io.github.nucleuspowered.nucleus.api.service.NucleusMessageTokenService;
import nl.riebie.mcclans.api.ClanService;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.commands.CommandRoot;
import nl.riebie.mcclans.commands.implementations.ClanCommands;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.enums.DBMSType;
import nl.riebie.mcclans.listeners.*;
import nl.riebie.mcclans.metrics.BStatsMetrics;
import nl.riebie.mcclans.metrics.MetricsWrapper;
import nl.riebie.mcclans.persistence.DatabaseConnectionOwner;
import nl.riebie.mcclans.persistence.DatabaseHandler;
import nl.riebie.mcclans.persistence.TaskExecutor;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.FileUtils;
import nl.riebie.mcclans.utils.Pair;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.*;

/**
 * Created by Kippers on 8-12-2015.
 */
@Plugin(
        id = "mcclans", name = "MCClans", version = "1.5", description = "Player grouping by forming clans",
        dependencies = @Dependency(id = "nucleus", optional = true)
)
public class MCClans {

    private static MCClans plugin;
    private ServiceHelper serviceHelper = new ServiceHelper();

    @Inject
    private Logger spongeLogger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    public MetricsWrapper stats;
    @Inject
    public BStatsMetrics bStats;

    private boolean loadError = false;

    @Listener
    public void onPreInitialize(GamePreInitializationEvent event) {
        stats.start();
        Sponge.getServiceManager().setProvider(this, ClanService.class, ClansImpl.getInstance());
    }

    @Listener
    public void onPostInitialize(GamePostInitializationEvent event) {
        ClansImpl.getInstance().getClanPermissionManager().setInitialized();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        plugin = this;

        getLogger().init(this, spongeLogger);
        // If an error occurs during config loading, make sure it gets logged (not enabled by default as the config has
        // not been checked yet if local logging is enabled. But we want to make sure any config loading error is
        // logged regardless)
        getLogger().enableLocalLoggingTrigger(true);

        // Init config
        if (!Config.load(configDir)) {
            getLogger().error("Config failed to load!", true);
            // todo stop plugin?
            return;
        }

        getLogger().enableLocalLoggingTrigger(false);
        if (Config.getBoolean(Config.LOGGING)) {
            getLogger().enableLocalLogging(configDir);
        }

        // TODO SPONGE reloadSettings stuff?
        //reloadSettings();

        // Init services
        if (!serviceHelper.initUserStorageService()) {
            MCClans.getPlugin().getLogger().warn("Could not find UserStorageService during initialization!", true);
            // todo stop plugin?
        }
        if (Config.getBoolean(Config.USE_ECONOMY) && !serviceHelper.initEconomyService()) {
            // todo what if config option 'use economy' is changed and reload is called, economy service not available?
            MCClans.getPlugin().getLogger().warn("Could not find EconomyService during initialization! Deactivating economy usage for MCClans", true);
            Config.setValue(Config.USE_ECONOMY, false);
        }
        if (serviceHelper.initNucleusMessageTokenService(Sponge.getPluginManager().getPlugin("mcclans"))) {
            MCClans.getPlugin().getLogger().info("Registered to Nucleus message token service", false);
        }

        // Init database/xml
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
            try {
                DatabaseHandler.getInstance().load();
            } catch (Exception e) {
                loadError = true;
                getLogger().error("MCClans: Fatal error during data load: " + e.getMessage(), true);
                Sponge.getServer().shutdown(Text.of("MCClans: Fatal error during data load!"));
                throw e;
            }
            getLogger().info("Finished loading in: " + (System.currentTimeMillis() - databaseLoadStartTime) + "ms", true);
            DatabaseHandler.getInstance().removeMarkedInactiveClanPlayers();
            getLogger().info("Database updater starting...", false);
            if (TaskExecutor.getInstance().initialize()) {
                getLogger().info("Database updater successfully started", false);
            }
            registerDatabasePollingTask();
        } else {
            getLogger().info("Starting load from flat file...", true);
            long databaseLoadStartTime = System.currentTimeMillis();
            try {
                if (DatabaseHandler.getInstance().load()) {
                    getLogger().info("Finished loading in: " + (System.currentTimeMillis() - databaseLoadStartTime) + "ms", true);
                    DatabaseHandler.getInstance().removeMarkedInactiveClanPlayers();
                } else {
                    getLogger().info("No data loaded from flat file", true);
                }
            } catch (Exception e) {
                loadError = true;
                getLogger().error("MCClans: Fatal error during data load: " + e.getMessage(), true);
                Sponge.getServer().shutdown(Text.of("MCClans: Fatal error during data load!"));
                throw e;
            }
        }

        if (Config.getInteger(Config.CREATE_BACKUP_AFTER_HOURS) != 0) {
            registerBackupTask();
        }

        ClansImpl.getInstance().updateClanTagCache();

        // Register default ranks
        List<Pair<String, List<String>>> convertedDefaultRanks = new ArrayList<>();
        Map<String, String> rawDefaultRanks = Config.getMap(Config.DEFAULT_CLAN_RANKS, String.class, String.class);
        for (Map.Entry<String, String> entry : rawDefaultRanks.entrySet()) {
            String key = entry.getKey().replace(" ", "");
            List<String> value = Arrays.asList(entry.getValue().replace(" ", "").split(","));
            convertedDefaultRanks.add(Pair.createPair(key, value));
        }
        RankFactory.getInstance().registerDefaultRanks(convertedDefaultRanks);

        // Register listeners and commands
        Sponge.getEventManager().registerListeners(this, ClansImpl.getInstance());
        Sponge.getEventManager().registerListeners(this, new PlayerChatListener());
        Sponge.getEventManager().registerListeners(this, new ClientConnectionListener());
        Sponge.getEventManager().registerListeners(this, new UpdateLastPlayerDamageListener());
        Sponge.getEventManager().registerListeners(this, new FriendlyFireListener());
        Sponge.getEventManager().registerListeners(this, new KillDeathRatioListener());

        CommandManager cmdService = Sponge.getCommandManager();
        nl.riebie.mcclans.commands.CommandManager commandManager = new nl.riebie.mcclans.commands.CommandManager();
        List<CommandRoot> commandRoots = commandManager.registerCommandStructure("clan", ClanCommands.class);
        for (CommandRoot commandRoot : commandRoots) {
            cmdService.register(this, commandRoot, commandRoot.getRoot());
        }
    }

    public void reloadSettings() {
        // TODO SPONGE
//        Configuration.reset();
//        reloadConfig();
//        loadSettings();
//        setupEconomy();
    }

    @Listener
    public void onServerStop(GameStoppingServerEvent event) {
        if (loadError) {
            if (Config.getBoolean(Config.USE_DATABASE)) {
                TaskExecutor.getInstance().terminate();
                DatabaseConnectionOwner.getInstance().close();
            }

            // Do not save data if loading failed, to prevent overwriting good data
            return;
        }

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
            getLogger().info("Starting save to flat file..", true);
            long databaseSaveStartTime = System.currentTimeMillis();
            if (DatabaseHandler.getInstance().save()) {
                getLogger().info("Successfully saved to flat file in: " + (System.currentTimeMillis() - databaseSaveStartTime) + "ms", true);
            } else {
                getLogger().error("Error saving flat file!", true);
            }
        }
    }

    private void registerBackupTask() {
        File backupFolder = new File(getDataFolder(), "backup");
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
        return plugin;
    }

    public File getDataFolder() {
        return new File(configDir, "data");
    }

    public nl.riebie.mcclans.messages.Logger getLogger() {
        return nl.riebie.mcclans.messages.Logger.get();
    }

    public ServiceHelper getServiceHelper() {
        return serviceHelper;
    }

    // Depends on Config being loaded
    public static class ServiceHelper {
        public UserStorageService userStorageService;
        public EconomyService economyService;
        public Currency currency;
        public NucleusMessageTokenService nucleusMessageTokenService;

        private boolean initUserStorageService() {
            Optional<ProviderRegistration<UserStorageService>> userStorageOpt = Sponge.getServiceManager().getRegistration(UserStorageService.class);
            if (userStorageOpt.isPresent()) {
                userStorageService = userStorageOpt.get().getProvider();
                return true;
            } else {
                return false;
            }
        }

        private boolean initEconomyService() {
            Optional<ProviderRegistration<EconomyService>> economyServiceOpt = Sponge.getServiceManager().getRegistration(EconomyService.class);
            if (economyServiceOpt.isPresent()) {
                economyService = economyServiceOpt.get().getProvider();
                String currencyName = Config.getString(Config.CURRENCY);
                if (currencyName.equalsIgnoreCase("default")) {
                    currency = economyService.getDefaultCurrency();
                } else {
                    for (Currency checkCurrency : economyService.getCurrencies()) {
                        if (currencyName.equalsIgnoreCase(checkCurrency.getDisplayName().toPlain())) {
                            currency = checkCurrency;
                            break;
                        }
                    }
                    if (currency == null) {
                        currency = economyService.getDefaultCurrency();
                        getPlugin().getLogger().warn("Currency " + currencyName + " not found, falling back to default currency", false);
                    }
                }
                return true;
            } else {
                return false;
            }
        }

        private boolean initNucleusMessageTokenService(Optional<PluginContainer> pluginContainerOpt) {
            Optional<ProviderRegistration<NucleusMessageTokenService>> nucleusMessageTokenServiceOpt = Sponge.getServiceManager().getRegistration(NucleusMessageTokenService.class);
            if (pluginContainerOpt.isPresent() && nucleusMessageTokenServiceOpt.isPresent()) {
                nucleusMessageTokenService = nucleusMessageTokenServiceOpt.get().getProvider();
                try {
                    nucleusMessageTokenService.register(pluginContainerOpt.get(), new NucleusMessageTokenService.TokenParser() {
                        @Nonnull
                        @Override
                        public Optional<Text> parse(String tokenInput, CommandSource source, Map<String, Object> variables) {
                            if (!"clantag".equals(tokenInput)) {
                                return Optional.empty();
                            }
                            if (!(source instanceof Player)) {
                                return Optional.empty();
                            }
                            Player player = (Player) source;
                            ClanPlayerImpl clanPlayer = ClansImpl.getInstance().getClanPlayer(player.getUniqueId());
                            if (clanPlayer == null || clanPlayer.getClan() == null) {
                                return Optional.empty();
                            }

                            return Optional.of(clanPlayer.getClan().getTagColored());
                        }
                    });
                } catch (PluginAlreadyRegisteredException e) {
                    MCClans.getPlugin().getLogger().error("Could not register to Nucleus message token service!", e, false);
                    return false;
                }

                return true;
            } else {
                return false;
            }
        }
    }
}
