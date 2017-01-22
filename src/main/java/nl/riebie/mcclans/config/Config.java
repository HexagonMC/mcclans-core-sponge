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

package nl.riebie.mcclans.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.config.constraints.ValidAliasMapConstraint;
import nl.riebie.mcclans.config.model.ConfigOption;
import nl.riebie.mcclans.config.model.ConfigSection;
import nl.riebie.mcclans.utils.MessageBoolean;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Kippers on 22/12/2015.
 */
public class Config {

    // ======================================== SECTION GENERAL ======================================== //
    private static final String SECTION_GENERAL = "general";

    public static final String DEBUGGING = "debugging";
    public static final String LOGGING = "logging";
    public static final String USE_PERMISSIONS = "use-permissions";
    public static final String TELEPORT_DELAY_SECONDS = "teleport-delay-seconds";
    public static final String TELEPORT_COOLDOWN_SECONDS = "teleport-cooldown-seconds";
    public static final String RE_SET_CLANHOME_COOLDOWN_SECONDS = "re-set-clanhome-cooldown-seconds";
    public static final String USE_CHAT_CLAN_TAGS = "use-chat-clan-tags";
    public static final String CLAN_TAG_DEFAULT_COLOR = "clan-tag-default-color";
    public static final String USE_COLORED_TAGS_BASED_ON_CLAN_KDR = "use-colored-tags-based-on-clan-kdr";
    public static final String ALLOW_FF_PROTECTION = "allow-ff-protection";
    public static final String LOG_PLAYER_KDR = "log-player-kdr";
    public static final String BLOCKED_WORLDS_FF_PROTECTION = "blocked-worlds-ff-protection";
    public static final String BLOCKED_WORLDS_PLAYER_KDR = "blocked-worlds-player-kdr";
    public static final String BLOCKED_CLAN_TAGS_AND_NAMES = "blocked-clan-tags-and-names";

    public static final String CREATE_BACKUP_AFTER_HOURS = "create-backup-after-hours";
    public static final String MAXIMUM_AMOUNT_OF_BACKUPS_BEFORE_REMOVING_OLDEST = "maximum-amount-of-backups-before-removing-oldest";
    public static final String REMOVE_INACTIVE_CLAN_PLAYERS_AFTER_DAYS = "remove-inactive-clan-players-after-days";
    public static final String REMOVE_INACTIVE_CLAN_OWNERS_INCLUDING_CLAN = "remove-inactive-clan-owners-including-clan";

    public static final String CLAN_TAG_REGEX = "clan-tag-regex";
    public static final String CLAN_TAG_CHARACTERS_MINIMUM = "clan-tag-characters-minimum";
    public static final String CLAN_TAG_CHARACTERS_MAXIMUM = "clan-tag-characters-maximum";
    public static final String CLAN_NAME_REGEX = "clan-name-regex";
    public static final String CLAN_NAME_CHARACTERS_MINIMUM = "clan-name-characters-minimum";
    public static final String CLAN_NAME_CHARACTERS_MAXIMUM = "clan-name-characters-maximum";

    // ======================================== SECTION DATABASE ======================================== //
    private static final String SECTION_DATABASE = "database";

    public static final String USE_DATABASE = "use-database";
    public static final String DBMS_TYPE = "dbms-type";
    public static final String DATABASE_SERVER_PORT = "database-server-port";
    public static final String DATABASE_SERVER = "database-server";
    public static final String DATABASE_NAME = "database-name";
    public static final String DATABASE_SERVER_USER = "database-server-user";
    public static final String DATABASE_SERVER_PASSWORD = "database-server-password";

    // ======================================== SECTION ECONOMY ======================================== //
    private static final String SECTION_ECONOMY = "economy";

    public static final String USE_ECONOMY = "use-economy";
    public static final String CURRENCY = "currency";
    public static final String CLAN_CREATION_COST = "clan-creation-cost";
    public static final String SET_CLANHOME_COST = "set-clanhome-cost";
    public static final String RE_SET_CLANHOME_COST_INCREASE = "re-set-clanhome-cost-increase";
    public static final String TELEPORT_COST = "teleport-cost";

    // ======================================== SECTION COMMAND ALIASES ======================================== //
    private static final String SECTION_COMMAND_ALIASES = "command-aliases";

    public static final String COMMAND_ALIASES = "command-aliases";

    // ======================================== SECTION DEFAULT RANKS ======================================== //
    private static final String SECTION_DEFAULT_CLAN_RANKS = "default-clan-ranks";

    public static final String DEFAULT_CLAN_RANKS = "default-clan-ranks";

    // Loaded config values
    private static Map<String, Object> sConfig = new HashMap<>();

