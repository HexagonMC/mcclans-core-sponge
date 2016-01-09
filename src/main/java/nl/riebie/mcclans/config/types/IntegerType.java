package nl.riebie.mcclans.config.types;

import nl.riebie.mcclans.config.model.ConfigOption;

/**
 * Created by Koen on 09/01/2016.
 */
public class IntegerType extends ConfigOption.Type {

    @Override
    public boolean isOfType(Object value) {
        if (value == null || !(value instanceof Integer)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getTypeDescription() {
        return "number (1, 2, etc.)";
    }
}
