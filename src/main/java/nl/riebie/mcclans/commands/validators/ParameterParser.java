package nl.riebie.mcclans.commands.validators;

import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;

/**
 * Created by Mirko on 17/01/2016.
 */
public interface ParameterParser<T> {
    ParseResult<T> parseValue(String value, NormalFilledParameter parameter);
}
