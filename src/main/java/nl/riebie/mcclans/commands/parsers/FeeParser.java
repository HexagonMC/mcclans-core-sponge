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

import nl.riebie.mcclans.commands.Fee;
import nl.riebie.mcclans.commands.filledparameters.NormalFilledParameter;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by Kippers on 18/04/2017.
 */
public class FeeParser implements ParameterParser<Fee> {

    @Override
    public ParseResult<Fee> parseValue(CommandSource commandSource, String value, NormalFilledParameter parameter) {
        if ("share".equals(value)) {
            return ParseResult.newSuccessResult(new Fee(-1));
        } else {
            ParseResult<Double> parseResult = new DoubleParser().parseValue(commandSource, value, parameter);
            if (parseResult.isSuccess()) {
                return ParseResult.newSuccessResult(new Fee(parseResult.getItem()));
            } else {
                return ParseResult.newErrorResult(parseResult.getErrorMessage());
            }
        }
    }

    @Override
    public String getDescription() {
        return "value (share) or number";
    }
}
