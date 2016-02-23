package nl.riebie.mcclans.commands.parsers;


import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;

/**
 * Created by Mirko on 17/01/2016.
 */
public class PermissionParser implements ParameterParser<Permission> {

    @Override
    public ParseResult<Permission> parseValue(String value, NormalFilledParameter parameter) {
        if (Permission.contains(value)) {
            return ParseResult.newSuccessResult(Permission.valueOf(value));
        } else {
            return ParseResult.newErrorResult(String.format("%s is not a valid permission", value));
        }

    }
}
