package nl.riebie.mcclans.config;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Koen on 22/12/2015.
 */
public class ConfigSection {

    public String key;
    public String comment;
    public List<ConfigOption> configOptions;

    public ConfigSection(@NotNull String key, @NotNull String comment, @NotNull List<ConfigOption> configOptions) {
        this.key = key;
        this.comment = comment;
        this.configOptions = configOptions;
    }

    public boolean hasComment() {
        return comment != null && comment.length() != 0;
    }


    public static class Builder {
        private String key;
        private String comment = "";
        private List<ConfigOption> configOptions = new ArrayList<>();

        public Builder(@NotNull String key) {
            this.key = key;
        }

        public Builder withComment(@NotNull String comment) {
            this.comment = comment;
            return this;
        }

        public Builder withConfigOptions(@NotNull List<ConfigOption> configOptions) {
            this.configOptions = configOptions;
            return this;
        }

        public Builder withConfigOptions(@NotNull ConfigOption... configOptions) {
            this.configOptions = Arrays.asList(configOptions);
            return this;
        }

        public ConfigSection build() {
            return new ConfigSection(key, comment, configOptions);
        }
    }
}
