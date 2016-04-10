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

package nl.riebie.mcclans.player;

import nl.riebie.mcclans.api.KillDeath;
import nl.riebie.mcclans.api.enums.KillDeathFactor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by riebie on 13/03/2016.
 */
public class KillDeathImpl implements KillDeath {

    private Map<KillDeathFactor, Integer> deathsMap = new HashMap<>();
    private Map<KillDeathFactor, Integer> killsMap = new HashMap<>();

    @Override
    public int getKills(KillDeathFactor factor) {
        Integer kills = killsMap.get(factor);
        return kills == null ? 0 : kills;
    }

    @Override
    public int getDeaths(KillDeathFactor factor) {
        Integer deaths = deathsMap.get(factor);
        return deaths == null ? 0 : deaths;
    }

    @Override
    public void setKills(KillDeathFactor factor, int kills) {
        killsMap.put(factor, kills);
    }

    @Override
    public void setDeaths(KillDeathFactor factor, int deaths) {
        deathsMap.put(factor, deaths);
    }

    @Override
    public void addKill(KillDeathFactor factor) {
        Integer kills = killsMap.get(factor);
        if (kills == null) {
            killsMap.put(factor, 1);
        } else {
            killsMap.put(factor, kills + 1);
        }
    }

    @Override
    public void addDeath(KillDeathFactor factor) {
        Integer deaths = deathsMap.get(factor);
        if (deaths == null) {
            deathsMap.put(factor, 1);
        } else {
            deathsMap.put(factor, deaths + 1);
        }
    }

    @Override
    public float getKillsWeighted(){
        float kills = 0;
        for (Map.Entry<KillDeathFactor, Integer> entry: killsMap.entrySet()) {
            kills += entry.getValue() * entry.getKey().getFactor();
        }
        return kills;
    }

    @Override
    public float getDeathsWeighted(){
        float deaths = 0;
        for (Map.Entry<KillDeathFactor, Integer> entry: deathsMap.entrySet()) {
            deaths += entry.getValue() * entry.getKey().getFactor();
        }
        return deaths;
    }

    @Override
    public int getTotalKills() {
        int kills = 0;
        for (int kill : killsMap.values()) {
            kills += kill;
        }
        return kills;
    }

    @Override
    public int getTotalDeaths() {
        int deaths = 0;
        for (int death : deathsMap.values()) {
            deaths += death;
        }
        return deaths;
    }

    @Override
    public float getKDR(){
        float kdr;
        float killsWeighted = getKillsWeighted();
        float deathsWeighted = getDeathsWeighted();
        if (deathsWeighted < 1) {
            deathsWeighted = 1;
        }
        kdr = killsWeighted / deathsWeighted;

        int ix = (int) (kdr * 10.0f); // scale it
        float dbl2 = ((float) ix) / 10.0f;
        return dbl2;
    }
}
