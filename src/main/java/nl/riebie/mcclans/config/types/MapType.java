package nl.riebie.mcclans.config.types;

import nl.riebie.mcclans.config.model.ConfigOption;

import java.util.Map;

/**
 * Created by Koen on 09/01/2016.
 */
public class MapType extends ConfigOption.Type {

    @Override
    public boolean isOfType(Object value) {
        if (value == null || !(value instanceof Map)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getTypeDescription() {
        return "map (example: map-key { \"key\"=\"value\" } )";
    }
}
