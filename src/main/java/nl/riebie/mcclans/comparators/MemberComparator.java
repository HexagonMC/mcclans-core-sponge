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

import nl.riebie.mcclans.player.ClanPlayerImpl;

import java.util.Comparator;

/**
 * Created by Kippers on 23-2-2016.
 */
public class MemberComparator implements Comparator<ClanPlayerImpl> {

    @Override
    public int compare(ClanPlayerImpl player, ClanPlayerImpl player2) {
        if (player.getRank().getImportance() < player2.getRank().getImportance()) {
            return 1;
        } else if (player.getRank().getImportance() > player2.getRank().getImportance()) {
            return -1;
        }

        double player1Kdr = player.getKillDeath().getKDR();
        double player2Kdr = player2.getKillDeath().getKDR();
        if (player1Kdr < player2Kdr) {
            return 1;
        } else if (player1Kdr > player2Kdr) {
            return -1;
        }
        return 0;
    }

}
