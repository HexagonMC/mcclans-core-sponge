package nl.riebie.mcclans.config.types;

import nl.riebie.mcclans.config.model.ConfigOption;

/**
 * Created by Koen on 09/01/2016.
 */
public class UnknownType extends ConfigOption.Type {

    @Override
    public boolean isOfType(Object value) {
        return false;
    }

    @Override
    public String getTypeDescription() {
        return "UNKNOWN TYPE. Please report this issue. Provide your config and the config option key this error triggered for!";
    }

}
