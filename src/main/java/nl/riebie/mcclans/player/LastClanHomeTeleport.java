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

import nl.riebie.mcclans.config.Config;

import javax.security.auth.login.Configuration;

/**
 * Created by Kippers on 19-1-2016.
 */
public class LastClanHomeTeleport {

    private long lastClanHomeTeleport;

    public LastClanHomeTeleport() {
        lastClanHomeTeleport = System.currentTimeMillis();
    }

    public boolean canPlayerTeleport() {
        int teleportCooldownSeconds = Config.getInteger(Config.TELEPORT_COOLDOWN_SECONDS);
        long timeDifferenceSeconds = (System.currentTimeMillis() - lastClanHomeTeleport) / 1000;
        if (timeDifferenceSeconds > teleportCooldownSeconds) {
            return true;
        } else {
            return false;
        }
    }

    public int secondsBeforePlayerCanTeleport() {
        int teleportCooldownSeconds = Config.getInteger(Config.TELEPORT_COOLDOWN_SECONDS);
        long timeDifferenceSeconds = (System.currentTimeMillis() - lastClanHomeTeleport) / 1000;
        long timeBeforePlayerCanTeleportInSeconds = teleportCooldownSeconds - timeDifferenceSeconds;
        if (timeBeforePlayerCanTeleportInSeconds > 0) {
            return (int) timeBeforePlayerCanTeleportInSeconds;
        } else {
            return 0;
        }
    }
}
