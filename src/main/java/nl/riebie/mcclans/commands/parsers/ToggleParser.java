package nl.riebie.mcclans.commands.parsers;

import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;
import nl.riebie.mcclans.commands.Toggle;

/**
 * Created by Mirko on 26/02/2016.
 */
public class ToggleParser implements ParameterParser<Toggle> {
    @Override
    public ParseResult<Toggle> parseValue(String value, NormalFilledParameter parameter) {
        Toggle.ToggleType type = Toggle.ToggleType.ofString(value);
        if (type == null) {
            return ParseResult.newErrorResult("A toggleable parameter should be one of: %s", Toggle.ToggleType.getPossibleParameterString());
        } else {
            return ParseResult.newSuccessResult(new Toggle(type));
        }
    }
}
