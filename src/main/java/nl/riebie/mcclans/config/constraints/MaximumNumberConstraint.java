package nl.riebie.mcclans.config.constraints;

import nl.riebie.mcclans.config.model.ConfigOption;

/**
 * Created by Koen on 09/01/2016.
 */
public class MaximumNumberConstraint implements ConfigOption.Constraint {

    private Integer mMaxInt;
    private Double mMaxDouble;

    public MaximumNumberConstraint(int maxInt) {
        this.mMaxInt = maxInt;
    }

    public MaximumNumberConstraint(double maxDouble) {
        this.mMaxDouble = maxDouble;
    }

    @Override
    public boolean meetsConstraint(Object value) {
        if (value instanceof Integer) {
            return isBelowMaximum((int) value);
        } else if (value instanceof Double) {
            return isBelowMaximum((double) value);
        } else {
            return false;
        }
    }

    @Override
    public String getConstraintDescription() {
        return "maximum of " + String.valueOf((mMaxInt) == null ? mMaxDouble : mMaxInt);
    }

    public boolean isBelowMaximum(int value) {
        if (mMaxInt != null) {
            return value <= mMaxInt;
        } else if (mMaxDouble != null) {
            return value <= mMaxDouble;
        } else {
            return false;
        }
    }

    public boolean isBelowMaximum(double value) {
        if (mMaxInt != null) {
            return value <= mMaxInt;
        } else if (mMaxDouble != null) {
            return value <= mMaxDouble;
        } else {
            return false;
        }
    }
}
