package nl.riebie.mcclans.config.model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

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

    public ConfigSection(@NotNull String key, @Nullable String comment, @NotNull List<ConfigOption> configOptions) {
        this.key = key;
        this.comment = comment;
        this.configOptions = configOptions;
    }

    public boolean hasComment() {
        return comment != null && comment.length() != 0;
    }

    public static Builder builder(@NotNull String key) {
        return new Builder(key);
    }

    public static class Builder {
        private String key;
        private String comment = "";
        private List<ConfigOption> configOptions = new ArrayList<>();

        private Builder(@NotNull String key) {
            this.key = key;
        }

        public Builder setComment(@Nullable String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setConfigOptions(@NotNull List<ConfigOption> configOptions) {
            this.configOptions = configOptions;
            return this;
        }

        public Builder setConfigOptions(@NotNull ConfigOption... configOptions) {
            this.configOptions = Arrays.asList(configOptions);
            return this;
        }

        public ConfigSection build() {
            return new ConfigSection(key, comment, configOptions);
        }
    }
}
