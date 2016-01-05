package nl.riebie.mcclans.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Koen on 22/12/2015.
 */
public class Config {

    public static final String DEBUGGING = "debugging";
    public static final String USE_PERMISSIONS = "use-permissions";
    public static final String SOME_STRING = "some-string";
    public static final String TELEPORT_DELAY_SECONDS = "teleport-delay-seconds";
    public static final String SOME_LIST = "some-list";

    private static List<ConfigSection> sConfigSections = new ArrayList<>();

    private static Map<String, Object> sConfig = new HashMap<>();

    static {
        ConfigSection generalConfigSection = new ConfigSection.Builder("general").withConfigOptions(
                new ConfigOption.Builder(DEBUGGING).withValue(false).build(),
                new ConfigOption.Builder(USE_PERMISSIONS).withValue(false).build(),
                new ConfigOption.Builder(SOME_STRING).withValue("someVALUEVBA").build(),
                new ConfigOption.Builder(TELEPORT_DELAY_SECONDS).withValue(5).build()
        ).build();

        ConfigSection listConfigSection = new ConfigSection.Builder("section-list-inside").withConfigOptions(
                new ConfigOption.Builder(SOME_LIST).withValue(Arrays.asList("entry1", "entry2")).build()
        ).build();


        sConfigSections.add(generalConfigSection);
        sConfigSections.add(listConfigSection);
    }

    public static void load(CommentedConfigurationNode rootNode) {
        putDefaultsAndGetValues(rootNode);
    }

    private static void putDefaultsAndGetValues(CommentedConfigurationNode rootNode) {
        for (ConfigSection configSection : sConfigSections) {
            CommentedConfigurationNode sectionNode = rootNode.getNode(configSection.key);
            if (sectionNode.isVirtual() && configSection.hasComment()) {
                sectionNode.setComment(configSection.comment);
            }
            for (ConfigOption configOption : configSection.configOptions) {
                CommentedConfigurationNode subNode = sectionNode.getNode(configOption.key);
                if (subNode.isVirtual()) {
                    subNode.setValue(configOption.value);
                }
                sConfig.put(configOption.key, subNode.getValue());
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

    public static int getInteger(String key) {
        Object value = sConfig.get(key);
        if (value == null || !(value instanceof Integer)) {
            return 0;
        } else {
            return (int) value;
        }
    }

    public static <T> List<T> getList(String key, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        Object value = sConfig.get(key);

        if (value == null || !(value instanceof List)) {
            return list;
        } else {
            for (Object object : (List<?>) value) {
                if (clazz.isInstance(object)) {
                    list.add(clazz.cast(object));
                }
            }
            return list;
        }
    }
}
