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

package nl.riebie.mcclans.commands.implementations;

import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.commands.constraints.PositiveNumberConstraint;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.EconomyUtils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Created by Kippers on 30/04/2016.
 */
public class ClanBankCommands {

    @Command(name = "balance", description = "View the balance of the clan bank", isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.bank.balance")
    public void clanBankBalanceCommand(CommandSource sender, ClanPlayerImpl clanPlayer) {
        if (Config.getBoolean(Config.USE_ECONOMY)) {
            ClanImpl clan = clanPlayer.getClan();
            Currency currency = MCClans.getPlugin().getServiceHelper().currency;
            Optional<Account> accountOpt = MCClans.getPlugin().getServiceHelper().economyService.getOrCreateAccount(clan.getBankId());
            if (accountOpt.isPresent()) {
                Account account = accountOpt.get();
                BigDecimal balance = account.getBalance(currency);
                Messages.sendClanBankBalance(sender, balance.doubleValue(), currency.getDisplayName().toPlain());
            } else {
                Messages.sendWarningMessage(sender, Messages.NO_ECONOMY_ACCOUNT_FOUND);
            }
        } else {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
        }
    }

    @Command(name = "deposit", description = "Deposit currency in the clan bank", isPlayerOnly = true, isClanOnly = true, clanPermission = "deposit", spongePermission = "mcclans.user.bank.deposit")
    public void clanBankDepositCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "amount", constraint = PositiveNumberConstraint.class) double amount) {
        if (Config.getBoolean(Config.USE_ECONOMY)) {
            ClanImpl clan = clanPlayer.getClan();
            boolean success = EconomyUtils.transferToBank(clan.getBankId(), clanPlayer.getUUID(), amount);
            String currencyName = MCClans.getPlugin().getServiceHelper().currency.getDisplayName().toPlain();
            if (success) {
                Messages.sendClanBroadcastMessageDepositedInClanBank(clan, sender.getName(), sender, amount, currencyName);
            } else {
                Messages.sendYouDoNotHaveEnoughCurrency(sender, amount, currencyName);
            }
        } else {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
        }
    }

    @Command(name = "withdraw", description = "Withdraw currency from the clan bank", isPlayerOnly = true, isClanOnly = true, clanPermission = "withdraw", spongePermission = "mcclans.user.bank.withdraw")
    public void clanBankWithdrawCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "amount", constraint = PositiveNumberConstraint.class) double amount) {
        if (Config.getBoolean(Config.USE_ECONOMY)) {
            ClanImpl clan = clanPlayer.getClan();
            boolean success = EconomyUtils.transferFromBank(clan.getBankId(), clanPlayer.getUUID(), amount);
            String currencyName = MCClans.getPlugin().getServiceHelper().currency.getDisplayName().toPlain();
            if (success) {
                Messages.sendClanBroadcastMessageWithdrewFromClanBank(clan, sender.getName(), sender, amount, currencyName);
            } else {
                Messages.sendNotEnoughCurrencyOnClanBank(sender, amount, currencyName);
            }
        } else {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
        }
    }
}
