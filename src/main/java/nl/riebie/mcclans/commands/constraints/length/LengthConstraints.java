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

package nl.riebie.mcclans.commands.constraints.length;

import nl.riebie.mcclans.config.Config;

/**
 * Created by riebie on 14/02/2016.
 */
public enum LengthConstraints implements LengthConstraint {
    EMPTY(-1, -1),
    CLAN_NAME(Config.CLAN_NAME_CHARACTERS_MINIMUM, Config.CLAN_NAME_CHARACTERS_MAXIMUM),
    CLAN_TAG(Config.CLAN_TAG_CHARACTERS_MINIMUM, Config.CLAN_TAG_CHARACTERS_MAXIMUM);

    private int minimumLength;
    private int maximumLength;

    LengthConstraints(String minimumKey, String maximumKey) {
        minimumLength = Config.getInteger(minimumKey);
        maximumLength = Config.getInteger(maximumKey);
    }

    LengthConstraints(int minimumLength, int maximumLength) {
        this.minimumLength = minimumLength;
        this.maximumLength = maximumLength;
    }

    @Override
    public int getMinimalLength() {
        return minimumLength;
    }

    @Override
    public int getMaximalLength() {
        return maximumLength;
    }
}
