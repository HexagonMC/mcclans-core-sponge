package nl.riebie.mcclans.commands.constraints.regex;

import nl.riebie.mcclans.config.Config;

/**
 * Created by Mirko on 14/02/2016.
 */
public enum  RegexConstraints implements RegexConstraint {
    EMPTY("", false),
    CLAN_TAG(Config.CLAN_TAG_REGEX, true),
    CLAN_NAME(Config.CLAN_NAME_REGEX, true);

    private String regex;

    RegexConstraints(String value, boolean fromConfig){
        if(fromConfig){
            this.regex = Config.getString(value);
        } else{
            this.regex = value;
        }
    }

    @Override
    public String getRegex() {
        return regex;
    }
}
