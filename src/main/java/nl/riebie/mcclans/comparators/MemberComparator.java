package nl.riebie.mcclans.comparators;

import nl.riebie.mcclans.player.ClanPlayerImpl;

import java.util.Comparator;

/**
 * Created by K.Volkers on 23-2-2016.
 */
public class MemberComparator implements Comparator<ClanPlayerImpl> {

    @Override
    public int compare(ClanPlayerImpl player, ClanPlayerImpl player2) {
        if (player.getRank().getImportance() < player2.getRank().getImportance()) {
            return 1;
        } else if (player.getRank().getImportance() > player2.getRank().getImportance()) {
            return -1;
        }

        double player1Kdr = player.getKDR();
        double player2Kdr = player2.getKDR();
        if (player1Kdr < player2Kdr) {
            return 1;
        } else if (player1Kdr > player2Kdr) {
            return -1;
        }
        return 0;
    }

}
