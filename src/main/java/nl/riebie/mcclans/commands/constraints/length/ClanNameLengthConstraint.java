package nl.riebie.mcclans.commands.constraints.length;

import nl.riebie.mcclans.config.Config;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ClanNameLengthConstraint extends ConfigLengthConstraint {
    public ClanNameLengthConstraint() {
        super(Config.CLAN_NAME_CHARACTERS_MINIMUM, Config.CLAN_NAME_CHARACTERS_MAXIMUM);
    }
}
