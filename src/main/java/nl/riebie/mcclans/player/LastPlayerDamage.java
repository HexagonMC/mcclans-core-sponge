package nl.riebie.mcclans.player;

import java.util.UUID;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class LastPlayerDamage {

    private UUID victimUUID;
    private UUID damagerUUID;
    long timeStamp;

    public LastPlayerDamage(UUID victimUUID, UUID damagerUUID) {
        this.victimUUID = victimUUID;
        this.damagerUUID = damagerUUID;
        timeStamp = System.currentTimeMillis();
    }

    public UUID getVictimUUID() {
        return victimUUID;
    }

    public UUID getDamagerUUID() {
        return damagerUUID;
    }

    public boolean isDamageExpired() {
        int secondsPassed = (int) ((System.currentTimeMillis() - timeStamp) / 1000);
        if (secondsPassed > 10) {
            return true;
        }
        return false;
    }
}
