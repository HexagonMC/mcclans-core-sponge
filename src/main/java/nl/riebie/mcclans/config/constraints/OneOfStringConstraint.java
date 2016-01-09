package nl.riebie.mcclans.config.constraints;

import nl.riebie.mcclans.config.model.ConfigOption;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Koen on 09/01/2016.
 */
public class OneOfStringConstraint implements ConfigOption.Constraint {

    private boolean mCaseSensitive;
    private List<String> mPossibilities;

    public OneOfStringConstraint(boolean caseSensitive, String... possibilities) {
        this.mCaseSensitive = caseSensitive;
        this.mPossibilities = Arrays.asList(possibilities);
    }

    @Override
    public String getConstraintDescription() {
        String delimiter = ", ";

        StringBuilder sb = new StringBuilder();

        for (String s : mPossibilities) {
            sb.append(s).append(delimiter);
        }

        sb.deleteCharAt(sb.length() - delimiter.length());

        return "must be one of: " + sb.toString();
    }

    @Override
    public boolean meetsConstraint(Object value) {
        if (value instanceof String) {
            String valueString = (String) value;
            for (String possibility : mPossibilities) {
                boolean match = (mCaseSensitive) ? possibility.equals(valueString) : possibility.equalsIgnoreCase(valueString);
                if (match) {
                    return true;
                }
            }
        } else {
            return false;
        }

        return false;
    }
}
