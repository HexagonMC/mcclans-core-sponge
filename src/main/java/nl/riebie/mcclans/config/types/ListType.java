package nl.riebie.mcclans.config.types;

import nl.riebie.mcclans.config.model.ConfigOption;

import java.util.List;

/**
 * Created by Koen on 09/01/2016.
 */
public class ListType extends ConfigOption.Type {

    @Override
    public boolean isOfType(Object value) {
        if (value == null || !(value instanceof List)) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String getTypeDescription() {
        return "list (example: list-key=[\"entry1\", \"entry2\" ] )";
    }
}
