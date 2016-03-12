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

package nl.riebie.mcclans.comparators;

import nl.riebie.mcclans.clan.ClanImpl;

import java.util.Comparator;

/**
 * Created by Kippers on 19-1-2016.
 */
public class ClanKdrComparator implements Comparator<ClanImpl> {

    @Override
    public int compare(ClanImpl clan1, ClanImpl clan2) {
        double clan1KDR = clan1.getKDR();
        double clan2KDR = clan2.getKDR();
        if (clan1KDR < clan2KDR) {
            return 1;
        } else if (clan1KDR > clan2KDR) {
            return -1;
        }

        long clan1CreationTime = clan1.getCreationDate().getTime();
        long clan2CreationTime = clan2.getCreationDate().getTime();
        if (clan1CreationTime < clan2CreationTime) {
            return -1;
        } else if (clan1CreationTime > clan2CreationTime) {
            return 1;
        }
        return 0;
    }

}
