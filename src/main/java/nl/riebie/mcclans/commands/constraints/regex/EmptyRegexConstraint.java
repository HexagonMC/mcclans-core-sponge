package nl.riebie.mcclans.commands.constraints.regex;

/**
 * Created by Mirko on 13/02/2016.
 */
public class EmptyRegexConstraint implements RegexConstraint{

    @Override
    public String getRegex(){
        return null;
    }
}