    private static List<ConfigSection> getDefaults() {
        List<ConfigSection> configSections = new ArrayList<>();

        ConfigSection generalConfigSection = ConfigSection.builder(SECTION_GENERAL).setConfigOptions(
                ConfigOption.builder(DEBUGGING, false).build(),
                ConfigOption.builder(LOGGING, true).build(),
                ConfigOption.builder(USE_PERMISSIONS, false).build(),
                ConfigOption.builder(TELEPORT_DELAY_SECONDS, 5).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(TELEPORT_COOLDOWN_SECONDS, 120).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(RE_SET_CLANHOME_COOLDOWN_SECONDS, 1800).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(USE_CHAT_CLAN_TAGS, true).build(),
                ConfigOption.builder(CLAN_TAG_DEFAULT_COLOR, TextColors.DARK_PURPLE.getName()).addColorConstraint().build(),
                ConfigOption.builder(USE_COLORED_TAGS_BASED_ON_CLAN_KDR, true).build(),
                ConfigOption.builder(ALLOW_FF_PROTECTION, true).build(),
                ConfigOption.builder(LOG_PLAYER_KDR, true).build(),
                ConfigOption.builder(BLOCKED_WORLDS_FF_PROTECTION, Arrays.asList("example_world1", "example_world2")).build(),
                ConfigOption.builder(BLOCKED_WORLDS_PLAYER_KDR, Arrays.asList("example_world1", "example_world2")).build(),
                ConfigOption.builder(BLOCKED_CLAN_TAGS_AND_NAMES, Arrays.asList("example_name1", "example_name2")).build(),

                ConfigOption.builder(CREATE_BACKUP_AFTER_HOURS, 24).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(MAXIMUM_AMOUNT_OF_BACKUPS_BEFORE_REMOVING_OLDEST, 14).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(REMOVE_INACTIVE_CLAN_PLAYERS_AFTER_DAYS, 60).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(REMOVE_INACTIVE_CLAN_OWNERS_INCLUDING_CLAN, false).build(),

                ConfigOption.builder(CLAN_TAG_REGEX, "[A-Za-z0-9_]+").build(),
                ConfigOption.builder(CLAN_TAG_CHARACTERS_MINIMUM, 2).addMinimumNumberConstraint(1).build(),
                ConfigOption.builder(CLAN_TAG_CHARACTERS_MAXIMUM, 6).addMinimumNumberConstraint(1).build(),
                ConfigOption.builder(CLAN_NAME_REGEX, "[A-Za-z0-9_]+").build(),
                ConfigOption.builder(CLAN_NAME_CHARACTERS_MINIMUM, 2).addMinimumNumberConstraint(1).build(),
                ConfigOption.builder(CLAN_NAME_CHARACTERS_MAXIMUM, 30).addMinimumNumberConstraint(1).build()
        ).build();

        ConfigSection databaseConfigSection = ConfigSection.builder(SECTION_DATABASE).setConfigOptions(
                ConfigOption.builder(USE_DATABASE, false).build(),
                ConfigOption.builder(DBMS_TYPE, "mysql").addOneOfStringConstraint(true, "mysql", "h2").setValueIfConstraintFailed("unrecognised").build(),
                ConfigOption.builder(DATABASE_SERVER_PORT, 3306).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(DATABASE_SERVER, "localhost").build(),
                ConfigOption.builder(DATABASE_NAME, "database_name").build(),
                ConfigOption.builder(DATABASE_SERVER_USER, "user").build(),
                ConfigOption.builder(DATABASE_SERVER_PASSWORD, "password").build()
        ).build();

        ConfigSection economyConfigSection = ConfigSection.builder(SECTION_ECONOMY).setConfigOptions(
                ConfigOption.builder(USE_ECONOMY, false).build(),
                ConfigOption.builder(CURRENCY, "default").build(),
                ConfigOption.builder(CLAN_CREATION_COST, 50).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(SET_CLANHOME_COST, 10).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(RE_SET_CLANHOME_COST_INCREASE, 0).addMinimumNumberConstraint(0).build(),
                ConfigOption.builder(TELEPORT_COST, 0).addMinimumNumberConstraint(0).build()
        ).build();

        Map<String, String> commandAliases = new HashMap<>();
        commandAliases.put("/g", "/clan chat global");
        commandAliases.put("/cc", "/clan chat clan");
        commandAliases.put("/ac", "/clan chat ally");
        commandAliases.put("/clanff", "/clan friendlyfire clan toggle");
        commandAliases.put("/personalff", "/clan friendlyfire personal toggle");

        ConfigSection commandAliasesConfigSection = ConfigSection.builder(SECTION_COMMAND_ALIASES).setConfigOptions(
                ConfigOption.builder(COMMAND_ALIASES, commandAliases).addConstraints(new ValidAliasMapConstraint()).setValueIfConstraintFailed(Collections.EMPTY_MAP).build()
        ).build();

        Map<String, String> defaultRanks = new HashMap<>();
        defaultRanks.put("Member", String.join(",", "home", "coords", "clanchat", "allychat", "deposit"));

        ConfigSection defaultRanksConfigSection = ConfigSection.builder(SECTION_DEFAULT_CLAN_RANKS).setConfigOptions(
                ConfigOption.builder(DEFAULT_CLAN_RANKS, defaultRanks).build()
        ).build();

        configSections.add(generalConfigSection);
        configSections.add(databaseConfigSection);
        configSections.add(economyConfigSection);
        configSections.add(commandAliasesConfigSection);
        configSections.add(defaultRanksConfigSection);

        return configSections;
    }

