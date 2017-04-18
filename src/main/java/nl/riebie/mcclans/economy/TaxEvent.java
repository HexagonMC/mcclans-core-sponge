package nl.riebie.mcclans.economy;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.events.ClanEvent;
import org.spongepowered.api.event.cause.Cause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO move to API
 * <p>
 * Created by k.volkers on 17-4-2017.
 */
public class TaxEvent extends ClanEvent {

    private final Map<Clan, List<Tax>> taxMap = new HashMap<>();

    public TaxEvent(Cause cause, List<Clan> clans) {
        super(cause);
        for (Clan clan : clans) {
            taxMap.put(clan, new ArrayList<>());
        }
    }

    public void addTax(Clan clan, Tax tax) {
        List<Tax> taxes = taxMap.get(clan);
        if (taxes == null) {
            taxes = new ArrayList<>();
            taxMap.put(clan, taxes);
        }
        taxes.add(tax);
    }

    public List<Tax> getTax(Clan clan) {
        return taxMap.getOrDefault(clan, new ArrayList<>());
    }

    public Map<Clan, List<Tax>> getTaxes() {
        return taxMap;
    }
}
