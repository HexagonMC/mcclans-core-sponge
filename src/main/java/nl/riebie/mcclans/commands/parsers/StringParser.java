package nl.riebie.mcclans.commands.parsers;

import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;

/**
 * Created by Mirko on 17/01/2016.
 */
public class StringParser implements ParameterParser<String> {

    @Override
    public ParseResult<String> parseValue(String value, NormalFilledParameter parameter) {
        if (parameter.getMaximalLength() == -1 || value.length() <= parameter.getMaximalLength()) {
            if (parameter.getMinimalLength() == -1 || value.length() >= parameter.getMinimalLength()) {
                if(parameter.getRegex() == null || value.matches(parameter.getRegex())) {
                    return ParseResult.newSuccessResult(value);
                } else{
                    return ParseResult.newErrorResult(String.format("Value should (%s)", parameter.getRegex()));
                }
            } else {
                return ParseResult.newErrorResult("Supplied parameter too small");
            }
        } else {
            return ParseResult.newErrorResult("Supplied parameter too long");
        }
    }
}
