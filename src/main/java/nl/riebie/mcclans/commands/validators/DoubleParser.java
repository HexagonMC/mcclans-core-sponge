package nl.riebie.mcclans.commands.validators;

import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;

/**
 * Created by Mirko on 17/01/2016.
 */
public class DoubleParser implements ParameterParser<Double> {

    @Override
    public ParseResult<Double> parseValue(String value, NormalFilledParameter parameter) {
        try {
            double doubleValue = Double.parseDouble(value);
            if (parameter.getMinimalLength() == -1 || doubleValue >= parameter.getMinimalLength()) {
                if (parameter.getMaximalLength() == -1 || doubleValue <= parameter.getMaximalLength()) {
                    return ParseResult.newSuccessResult(doubleValue);
                } else {
                    return ParseResult.newErrorResult(String.format("number supplied too high (%s/%s)", doubleValue, parameter.getMaximalLength()));
                }
            } else {
                return ParseResult.newErrorResult(String.format("number supplied too low (%s/%s)", doubleValue, parameter.getMinimalLength()));
            }
        } catch (NumberFormatException e) {
            return ParseResult.newErrorResult("Supplied parameter is not a Double");
        }
    }
}
