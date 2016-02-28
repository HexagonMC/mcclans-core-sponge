package nl.riebie.mcclans.commands.parsers;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;
import nl.riebie.mcclans.messages.Messages;
import org.spongepowered.api.text.Text;

/**
 * Created by Mirko on 28/02/2016.
 */
public class ClanParser implements ParameterParser<ClanImpl> {
    @Override
    public ParseResult<ClanImpl> parseValue(String value, NormalFilledParameter parameter) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(value);
        if (clan == null) {
            return ParseResult.newErrorResult(Messages.CLAN_DOES_NOT_EXIST);
        } else {
            return ParseResult.newSuccessResult(clan);
        }
    }
}
