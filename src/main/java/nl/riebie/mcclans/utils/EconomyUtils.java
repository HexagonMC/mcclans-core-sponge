/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.utils;

import nl.riebie.mcclans.MCClans;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Kippers on 05/03/2016.
 */
public class EconomyUtils {

    public static double getBalance(String clanBankIdentifier) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Optional<Account> clanAccountOpt = economyService.getOrCreateAccount(clanBankIdentifier);
        if (!clanAccountOpt.isPresent()) {
            return 0;
        }

        return clanAccountOpt.get().getBalance(currency).doubleValue();
    }

    public static boolean withdraw(UUID uuid, double charge) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Optional<UniqueAccount> accountOpt = economyService.getOrCreateAccount(uuid);
        if (!accountOpt.isPresent()) {
            return false;
        }

        return withdraw(accountOpt.get(), currency, charge);
    }

    public static boolean withdraw(String clanBankIdentifier, Currency currency, double charge) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Optional<Account> clanAccountOpt = economyService.getOrCreateAccount(clanBankIdentifier);
        if (!clanAccountOpt.isPresent()) {
            return false;
        }

        return withdraw(clanAccountOpt.get(), currency, charge);
    }

    public static boolean withdraw(Account account, Currency currency, double charge) {
        if (charge == 0) {
            return true;
        }

        TransactionResult result = account.withdraw(
                currency,
                BigDecimal.valueOf(charge),
                Cause.builder().append("MCClans").build(EventContext.empty())
        );
        return (result.getResult().equals(ResultType.SUCCESS));
    }

    public static boolean deposit(UUID uuid, double charge) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Optional<UniqueAccount> accountOpt = economyService.getOrCreateAccount(uuid);
        if (!accountOpt.isPresent()) {
            return false;
        }

        return deposit(accountOpt.get(), currency, charge);
    }

    public static boolean deposit(String clanBankIdentifier, Currency currency, double charge) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Optional<Account> clanAccountOpt = economyService.getOrCreateAccount(clanBankIdentifier);
        if (!clanAccountOpt.isPresent()) {
            return false;
        }

        return deposit(clanAccountOpt.get(), currency, charge);
    }

    public static boolean deposit(Account account, Currency currency, double charge) {
        if (charge == 0) {
            return true;
        }

        TransactionResult result = account.deposit(
                currency,
                BigDecimal.valueOf(charge),
                Cause.builder().append("MCClans").build(EventContext.empty())
        );
        return (result.getResult().equals(ResultType.SUCCESS));
    }

    public static boolean transferToBank(String clanBankIdentifier, UUID clanMember, double charge) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Optional<UniqueAccount> memberAccountOpt = economyService.getOrCreateAccount(clanMember);
        if (!memberAccountOpt.isPresent()) {
            return false;
        }

        return transferToBank(clanBankIdentifier, currency, memberAccountOpt.get(), charge);
    }

    public static boolean transferToBank(String clanBankIdentifier, Currency currency, Account account, double charge) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Optional<Account> clanAccountOpt = economyService.getOrCreateAccount(clanBankIdentifier);
        if (!clanAccountOpt.isPresent()) {
            return false;
        }

        return transfer(account, clanAccountOpt.get(), currency, charge);
    }

    public static boolean transferFromBank(String clanBankIdentifier, UUID clanMember, double charge) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Optional<UniqueAccount> memberAccountOpt = economyService.getOrCreateAccount(clanMember);
        if (!memberAccountOpt.isPresent()) {
            return false;
        }

        return transferFromBank(clanBankIdentifier, currency, memberAccountOpt.get(), charge);
    }

    public static boolean transferFromBank(String clanBankIdentifier, Currency currency, Account account, double charge) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Optional<Account> clanAccountOpt = economyService.getOrCreateAccount(clanBankIdentifier);
        if (!clanAccountOpt.isPresent()) {
            return false;
        }

        return transfer(clanAccountOpt.get(), account, currency, charge);
    }

    private static boolean transfer(Account fromAccount, Account toAccount, Currency currency, double charge) {
        if (charge == 0) {
            return true;
        }

        TransactionResult result = fromAccount.transfer(
                toAccount,
                currency,
                BigDecimal.valueOf(charge),
                Cause.builder().append("MCClans").build(EventContext.empty())
        );
        return (result.getResult().equals(ResultType.SUCCESS));
    }
}
