package nl.riebie.mcclans.economy;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.config.Config;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.scheduler.Task;

import java.util.List;
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
            List<Tax> tax = taxEvent.getTax(clan);
            if (tax.isEmpty()) {
                continue;
            }

// todo
        }
        // tax members
        // tax clan
    }
}
