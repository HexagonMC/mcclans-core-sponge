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

package nl.riebie.mcclans.config.constraints;

import nl.riebie.mcclans.config.model.ConfigOption;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kippers on 09/01/2016.
 */
public class OneOfStringConstraint implements ConfigOption.Constraint {

    private boolean mCaseSensitive;
    private List<String> mPossibilities;

    public OneOfStringConstraint(boolean caseSensitive, String... possibilities) {
        this.mCaseSensitive = caseSensitive;
        this.mPossibilities = Arrays.asList(possibilities);
    }

    @Override
    public String getConstraintDescription() {
        String delimiter = ", ";

        StringBuilder sb = new StringBuilder();

        for (String s : mPossibilities) {
            sb.append(s).append(delimiter);
        }

        sb.deleteCharAt(sb.length() - delimiter.length());

        return "must be one of: " + sb.toString();
    }

    @Override
    public boolean meetsConstraint(Object value) {
        if (value instanceof String) {
            String valueString = (String) value;
            for (String possibility : mPossibilities) {
                boolean match = (mCaseSensitive) ? possibility.equals(valueString) : possibility.equalsIgnoreCase(valueString);
                if (match) {
                    return true;
                }
            }
        } else {
            return false;
        }

        return false;
    }
}
