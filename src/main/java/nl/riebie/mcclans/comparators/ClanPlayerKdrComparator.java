package nl.riebie.mcclans.comparators;

import nl.riebie.mcclans.player.ClanPlayerImpl;

import java.util.Comparator;

/**
 * Created by Koen on 24/02/2016.
 */
public class ClanPlayerKdrComparator implements Comparator<ClanPlayerImpl> {

    @Override
    public int compare(ClanPlayerImpl player, ClanPlayerImpl player2) {
        if (player.getKDR() < player2.getKDR()) {
            return 1;
        } else if (player.getKDR() > player2.getKDR()) {
            return -1;
        }
        return 0;
    }
}
