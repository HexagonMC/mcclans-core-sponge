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

import nl.riebie.mcclans.commands.constraints.ParameterConstraint;
import nl.riebie.mcclans.commands.filledparameters.NormalFilledParameter;
import org.spongepowered.api.command.CommandSource;

/**
 * Created by riebie on 17/01/2016.
 */
public class FloatParser implements ParameterParser<Float> {

    @Override
    public ParseResult<Float> parseValue(CommandSource commandSource, String value, NormalFilledParameter parameter) {
        try {
            float floatValue = Float.parseFloat(value);
            ParameterConstraint constraint = parameter.getConstraint();
            if (constraint.getMinimalLength() == -1 || floatValue >= constraint.getMinimalLength()) {
                if (constraint.getMaximalLength() == -1 || floatValue <= constraint.getMaximalLength()) {
                    return ParseResult.newSuccessResult(floatValue);
                } else {
                    return ParseResult.newErrorResult(String.format("The supplied parameter is too high (%s/%s)", floatValue, constraint.getMaximalLength()));
                }
            } else {
                return ParseResult.newErrorResult(String.format("The supplied parameter is too low (%s/%s)", floatValue, constraint.getMinimalLength()));
            }
        } catch (NumberFormatException e) {
            return ParseResult.newErrorResult("The supplied parameter is not a decimal number");
        }
    }

    @Override
    public String getDescription() {
        return "decimal number";
    }
}
