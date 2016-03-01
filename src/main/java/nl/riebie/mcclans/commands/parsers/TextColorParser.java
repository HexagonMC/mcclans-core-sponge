package nl.riebie.mcclans.commands.parsers;

import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

/**
 * Created by K.Volkers on 1-3-2016.
 */
public class TextColorParser implements ParameterParser<TextColor> {
    @Override
    public ParseResult<TextColor> parseValue(String value, NormalFilledParameter parameter) {
        TextColor textColor = Utils.getTextColorByName(value, null);
        if (textColor == null) {
            return ParseResult.newErrorResult(Messages.THIS_IS_NOT_A_VALID_COLOR);
        } else {
            return ParseResult.newSuccessResult(textColor);
        }
    }
}
