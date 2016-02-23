package nl.riebie.mcclans.config.model;

import javax.annotation.Nullable;
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

    public ConfigSection(String key, @Nullable String comment, List<ConfigOption> configOptions) {
        this.key = key;
        this.comment = comment;
        this.configOptions = configOptions;
    }

    public boolean hasComment() {
        return comment != null && comment.length() != 0;
    }

    public static Builder builder(String key) {
        return new Builder(key);
    }

    public static class Builder {
        private String key;
        private String comment = "";
        private List<ConfigOption> configOptions = new ArrayList<>();

        private Builder(String key) {
            this.key = key;
        }

        public Builder setComment(@Nullable String comment) {
            this.comment = comment;
            return this;
        }

        public Builder setConfigOptions(List<ConfigOption> configOptions) {
            this.configOptions = configOptions;
            return this;
        }

        public Builder setConfigOptions(ConfigOption... configOptions) {
            this.configOptions = Arrays.asList(configOptions);
            return this;
        }

        public ConfigSection build() {
            return new ConfigSection(key, comment, configOptions);
        }
    }
}
