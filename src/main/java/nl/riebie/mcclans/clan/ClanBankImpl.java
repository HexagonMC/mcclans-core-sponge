package nl.riebie.mcclans.clan;

import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.api.ClanBank;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.utils.EconomyUtils;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;

import javax.annotation.Nullable;

/**
 * Created by Kippers on 09/04/2017.
 */
public class ClanBankImpl implements ClanBank {

    private final String id;

    private double debt;
    private double memberFee;

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
    public boolean withdraw(double amount) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return false;
        }

        return EconomyUtils.withdraw(id, getCurrency(), amount);
    }

    @Override
    public boolean deposit(double amount) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return false;
        }

        return EconomyUtils.deposit(id, getCurrency(), amount);
    }

    @Override
    public boolean transferFromBank(Account account, double amount) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return false;
        }

        return EconomyUtils.transferFromBank(id, getCurrency(), account, amount);
    }

    @Override
    public boolean transferToBank(Account account, double amount) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return false;
        }

        return EconomyUtils.transferToBank(id, getCurrency(), account, amount);
    }

    @Override
    public double getBalance() {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            return 0;
        }

        return EconomyUtils.getBalance(id);
    }

    public double getDebt() {
        return debt;
    }

    public void setDebt(double debt) {
        this.debt = debt;
    }

    public void addDebt(double debt) {
        setDebt(getDebt() + debt);
    }

    public double getMemberFee() {
        return memberFee;
    }

    public void setMemberFee(double memberFee) {
        this.memberFee = memberFee;
    }
}
