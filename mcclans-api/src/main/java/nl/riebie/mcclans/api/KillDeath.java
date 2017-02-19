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
 * Holds the kills and deaths of a player. Contains convenience methods for adding kills and deaths with their weight factor.
 * <p>
 * Created by riebie on 10/04/2016.
 */
public interface KillDeath {

    /**
     * Get the kills for the provided KillDeathFactor
     *
     * @param factor the KillDeathFactor to get the kills for
     * @return the kills for the provided KillDeathFactor
     */
    int getKills(KillDeathFactor factor);

    /**
     * Get the deaths for the provided KillDeathFactor
     *
     * @param factor the KillDeathFactor to get the deaths for
     * @return the deaths for the provided KillDeathFactor
     */
    int getDeaths(KillDeathFactor factor);

    /**
     * Set the kills for the provided KillDeathFactor
     *
     * @param factor the KillDeathFactor to set the kills for
     * @param kills  the kills for the provided KillDeathFactor
     */
    void setKills(KillDeathFactor factor, int kills);

    /**
     * Set the deaths for the provided KillDeathFactor
     *
     * @param factor the KillDeathFactor to set the deaths for
     * @param deaths the deaths for the provided KillDeathFactor
     */
    void setDeaths(KillDeathFactor factor, int deaths);

    /**
     * Add a kill for the provided KillDeathFactor
     *
     * @param factor the KillDeathFactor to add a kill for
     */
    void addKill(KillDeathFactor factor);

    /**
     * Add a death for the provided KillDeathFactor
     *
     * @param factor the KillDeathFactor to add a death for
     */
    void addDeath(KillDeathFactor factor);

    /**
     * Get the weighted kills
     *
     * @return the weighted kills
     */
    float getKillsWeighted();

    /**
     * Get the weighted deaths
     *
     * @return the weighted deaths
     */
    float getDeathsWeighted();

    /**
     * Get the total kills
     *
     * @return the total kills
     */
    int getTotalKills();

    /**
     * Get the total deaths
     *
     * @return the total deaths
     */
    int getTotalDeaths();

    /**
     * Get the weighted Kill/Death ratio
     *
     * @return the weighted Kill/Death ratio
     */
    float getKDR();
}
