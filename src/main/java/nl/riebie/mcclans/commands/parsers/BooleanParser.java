package nl.riebie.mcclans.commands.parsers;


import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;

/**
 * Created by Mirko on 17/01/2016.
 */
public class BooleanParser implements ParameterParser<Boolean> {

    @Override
    public ParseResult<Boolean> parseValue(String value, NormalFilledParameter parameter) {
        boolean booleanValue = Boolean.parseBoolean(value);

        return ParseResult.newSuccessResult(booleanValue);
    }
}
