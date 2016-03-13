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

package nl.riebie.mcclans.player;

import nl.riebie.mcclans.api.enums.KillDeathFactor;

/**
 * Created by Kippers on 19-1-2016.
 */
public class KillDeathFactorHandler {

    private static KillDeathFactorHandler killDeathFactorHandler;

    private final int lowMediumCutoff = 1;
    private final int mediumHighCutoff = 3;

    public static KillDeathFactorHandler getInstance() {
        if (killDeathFactorHandler == null) {
            killDeathFactorHandler = new KillDeathFactorHandler();
        }
        return killDeathFactorHandler;
    }

    public KillDeathFactor getKillFactor(double killerKdr, double victimKdr) {
        if (killerKdr < lowMediumCutoff) {
            if (victimKdr < lowMediumCutoff) {
                return KillDeathFactor.MEDIUM;
            } else if (victimKdr >= lowMediumCutoff && victimKdr < mediumHighCutoff) {
                return KillDeathFactor.MEDIUM;
            } else {
                return KillDeathFactor.HIGH;
            }
        } else if (killerKdr >= lowMediumCutoff && killerKdr < mediumHighCutoff) {
            if (victimKdr < lowMediumCutoff) {
                return KillDeathFactor.LOW;
            } else if (victimKdr >= lowMediumCutoff && victimKdr < mediumHighCutoff) {
                return KillDeathFactor.MEDIUM;
            } else {
                return KillDeathFactor.HIGH;
            }
        } else {
            if (victimKdr < lowMediumCutoff) {
                return KillDeathFactor.LOW;
            } else if (victimKdr >= lowMediumCutoff && victimKdr < mediumHighCutoff) {
                return KillDeathFactor.MEDIUM;
            } else {
                return KillDeathFactor.MEDIUM;
            }
        }
    }

    public KillDeathFactor getDeathFactor(double killerKdr, double victimKdr) {
        if (killerKdr < lowMediumCutoff) {
            if (victimKdr < lowMediumCutoff) {
                return KillDeathFactor.MEDIUM;
            } else if (victimKdr >= lowMediumCutoff && victimKdr < mediumHighCutoff) {
                return KillDeathFactor.MEDIUM;
            } else {
                return KillDeathFactor.HIGH;
            }
        } else if (killerKdr >= lowMediumCutoff && killerKdr < mediumHighCutoff) {
            if (victimKdr < lowMediumCutoff) {
                return KillDeathFactor.LOW;
            } else if (victimKdr >= lowMediumCutoff && victimKdr < mediumHighCutoff) {
                return KillDeathFactor.MEDIUM;
            } else {
                return KillDeathFactor.MEDIUM;
            }
        } else {
            if (victimKdr < lowMediumCutoff) {
                return KillDeathFactor.LOW;
            } else if (victimKdr >= lowMediumCutoff && victimKdr < mediumHighCutoff) {
                return KillDeathFactor.MEDIUM;
            } else {
                return KillDeathFactor.MEDIUM;
            }
        }
    }

    public KillDeathFactor getKillFactor(ClanPlayerImpl killer, ClanPlayerImpl victim) {
        double killerKdr = killer.getKillDeath().getKDR();
        double victimKdr = victim.getKillDeath().getKDR();
        return getKillFactor(killerKdr, victimKdr);
    }

    public KillDeathFactor getDeathFactor(ClanPlayerImpl killer, ClanPlayerImpl victim) {
        double killerKdr = killer.getKillDeath().getKDR();
        double victimKdr = victim.getKillDeath().getKDR();
        return getDeathFactor(killerKdr, victimKdr);
    }

}
