package nl.riebie.mcclans.config.constraints;

import nl.riebie.mcclans.config.model.ConfigOption;

/**
 * Created by Koen on 09/01/2016.
 */
public class MinimumNumberConstraint implements ConfigOption.Constraint {

    private Integer mMinInt;
    private Double mMinDouble;

    public MinimumNumberConstraint(int minInt) {
        this.mMinInt = minInt;
    }

    public MinimumNumberConstraint(double minDouble) {
        this.mMinDouble = minDouble;
    }

    @Override
    public String getConstraintDescription() {
        return "minimum of " + String.valueOf((mMinInt) == null ? mMinDouble : mMinInt);
    }

    @Override
    public boolean meetsConstraint(Object value) {
        if (value instanceof Integer) {
            return isAboveMinimum((int) value);
        } else if (value instanceof Double) {
            return isAboveMinimum((double) value);
        } else {
            return false;
        }
    }

    public boolean isAboveMinimum(int value) {
        if (mMinInt != null) {
            return value >= mMinInt;
        } else if (mMinDouble != null) {
            return value >= mMinDouble;
        } else {
            return false;
        }
    }

    public boolean isAboveMinimum(double value) {
        if (mMinInt != null) {
            return value >= mMinInt;
        } else if (mMinDouble != null) {
            return value >= mMinDouble;
        } else {
            return false;
        }
    }
}
