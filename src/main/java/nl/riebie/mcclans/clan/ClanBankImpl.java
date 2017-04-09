package nl.riebie.mcclans.clan;

import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.api.ClanBank;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.utils.EconomyUtils;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * Created by Kippers on 09/04/2017.
 */
public class ClanBankImpl implements ClanBank {

    private final String id;

    public ClanBankImpl(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    @Nullable
    public Currency getCurrency() {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return null;
        }

        return MCClans.getPlugin().getServiceHelper().currency;
    }

    @Override
    public boolean withdraw(BigDecimal bigDecimal) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return false;
        }

        return EconomyUtils.withdraw(id, getCurrency(), bigDecimal.doubleValue());
    }

    @Override
    public boolean deposit(BigDecimal bigDecimal) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return false;
        }

        return EconomyUtils.deposit(id, getCurrency(), bigDecimal.doubleValue());
    }

    @Override
    public boolean transferFromBank(Account account, BigDecimal bigDecimal) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return false;
        }

        return EconomyUtils.transferFromBank(id, getCurrency(), account, bigDecimal.doubleValue());
    }

    @Override
    public boolean transferToBank(Account account, BigDecimal bigDecimal) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return false;
        }

        return EconomyUtils.transferToBank(id, getCurrency(), account, bigDecimal.doubleValue());
    }

    @Override
    public BigDecimal getBalance() {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(EconomyUtils.getBalance(id));
    }
}
