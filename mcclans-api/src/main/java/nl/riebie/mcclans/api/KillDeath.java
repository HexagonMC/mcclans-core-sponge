/*
 *  Copyright (c) 2016 riebie, Kippers < https://bitbucket.org/Kippers/mcclans-core-sponge>
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
 *
 */

package nl.riebie.mcclans.api;

import nl.riebie.mcclans.api.enums.KillDeathFactor;

/**
 * Created by riebie on 10/04/2016.
 */
public interface KillDeath {
    int getKills(KillDeathFactor factor);

    int getDeaths(KillDeathFactor factor);

    void setKills(KillDeathFactor factor, int kills);

    void setDeaths(KillDeathFactor factor, int deaths);

    void addKill(KillDeathFactor factor);

    void addDeath(KillDeathFactor factor);

    float getKillsWeighted();

    float getDeathsWeighted();

    int getTotalKills();

    int getTotalDeaths();

    float getKDR();
}
