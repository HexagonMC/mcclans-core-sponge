package nl.riebie.mcclans.config.types;

import nl.riebie.mcclans.config.model.ConfigOption;

/**
 * Created by Koen on 09/01/2016.
 */
public class DoubleType extends ConfigOption.Type {

    @Override
    public boolean isOfType(Object value) {
        if (value == null || !(value instanceof Double)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getTypeDescription() {
        return "decimal number (0.1, 0.2, etc.)";
    }
}
