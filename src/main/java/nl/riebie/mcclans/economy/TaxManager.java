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

package nl.riebie.mcclans.economy;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.Tax;
import nl.riebie.mcclans.api.events.ClanTaxEvent;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.persistence.TaskForwarder;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.EconomyUtils;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    public static TaxManager get() {
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

    private void triggerTaxEvent() {
        List<Clan> clans = ClansImpl.getInstance().getClans();
        ClanTaxEvent taxEvent = new ClanTaxEvent(Cause.of(NamedCause.source(MCClans.getPlugin())), clans);
        double cost = Config.getDouble(Config.CLAN_TAX_COST);
        if (cost > 0) {
            for (Clan clan : clans) {
                double upkeep = Config.getBoolean(Config.CLAN_TAX_PER_MEMBER) ? clan.getMemberCount() * cost : cost;
                taxEvent.addTax(clan, new Tax("Clan upkeep", upkeep));
            }
        }
        if (!Sponge.getEventManager().post(taxEvent)) {
            processTaxEvent(taxEvent);
        }
    }

    private void processTaxEvent(ClanTaxEvent taxEvent) {
        double taxTotal = 0;
        List<ClanImpl> debtList = new ArrayList<>();
        List<ClanImpl> bankruptList = new ArrayList<>();
        List<ClanImpl> clans = ClansImpl.getInstance().getClanImpls();
        for (ClanImpl clan : clans) {
            List<Tax> taxes = taxEvent.getTax(clan);
            if (taxes.isEmpty()) {
                continue;
            } else {
                taxTotal += taxTotal(taxes);
            }

            if (isDebtOverMaximum(clan.getBank().getDebt())) {
                bankruptList.add(clan);
            } else {
                processTaxEventForClan(clan, taxes);
                if (clan.getBank().getDebt() > 0) {
                    debtList.add(clan);
                }
            }
        }

        sendGlobalTaxLog(debtList, bankruptList, clans.size(), taxTotal);
        sendIndividualTaxLog(taxEvent, clans, bankruptList);

        for (ClanImpl clan : bankruptList) {
            bankruptClan(clan);
        }
    }

    private boolean isDebtOverMaximum(double debt) {
        double maxDebt = Config.getDouble(Config.CLAN_TAX_MAXIMUM_DEBT);
        return maxDebt != -1 && debt > maxDebt;
    }

    // TODO messages
    private void bankruptClan(ClanImpl clan) {
        clan.sendMessage(Text.builder("Your clan has gone bankrupt!").color(TextColors.RED).build());
        ClansImpl.getInstance().disbandClan(clan);
    }

    private void processTaxEventForClan(ClanImpl clan, List<Tax> taxes) {
        double bill = taxTotal(taxes);
        if (bill == 0) {
            return;
        }

        double remainingBill = chargeClanMembers(bill, clan);
        chargeClan(clan, remainingBill);
    }

    private double chargeClanMembers(double bill, ClanImpl clan) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;

        double memberBill = getMemberBill(clan, bill);
        if (memberBill == 0) {
            return bill;
        }

        for (ClanPlayerImpl clanPlayer : clan.getMembersImpl()) {
            Optional<UniqueAccount> accountOpt = economyService.getOrCreateAccount(clanPlayer.getUUID());
            if (!accountOpt.isPresent()) {
                clanPlayer.getEconomyStats().addDebt(memberBill);
                TaskForwarder.sendUpdateClanPlayer(clanPlayer);
                continue;
            }
            UniqueAccount account = accountOpt.get();

            if (EconomyUtils.withdraw(account, currency, memberBill)) {
                bill -= memberBill;
                clanPlayer.getEconomyStats().addTax(memberBill);
            } else {
                double balance = account.getBalance(currency).doubleValue();
                if (balance > 0) {
                    if (EconomyUtils.withdraw(account, currency, balance)) {
                        bill -= balance;
                        clanPlayer.getEconomyStats().addTax(balance);
                        clanPlayer.getEconomyStats().addDebt(memberBill - balance);
                        TaskForwarder.sendUpdateClanPlayer(clanPlayer);
                        continue;
                    }
                }
                clanPlayer.getEconomyStats().addDebt(memberBill);
            }
            TaskForwarder.sendUpdateClanPlayer(clanPlayer);
        }
        return bill;
    }

    private double getMemberBill(ClanImpl clan, double bill) {
        return Utils.round(clan.getBank().getMemberFee() == -1 ? bill / clan.getMemberCount() : clan.getBank().getMemberFee(), 2);
    }


    private void chargeClan(ClanImpl clan, double remainingBill) {
        if (remainingBill > 0.1) {
            if (!clan.getBank().withdraw(remainingBill)) {
                double balance = clan.getBank().getBalance();
                if (balance > 0) {
                    if (clan.getBank().withdraw(balance)) {
                        remainingBill -= balance;
                    }
                }
                clan.getBank().addDebt(remainingBill);
                TaskForwarder.sendUpdateClan(clan);
            }
        } else if (remainingBill < 0.1) {
            double clanDebt = clan.getBank().getDebt();
            double amount = -remainingBill;
            if (amount >= clanDebt) {
                clan.getBank().setDebt(0);
                clan.getBank().deposit(amount - clanDebt);
            } else {
                clan.getBank().addDebt(-amount);
            }
            TaskForwarder.sendUpdateClan(clan);
        }
    }

    // TODO messages
    private void sendGlobalTaxLog(List<ClanImpl> debtList, List<ClanImpl> bankruptList, int clanCount, double taxTotal) {
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Text.Builder taxLogBuilder = Text.join(
                Text.NEW_LINE,
                Text.builder("== ").color(TextColors.DARK_GRAY).build(),
                Text.builder("MC").color(TextColors.DARK_GREEN).build(),
                Text.builder("Clans").color(TextColors.GREEN).build(),
                Text.of(" Clan Tax "),
                Text.builder(" ==").color(TextColors.DARK_GRAY).build(),
                Text.NEW_LINE
        ).toBuilder();

        if (clanCount == 0) {
            taxLogBuilder.append(
                    Text.builder("There are no clans to tax").color(TextColors.GRAY).build()
            );
        } else {
            taxLogBuilder.append(
                    Text.builder("Total tax bill ").color(TextColors.GRAY).build(),
                    Text.builder(Utils.round(taxTotal, 2) + " ").color(TextColors.RESET).build(),
                    Utils.getDisplayName(currency, taxTotal).toBuilder().color(TextColors.GRAY).build(),
                    Text.builder(" for ").color(TextColors.GRAY).build(),
                    Text.builder(String.valueOf(clanCount)).color(TextColors.RESET).build(),
                    Text.builder(" clan" + (clanCount == 1 ? "" : "s")).color(TextColors.GRAY).build()
            );
        }

        if (!debtList.isEmpty()) {
            taxLogBuilder.append(
                    Text.NEW_LINE,
                    Text.builder("Clans in debt: ").color(TextColors.GRAY).build(),
                    Text.joinWith(Text.of(", "), debtList.stream().map(clan ->
                            Text.join(
                                    clan.getTagColored(),
                                    Text.builder(" (").color(TextColors.GRAY).build(),
                                    Text.builder(String.valueOf(Utils.round(clan.getBank().getDebt(), 2))).color(TextColors.RESET).build(),
                                    Text.builder(")").color(TextColors.GRAY).build()
                            )
                    ).collect(Collectors.toList()))
            ).build();
        }

        if (!bankruptList.isEmpty()) {
            taxLogBuilder.append(
                    Text.NEW_LINE,
                    Text.builder("Bankrupted clans: ").color(TextColors.GRAY).build(),
                    Text.joinWith(Text.of(", "), bankruptList.stream().map(ClanImpl::getTagColored).collect(Collectors.toList()))
            ).build();
        }

        taxLogBuilder.append(
                Text.NEW_LINE,
                Text.builder("Next tax event in ").color(TextColors.DARK_GREEN).build(),
                Utils.formatTime(Config.getInteger(Config.CLAN_TAX_INTERVAL_SECONDS), TextColors.DARK_GREEN, TextColors.GREEN)
        ).build();

        Text taxLog = taxLogBuilder.append(Text.NEW_LINE).build();

        Sponge.getServer().getBroadcastChannel().send(taxLog);
        MCClans.getPlugin().getLogger().local(taxLog.toPlain());
    }

    // TODO messages
    private void sendIndividualTaxLog(ClanTaxEvent taxEvent, List<ClanImpl> clans, List<ClanImpl> bankruptList) {
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        for (ClanImpl clan : clans) {
            List<Tax> clanTax = taxEvent.getTax(clan);
            double clanBill = Utils.round(taxTotal(clanTax), 2);
            for (ClanPlayerImpl clanPlayer : clan.getMembersImpl()) {
                double debt = clanPlayer.getEconomyStats().getDebt();
                if (debt > 0) {
                    clanPlayer.sendMessage(
                            Text.join(
                                    Text.builder("You are ").color(TextColors.RED).build(),
                                    Text.builder(String.valueOf(debt) + " ").color(TextColors.WHITE).build(),
                                    Utils.getDisplayName(currency, debt).toBuilder().color(TextColors.RED).build(),
                                    Text.builder(" in debt to your clan!").color(TextColors.RED).build()
                            )
                    );
                } else {
                    double memberBill = getMemberBill(clan, clanBill);
                    if (memberBill > 0) {
                        clanPlayer.sendMessage(
                                Text.join(
                                        Text.builder("You paid the clan fee of ").color(TextColors.DARK_GREEN).build(),
                                        Text.builder(String.valueOf(memberBill) + " ").color(TextColors.GREEN).build(),
                                        Utils.getDisplayName(currency, memberBill).toBuilder().color(TextColors.DARK_GREEN).build()
                                )
                        );
                    }
                }
            }
            double clanDebt = clan.getBank().getDebt();
            if (clanDebt > 0) {
                if (!bankruptList.contains(clan)) {
                    clan.sendMessage(
                            Text.join(
                                    Text.builder("Your clan is ").color(TextColors.RED).build(),
                                    Text.builder(String.valueOf(clanDebt) + " ").color(TextColors.WHITE).build(),
                                    Utils.getDisplayName(currency, clanDebt).toBuilder().color(TextColors.RED).build(),
                                    Text.builder(" in debt!").color(TextColors.RED).build()
                            )
                    );
                }
            } else {
                Text.Builder taxLog = Text.join(
                        Text.builder("Your clan paid the clan tax of ").color(TextColors.DARK_GREEN).build(),
                        Text.builder(String.valueOf(clanBill) + " ").color(TextColors.GREEN).build(),
                        Utils.getDisplayName(currency, clanBill).toBuilder().color(TextColors.DARK_GREEN).build()
                ).toBuilder();

                if (clanTax.size() > 1) {
                    for (Tax tax : clanTax) {
                        taxLog.append(
                                Text.NEW_LINE,
                                Text.builder(" - " + tax.getName() + ": ").color(TextColors.DARK_GREEN).build(),
                                Text.builder(String.valueOf(Utils.round(tax.getCost(), 2)) + " ").color(TextColors.GREEN).build(),
                                Utils.getDisplayName(currency, tax.getCost()).toBuilder().color(TextColors.DARK_GREEN).build()
                        );
                    }
                }

                clan.sendMessage(taxLog.build());
            }
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
