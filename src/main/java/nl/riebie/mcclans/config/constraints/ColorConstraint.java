package nl.riebie.mcclans.config.constraints;

import nl.riebie.mcclans.config.model.ConfigOption;
import nl.riebie.mcclans.utils.Utils;

/**
 * Created by K.Volkers on 7-6-2016.
 */
public class ColorConstraint implements ConfigOption.Constraint {

    @Override
    public boolean meetsConstraint(Object value) {
        if (value instanceof String) {
            return Utils.getTextColorByName((String) value, null) != null;
        } else {
            return false;
        }
    }

    @Override
    public String getConstraintDescription() {
        return "must be a color";
    }
}