    public static boolean load(File configDir) {
        if (!configDir.exists() && !configDir.mkdir()) {
            // todo failed to create config dir go die
            return false;
        }

        File config = new File(configDir, "config.conf");
        ConfigurationLoader<CommentedConfigurationNode> configLoader;
        CommentedConfigurationNode rootNode;

        try {
            if (config.exists()) {
                configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                rootNode = configLoader.load();
            } else if (config.createNewFile()) {
                configLoader = HoconConfigurationLoader.builder().setFile(config).build();
                rootNode = configLoader.createEmptyNode();
            } else {
                // todo failed to create file for config go die
                return false;
            }

            sConfig.clear();
            putDefaultsAndGetValues(rootNode);

            configLoader.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
            // todo some error go die
            return false;
        }

        return true;
    }

    private static void putDefaultsAndGetValues(CommentedConfigurationNode rootNode) {
        for (ConfigSection configSection : getDefaults()) {
            CommentedConfigurationNode sectionNode = rootNode.getNode(configSection.key);
            if (sectionNode.isVirtual() && configSection.hasComment()) {
                sectionNode.setComment(configSection.comment);
            }
            for (ConfigOption configOption : configSection.configOptions) {
                CommentedConfigurationNode subNode = sectionNode.getNode(configOption.key);
                if (subNode.isVirtual()) {
                    subNode.setValue(configOption.value);
                    if (configOption.hasComment()) {
                        subNode.setComment(configOption.comment);
                    }
                }
                Object value = subNode.getValue();
                MessageBoolean isOfType = configOption.isOfType(value);
                if (isOfType.bool) {
                    MessageBoolean meetsConstraints = configOption.meetsConstraints(value);
                    if (!meetsConstraints.bool) {
                        MCClans.getPlugin().getLogger().warn("Could not load config option " + configOption.key + " (" + value + "): " + meetsConstraints.message, true);
                        // Only change runtime value
                        value = configOption.valueIfConstraintFailed;
                    }
                } else {
                    MCClans.getPlugin().getLogger().warn("Could not load config option " + configOption.key + " (" + value + "): needs to be a " + isOfType.message, true);
                    // Change runtime and config file value to the default
                    value = configOption.value;
                    subNode.setValue(configOption.value);
                }
                sConfig.put(configOption.key, value);
            }
        }
    }

    public static boolean getBoolean(String key) {
        Object value = sConfig.get(key);
        if (value == null || !(value instanceof Boolean)) {
            return false;
        } else {
            return (boolean) value;
        }
    }

    public static String getString(String key) {
        Object value = sConfig.get(key);
        if (value == null || !(value instanceof String)) {
            return "";
        } else {
            return (String) value;
        }
    }

    public static TextColor getColor(String key) {
        return Utils.getTextColorByName(getString(key), TextColors.DARK_PURPLE);
    }

    public static int getInteger(String key) {
        Object value = sConfig.get(key);
        if (value == null || !(value instanceof Integer)) {
            return 0;
        } else {
            return (int) value;
        }
    }

    public static double getDouble(String key) {
        Object value = sConfig.get(key);
        if (value == null) {
            return 0;
        }

        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof Integer) {
            return (int) value;
        } else {
            return 0;
        }
    }

    public static <T> List<T> getList(String key, Class<T> typeClazz) {
        List<T> list = new ArrayList<>();
        Object value = sConfig.get(key);

        if (value == null || !(value instanceof List)) {
            return list;
        } else {
            for (Object object : (List<?>) value) {
                if (typeClazz.isInstance(object)) {
                    list.add(typeClazz.cast(object));
                }
            }
            return list;
        }
    }

    public static <K, V> Map<K, V> getMap(String key, Class<K> keyClazz, Class<V> valueClazz) {
        Map<K, V> map = new HashMap<>();
        Object value = sConfig.get(key);

        if (value == null || !(value instanceof Map)) {
            return map;
        } else {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (keyClazz.isInstance(entry.getKey()) && valueClazz.isInstance(entry.getValue())) {
                    map.put(keyClazz.cast(entry.getKey()), valueClazz.cast(entry.getValue()));
                }
            }
            return map;
        }
    }

    public static void setValue(String key, Object value) {
        sConfig.put(key, value);
    }
}
