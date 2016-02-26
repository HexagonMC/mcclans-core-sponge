package nl.riebie.mcclans.commands;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mirko on 26/02/2016.
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
