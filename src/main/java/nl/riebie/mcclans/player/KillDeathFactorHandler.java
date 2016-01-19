package nl.riebie.mcclans.player;

import nl.riebie.mcclans.enums.KillDeathFactor;

/**
 * Created by K.Volkers on 19-1-2016.
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
        double killerKdr = killer.getKDR();
        double victimKdr = victim.getKDR();
        return getKillFactor(killerKdr, victimKdr);
    }

    public KillDeathFactor getDeathFactor(ClanPlayerImpl killer, ClanPlayerImpl victim) {
        double killerKdr = killer.getKDR();
        double victimKdr = victim.getKDR();
        return getDeathFactor(killerKdr, victimKdr);
    }

}
