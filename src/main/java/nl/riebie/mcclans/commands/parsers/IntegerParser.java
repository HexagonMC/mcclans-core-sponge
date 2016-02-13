package nl.riebie.mcclans.commands.parsers;

import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;

/**
 * Created by Mirko on 17/01/2016.
 */
public class IntegerParser implements ParameterParser<Integer> {

    @Override
    public ParseResult<Integer> parseValue(String value, NormalFilledParameter parameter) {
        try {
            int intValue = Integer.parseInt(value);
            if (parameter.getMinimalLength() == -1 || intValue >= parameter.getMinimalLength()) {
                if (parameter.getMaximalLength() == -1 || intValue <= parameter.getMaximalLength()) {
                    return ParseResult.newSuccessResult(intValue);
                } else {
                    return ParseResult.newErrorResult(String.format("number supplied too high (%s/%s)", intValue, parameter.getMaximalLength()));
                }
            } else {
                return ParseResult.newErrorResult(String.format("number supplied too low (%s/%s)", intValue, parameter.getMinimalLength()));
            }
        } catch (NumberFormatException e) {
            return ParseResult.newErrorResult("Supplied parameter is not a Integer");
        }
    }
}
