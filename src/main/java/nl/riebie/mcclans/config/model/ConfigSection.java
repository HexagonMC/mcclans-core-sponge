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

package nl.riebie.mcclans.config.model;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kippers on 22/12/2015.
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
