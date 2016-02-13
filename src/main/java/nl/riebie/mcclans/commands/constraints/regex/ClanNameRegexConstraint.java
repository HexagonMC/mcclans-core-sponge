package nl.riebie.mcclans.commands.constraints.regex;

import nl.riebie.mcclans.config.Config;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ClanNameRegexConstraint extends ConfigRegexConstraint {
    public ClanNameRegexConstraint() {
        super(Config.CLAN_NAME_REGEX);
    }
}
