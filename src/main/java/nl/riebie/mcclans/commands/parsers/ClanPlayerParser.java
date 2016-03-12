package nl.riebie.mcclans.commands.parsers;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.commands.FilledParameters.NormalFilledParameter;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;

/**
 * Created by Mirko on 12/03/2016.
 */
public class ClanPlayerParser implements ParameterParser<ClanPlayer> {
    @Override
    public ParseResult<ClanPlayer> parseValue(String value, NormalFilledParameter parameter) {
        ClanPlayerImpl clanPlayer = ClansImpl.getInstance().getClanPlayer(value);
        if (clanPlayer != null) {
            return ParseResult.newSuccessResult(clanPlayer);
        } else {
            return ParseResult.newErrorResult(Messages.PLAYER_DOES_NOT_EXIST);
        }
    }
}
