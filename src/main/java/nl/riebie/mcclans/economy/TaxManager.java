package nl.riebie.mcclans.economy;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.persistence.TaskForwarder;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.EconomyUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kippers on 17-4-2017.
 */
public class TaxManager {

    private static TaxManager instance;

    private final Runnable taxRunnable = new Runnable() {
        @Override
        public void run() {
            triggerTaxEvent();
        }
    };

    public TaxManager get() {
        if (instance == null) {
            instance = new TaxManager();
        }
        return instance;
    }

    public void init() {
        int interval = Config.getInteger(Config.CLAN_TAX_INTERVAL_SECONDS);

        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.execute(taxRunnable);
        taskBuilder.delay(interval, TimeUnit.SECONDS).interval(interval, TimeUnit.SECONDS).submit(MCClans.getPlugin());

        MCClans.getPlugin().getLogger().info("Registered tax task to run every " + interval + "s (" + TimeUnit.HOURS.convert(interval, TimeUnit.SECONDS) + "h), starting in " + interval + "s", false);
    }

    public void triggerTaxEvent() {
        List<Clan> clans = ClansImpl.getInstance().getClans();
        TaxEvent taxEvent = new TaxEvent(Cause.of(NamedCause.source(MCClans.getPlugin())), clans);
        double cost = Config.getDouble(Config.CLAN_TAX_COST);
        if (cost > 0) {
            for (Clan clan : clans) {
                double clanCost = Config.getBoolean(Config.CLAN_TAX_PER_MEMBER) ? clan.getMemberCount() * cost : cost;
                taxEvent.addTax(clan, new Tax("Clan upkeep", clanCost));
            }
        }
        if (Sponge.getEventManager().post(taxEvent)) {
            processTaxEvent(taxEvent);
        }
    }

    private void processTaxEvent(TaxEvent taxEvent) {
        for (ClanImpl clan : ClansImpl.getInstance().getClanImpls()) {
            List<Tax> taxes = taxEvent.getTax(clan);
            if (taxes.isEmpty()) {
                continue;
            }

            processTaxEventForClan(clan, taxes);
        }
    }

    // TODO messages
    private void processTaxEventForClan(ClanImpl clan, List<Tax> taxes) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;

        double bill = taxTotal(taxes);
        if (bill == 0) {
            return;
        }

        double memberBill = clan.getBank().getMemberFee() == -1 ? bill / clan.getMemberCount() : clan.getBank().getMemberFee();
        for (ClanPlayerImpl clanPlayer : clan.getMembersImpl()) {
            Optional<UniqueAccount> accountOpt = economyService.getOrCreateAccount(clanPlayer.getUUID());
            if (!accountOpt.isPresent()) {
                clanPlayer.sendMessage(Text.of("You failed to pay the clan tax of $" + memberBill));
                clanPlayer.getEconomyStats().addDebt(memberBill);
                TaskForwarder.sendUpdateClanPlayer(clanPlayer);
                continue;
            }
            UniqueAccount account = accountOpt.get();

            if (EconomyUtils.withdraw(account, currency, memberBill)) {
                bill -= memberBill;
                clanPlayer.sendMessage(Text.of("You paid the clan tax of $" + memberBill));
                clanPlayer.getEconomyStats().addTax(memberBill);
            } else {
                double balance = account.getBalance(currency).doubleValue();
                if (balance > 0) {
                    if (EconomyUtils.withdraw(account, currency, balance)) {
                        bill -= balance;
                        clanPlayer.sendMessage(Text.of("You failed to pay the clan tax of $" + memberBill + ", being short $" + (memberBill - balance)));
                        clanPlayer.getEconomyStats().addTax(balance);
                        clanPlayer.getEconomyStats().addDebt(memberBill - balance);
                        TaskForwarder.sendUpdateClanPlayer(clanPlayer);
                        continue;
                    }
                }
                clanPlayer.sendMessage(Text.of("You failed to pay the clan tax of $" + memberBill));
                clanPlayer.getEconomyStats().addDebt(memberBill);
            }
            TaskForwarder.sendUpdateClanPlayer(clanPlayer);
        }

        if (bill >= 0.01) {
            if (!clan.getBank().withdraw(bill)) {
                double balance = clan.getBank().getBalance();
                if (balance > 0) {
                    if (clan.getBank().withdraw(balance)) {
                        bill -= balance;
                    }
                }
                clan.getBank().addDebt(bill);
                TaskForwarder.sendUpdateClan(clan);

                clan.sendMessage(Text.of("Your clan failed to pay the tax of $" + taxTotal(taxes) + ", and is now $" + bill + " in debt"));
            }
        } else {
            clan.sendMessage(Text.of("Your clan paid the tax of $" + taxTotal(taxes)));
        }
    }

    private static double taxTotal(List<Tax> taxes) {
        double bill = 0;
        for (Tax tax : taxes) {
            bill += tax.getCost();
        }
        return bill;
    }
}
