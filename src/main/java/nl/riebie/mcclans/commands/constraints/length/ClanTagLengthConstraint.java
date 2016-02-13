package nl.riebie.mcclans.commands.constraints.length;

import nl.riebie.mcclans.config.Config;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ClanTagLengthConstraint extends ConfigLengthConstraint {
    public ClanTagLengthConstraint() {
        super(Config.CLAN_TAG_CHARACTERS_MINIMUM, Config.CLAN_TAG_CHARACTERS_MAXIMUM);
    }
}
