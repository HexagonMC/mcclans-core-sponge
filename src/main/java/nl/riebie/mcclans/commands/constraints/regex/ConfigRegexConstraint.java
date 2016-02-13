package nl.riebie.mcclans.commands.constraints.regex;

import nl.riebie.mcclans.config.Config;

/**
 * Created by Mirko on 13/02/2016.
 */
public class ConfigRegexConstraint implements RegexConstraint {

    private String regex;

    public ConfigRegexConstraint(String configRegexKey) {
        regex = Config.getString(configRegexKey);
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
