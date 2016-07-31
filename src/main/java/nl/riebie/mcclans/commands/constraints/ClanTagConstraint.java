package nl.riebie.mcclans.commands.constraints;

import nl.riebie.mcclans.config.Config;

/**
 * Created by riebie on 26/06/2016.
 */
public class ClanTagConstraint extends ParameterConstraint {
    private final String regex;
    private final int minimumLength;
    private final int maximumLength;

    public ClanTagConstraint(){
        regex = Config.getString(Config.CLAN_TAG_REGEX);
        minimumLength = Config.getInteger(Config.CLAN_TAG_CHARACTERS_MINIMUM);
        maximumLength = Config.getInteger(Config.CLAN_TAG_CHARACTERS_MAXIMUM);
    }

    @Override
    public int getMinimalLength() {
        return minimumLength;
    }

    @Override
    public int getMaximalLength() {
        return maximumLength;
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
