package nl.riebie.mcclans.player;

import nl.riebie.mcclans.config.Config;

import javax.security.auth.login.Configuration;

/**
 * Created by K.Volkers on 19-1-2016.
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
