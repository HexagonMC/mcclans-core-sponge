package nl.riebie.mcclans.comparators;

import nl.riebie.mcclans.clan.ClanImpl;

import java.util.Comparator;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanKdrComparator implements Comparator<ClanImpl> {

    @Override
    public int compare(ClanImpl clan1, ClanImpl clan2) {
        double clan1KDR = clan1.getKDR();
        double clan2KDR = clan2.getKDR();
        if (clan1KDR < clan2KDR) {
            return 1;
        } else if (clan1KDR > clan2KDR) {
            return -1;
        }

        long clan1CreationTime = clan1.getCreationDate().getTime();
        long clan2CreationTime = clan2.getCreationDate().getTime();
        if (clan1CreationTime < clan2CreationTime) {
            return -1;
        } else if (clan1CreationTime > clan2CreationTime) {
            return 1;
        }
        return 0;
    }

}
