/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.commands.parsers;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.filledparameters.NormalFilledParameter;
import nl.riebie.mcclans.messages.Messages;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by riebie on 28/02/2016.
 */
public class ClanParser implements ParameterParser<ClanImpl> {
    @Override
    public ParseResult<ClanImpl> parseValue(CommandSource commandSource, String value, NormalFilledParameter parameter) {
        ClansImpl clansImpl = ClansImpl.getInstance();
        ClanImpl clan = clansImpl.getClan(value);
        if (clan == null) {
            return ParseResult.newErrorResult(Messages.CLAN_DOES_NOT_EXIST);
        } else {
            return ParseResult.newSuccessResult(clan);
        }
    }

    @Override
    public String getDescription() {
        return "clan tag";
    }
}
