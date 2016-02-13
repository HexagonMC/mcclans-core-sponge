package nl.riebie.mcclans.commands.validators;

import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;

/**
 * Created by Mirko on 17/01/2016.
 */
public class FloatParser implements ParameterParser<Float> {

    @Override
    public ParseResult<Float> parseValue(String value, NormalFilledParameter parameter) {
        try {
            float floatValue = Float.parseFloat(value);
            if (parameter.getMinimalLength() == -1 || floatValue >= parameter.getMinimalLength()) {
                if (parameter.getMaximalLength() == -1 || floatValue <= parameter.getMaximalLength()) {
                    return ParseResult.newSuccessResult(floatValue);
                } else {
                    return ParseResult.newErrorResult(String.format("number supplied to high (%s/%s)", floatValue, parameter.getMaximalLength()));
                }
            } else {
                return ParseResult.newErrorResult(String.format("number supplied to low (%s/%s)", floatValue, parameter.getMinimalLength()));
            }
        } catch (NumberFormatException e) {
            return ParseResult.newErrorResult("Supplied parameter is not a Float");
        }
    }
}
