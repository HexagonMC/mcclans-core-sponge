package nl.riebie.mcclans.utils;

import nl.riebie.mcclans.MCClans;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Koen on 05/03/2016.
 */
public class EconomyUtils {

    public static boolean withdraw(UUID uuid, double charge) {
        if (charge == 0) {
            return true;
        }

        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Optional<UniqueAccount> accountOpt = economyService.getAccount(uuid);
        if (!accountOpt.isPresent()) {
            accountOpt = economyService.createAccount(uuid);
        }
        if (!accountOpt.isPresent()) {
            return false;
        }

        UniqueAccount account = accountOpt.get();
        TransactionResult result = account.withdraw(currency, BigDecimal.valueOf(charge), Cause.of(MCClans.getPlugin()));
        return (result.getResult().equals(ResultType.SUCCESS));
    }
}
