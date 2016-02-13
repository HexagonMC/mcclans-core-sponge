package nl.riebie.mcclans.commands.constraints.regex;

import nl.riebie.mcclans.config.Config;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ClanTagRegexConstraint extends ConfigRegexConstraint {
    public ClanTagRegexConstraint() {
        super(Config.CLAN_TAG_REGEX);
    }
}
