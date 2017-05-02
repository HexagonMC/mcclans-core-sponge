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
import nl.riebie.mcclans.commands.Fee;
import nl.riebie.mcclans.commands.annotations.Command;
import nl.riebie.mcclans.commands.annotations.PageParameter;
import nl.riebie.mcclans.commands.annotations.Parameter;
import nl.riebie.mcclans.commands.constraints.PositiveNumberConstraint;
import nl.riebie.mcclans.comparators.BankStatsComparator;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.persistence.TaskForwarder;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.player.EconomyStats;
import nl.riebie.mcclans.table.HorizontalTable;
import nl.riebie.mcclans.utils.EconomyUtils;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Created by Kippers on 30/04/2016.
 */
public class ClanBankCommands {

    @Command(name = "balance", description = "View the balance of the clan bank", isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.bank.balance")
    public void clanBankBalanceCommand(CommandSource sender, ClanPlayerImpl clanPlayer) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
            return;
        }

        ClanImpl clan = clanPlayer.getClan();
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Optional<Account> accountOpt = MCClans.getPlugin().getServiceHelper().economyService.getOrCreateAccount(clan.getBankId());
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            BigDecimal balance = account.getBalance(currency);
            Messages.sendClanBankBalance(sender, balance.doubleValue(), clan.getBank().getDebt(), currency.getDisplayName().toPlain());
        } else {
            Messages.sendWarningMessage(sender, Messages.NO_ECONOMY_ACCOUNT_FOUND);
        }
    }

    @Command(name = "deposit", description = "Deposit currency in the clan bank", isPlayerOnly = true, isClanOnly = true, clanPermission = "deposit", spongePermission = "mcclans.user.bank.deposit")
    public void clanBankDepositCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "amount", constraint = PositiveNumberConstraint.class) double amount) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
            return;
        }

        ClanImpl clan = clanPlayer.getClan();
        double clanDebt = clan.getBank().getDebt();
        String currencyName = MCClans.getPlugin().getServiceHelper().currency.getDisplayName().toPlain();
        if (clanDebt <= 0) {
            boolean success = EconomyUtils.transferToBank(clan.getBankId(), clanPlayer.getUUID(), amount);
            if (success) {
                updateClanPlayerEconomyStatsDeposit(clanPlayer, amount);
                Messages.sendClanBroadcastMessageDepositedInClanBank(clan, sender.getName(), sender, amount, currencyName);
            } else {
                Messages.sendYouDoNotHaveEnoughCurrency(sender, amount, currencyName);
            }
        } else {
            if (EconomyUtils.withdraw(clanPlayer.getUUID(), amount)) {
                if (amount >= clanDebt) {
                    clan.getBank().setDebt(0);
                    clan.getBank().deposit(amount - clanDebt);
                } else {
                    clan.getBank().addDebt(-amount);
                }
                updateClanPlayerEconomyStatsDeposit(clanPlayer, amount);
                Messages.sendClanBroadcastMessageDepositedInClanBank(clan, sender.getName(), sender, amount, currencyName);
                TaskForwarder.sendUpdateClan(clan);

            } else {
                Messages.sendYouDoNotHaveEnoughCurrency(sender, amount, currencyName);
            }
        }
    }

    private void updateClanPlayerEconomyStatsDeposit(ClanPlayerImpl clanPlayer, double amount) {
        if (amount == 0) {
            return;
        }

        EconomyStats economyStats = clanPlayer.getEconomyStats();
        double playerDebt = economyStats.getDebt();
        if (playerDebt > 0) {
            if (amount >= playerDebt) {
                economyStats.setDebt(0);
                economyStats.addTax(playerDebt);
                economyStats.addDeposit(amount - playerDebt);
            } else {
                economyStats.addDebt(-amount);
                economyStats.addTax(amount);
            }
        } else {
            economyStats.addDeposit(amount);
        }
        TaskForwarder.sendUpdateClanPlayer(clanPlayer);
    }

    @Command(name = "withdraw", description = "Withdraw currency from the clan bank", isPlayerOnly = true, isClanOnly = true, clanPermission = "withdraw", spongePermission = "mcclans.user.bank.withdraw")
    public void clanBankWithdrawCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "amount", constraint = PositiveNumberConstraint.class) double amount) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
            return;
        }

        ClanImpl clan = clanPlayer.getClan();
        boolean success = EconomyUtils.transferFromBank(clan.getBankId(), clanPlayer.getUUID(), amount);
        String currencyName = MCClans.getPlugin().getServiceHelper().currency.getDisplayName().toPlain();
        if (success) {
            updateClanPlayerEconomyStatsWithdraw(clanPlayer, amount);
            Messages.sendClanBroadcastMessageWithdrewFromClanBank(clan, sender.getName(), sender, amount, currencyName);
        } else {
            Messages.sendNotEnoughCurrencyOnClanBank(sender, amount, currencyName);
        }
    }

    private void updateClanPlayerEconomyStatsWithdraw(ClanPlayerImpl clanPlayer, double amount) {
        if (amount == 0) {
            return;
        }

        clanPlayer.getEconomyStats().addWithdraw(amount);
        TaskForwarder.sendUpdateClanPlayer(clanPlayer);
    }

    @Command(name = "fee", description = "Set the member fee", isPlayerOnly = true, isClanOnly = true, clanPermission = "fee", spongePermission = "mcclans.user.bank.fee")
    public void clanBankFeeCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @Parameter(name = "amount", constraint = PositiveNumberConstraint.class) Fee fee) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
            return;
        }
        double amount = Utils.round(fee.value, 2);

        ClanImpl clan = clanPlayer.getClan();
        clan.getBank().setMemberFee(amount);
        TaskForwarder.sendUpdateClan(clan);
        // TODO messages
        if (amount == -1) {
            clan.sendMessage(Text.of(clanPlayer.getName() + " set the member fee to share the tax bill"));
        } else {
            clan.sendMessage(Text.of(clanPlayer.getName() + " set the member fee to $" + amount));
        }
    }

    @Command(name = "stats", description = "See the bank stats of a clan's members", isPlayerOnly = true, isClanOnly = true, spongePermission = "mcclans.user.bank.stats")
    public void clanBankStatsCommand(CommandSource sender, ClanPlayerImpl clanPlayer, @PageParameter int page) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
            return;
        }

        printBankStats(sender, clanPlayer.getClan(), page);
    }

    private void printBankStats(CommandSource commandSource, ClanImpl clan, int page) {
        List<ClanPlayerImpl> members = clan.getMembersImpl();
        members.sort(new BankStatsComparator());

        HorizontalTable<ClanPlayerImpl> table = new HorizontalTable<>("Bank statistics " + clan.getName(), 10,
                (row, member, index) -> {
                    row.setValue("Player", Text.of(member.getName()));
                    EconomyStats stats = member.getEconomyStats();
                    row.setValue("Deposit", Text.of(Utils.round(stats.getDeposit(), 2)));
                    row.setValue("Withdraw", Text.builder(String.valueOf(Utils.round(stats.getWithdraw(), 2))).color(TextColors.RED).build());
                    row.setValue("Tax", Text.of(Utils.round(stats.getTax(), 2)));
                    row.setValue("Debt", Text.builder(String.valueOf(Utils.round(stats.getDebt(), 2))).color(TextColors.RED).build());
                });
        table.defineColumn("Player", 20, true);
        table.defineColumn("Deposit", 15);
        table.defineColumn("Withdraw", 15);
        table.defineColumn("Tax", 15);
        table.defineColumn("Debt", 15);

        table.draw(members, page, commandSource);
    }

    @Command(name = "resetstats", description = "Reset the bank statistics", isPlayerOnly = true, isClanOnly = true, clanPermission = "resetbankstats", spongePermission = "mcclans.user.bank.resetstats")
    public void clanBankResetStatsCommand(CommandSource sender, ClanPlayerImpl clanPlayer) {
        if (!Config.getBoolean(Config.USE_ECONOMY)) {
            Messages.sendWarningMessage(sender, Messages.ECONOMY_USAGE_IS_CURRENTLY_DISABLED);
            return;
        }

        for (ClanPlayerImpl clanMember : clanPlayer.getClan().getMembersImpl()) {
            clanMember.resetEconomyStats();
        }
        // TODO messages
        sender.sendMessage(Text.of("Clan bank statistics reset"));
    }
}
