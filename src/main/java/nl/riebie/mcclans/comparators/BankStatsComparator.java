package nl.riebie.mcclans.comparators;

import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.player.EconomyStats;

import java.util.Comparator;

/**
 * Created by k.volkers on 2-5-2017.
 */
public class BankStatsComparator implements Comparator<ClanPlayerImpl> {

    @Override
    public int compare(ClanPlayerImpl player, ClanPlayerImpl player2) {
        EconomyStats stats1 = player.getEconomyStats();
        EconomyStats stats2 = player2.getEconomyStats();

        if (stats1.getDebt() < stats2.getDebt()) {
            return 1;
        } else if (stats1.getDebt() > stats2.getDebt()) {
            return -1;
        }

        double player1Total = stats1.getDeposit() + stats1.getTax() - stats1.getWithdraw() - stats1.getDebt();
        double player2Total = stats2.getDeposit() + stats2.getTax() - stats2.getWithdraw() - stats2.getDebt();
        if (player1Total < player2Total) {
            return 1;
        } else if (player1Total > player2Total) {
            return -1;
        }
        return 0;
    }
}
