package nl.riebie.mcclans.commands.constraints.length;

import nl.riebie.mcclans.config.Config;

/**
 * Created by Mirko on 14/02/2016.
 */
public enum LengthConstraints implements LengthConstraint {
    EMPTY(-1, -1),
    CLAN_NAME(Config.CLAN_NAME_CHARACTERS_MINIMUM, Config.CLAN_NAME_CHARACTERS_MAXIMUM),
    CLAN_TAG(Config.CLAN_TAG_CHARACTERS_MINIMUM, Config.CLAN_TAG_CHARACTERS_MAXIMUM);

    private int minimumLength;
    private int maximumLength;

    LengthConstraints(String minimumKey, String maximumKey) {
        minimumLength = Config.getInteger(minimumKey);
        maximumLength = Config.getInteger(maximumKey);
    }

    LengthConstraints(int minimumLength, int maximumLength) {
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
    }

    @Override
    public int getMinimalLength() {
        return minimumLength;
    }

    @Override
    public int getMaximalLength() {
        return maximumLength;
    }
}
