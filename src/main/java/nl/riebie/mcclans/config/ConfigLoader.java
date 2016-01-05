package nl.riebie.mcclans.config;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Koen on 22/12/2015.
 */
public class ConfigLoader {

    private static List<ConfigSection> sConfigSections = new ArrayList<>();

    static {
        ConfigSection generalConfigSection = new ConfigSection.Builder("general").withConfigOptions(
                new ConfigOption.Builder("debugging").withValue(false).build(),
                new ConfigOption.Builder("use-permissions").withValue(false).build(),
                new ConfigOption.Builder("teleport-delay-seconds").withValue(5).build()
        ).build();

        ConfigSection listConfigSection = new ConfigSection.Builder("some-list").withConfigOptions(
                new ConfigOption.Builder("list").withValue(Arrays.asList("entry1", "entry2")).build()
        ).build();


        sConfigSections.add(generalConfigSection);
        sConfigSections.add(listConfigSection);
    }

    public static void load(CommentedConfigurationNode rootNode) {
        putDefaultValues(rootNode);
        getValues(rootNode);
    }

    private static void putDefaultValues(CommentedConfigurationNode rootNode) {

    }

    private static void getValues(CommentedConfigurationNode rootNode) {

    }

}
