package nl.riebie.mcclans.config;

import com.sun.istack.internal.NotNull;

/**
 * Created by Koen on 22/12/2015.
 */
public class ConfigOption<T> {

    public String key;
    public String comment;
    public Object value;

    public ConfigOption(@NotNull String key, @NotNull String comment, @NotNull Object value) {
        this.key = key;
        this.comment = comment;
        this.value = value;
    }


    public static class Builder {
        private String key;
        private String comment;
        private Object value;

        public Builder(String key) {
            this.key = key;
        }

        public Builder withComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder withValue(Object value) {
            this.value = value;
            return this;
        }

        public ConfigOption build() {
            return new ConfigOption(key, comment, value);
        }
    }
}
