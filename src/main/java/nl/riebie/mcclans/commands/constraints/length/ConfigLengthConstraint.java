package nl.riebie.mcclans.commands.constraints.length;

import nl.riebie.mcclans.config.Config;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ConfigLengthConstraint implements LengthConstraint {

    private final int minimalLength;
    private final int maximalLength;

    public ConfigLengthConstraint(String minimalConfigName, String maximumConfigName) {
        this.minimalLength =  Config.getInteger(minimalConfigName);
        this.maximalLength = Config.getInteger(maximumConfigName);
    }

    @Override
    public int getMinimalLength() {
        return minimalLength;
    }

    @Override
    public int getMaximalLength() {
        return maximalLength;
    }
}
