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

package nl.riebie.mcclans.commands;

import java.util.Arrays;
import java.util.List;

/**
 * Created by riebie on 26/02/2016.
 */
public class Toggle {

    private ToggleType type;

    public Toggle(ToggleType type) {
        this.type = type;
    }

    public boolean getBoolean(boolean previousValue) {
        return type == ToggleType.TOGGLE ? !previousValue : type == ToggleType.TRUE;
    }

    public enum ToggleType {
        TRUE("true", "on"), FALSE("false", "off"), TOGGLE("toggle");

        private List<String> values;

        ToggleType(String... values) {
            this.values = Arrays.asList(values);
        }

        public static ToggleType ofString(String value) {
            String lowerCaseValue = value.toLowerCase();
            for (ToggleType toggleType : values()) {
                if (toggleType.values.contains(lowerCaseValue)) {
                    return toggleType;
                }
            }
            return null;
        }

        public static String getPossibleParameterString() {
            StringBuilder stringBuilder = new StringBuilder();
            boolean first = true;
            for (ToggleType toggleType : values()) {
                for (String value : toggleType.values) {
                    if (!first) {
                        stringBuilder.append(", ");
                    }
                    first = false;
                    stringBuilder.append(value);
                }
            }
            return stringBuilder.toString();
        }

    }
}
