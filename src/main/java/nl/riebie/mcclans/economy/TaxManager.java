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
import nl.riebie.mcclans.messages.Messages;
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
                double clanCost = Config.getBoolean(Config.CLAN_TAX_PER_MEMBER) ? clan.getMemberCount() * cost : cost;
                taxEvent.addTax(clan, new Tax("Clan upkeep", clanCost));
            }
        }
        if (!Sponge.getEventManager().post(taxEvent)) {
            processTaxEvent(taxEvent);
        }
    }

    // TODO messages
    private void processTaxEvent(ClanTaxEvent taxEvent) {
        double taxTotal = 0;
        List<Text> debtLog = new ArrayList<>();
        List<Text> bankruptLog = new ArrayList<>();
        List<ClanImpl> clans = ClansImpl.getInstance().getClanImpls();
        int clanCount = clans.size();
        for (ClanImpl clan : clans) {
            List<Tax> taxes = taxEvent.getTax(clan);
            if (taxes.isEmpty()) {
                continue;
            } else {
                taxTotal += taxTotal(taxes);
            }

            // TODO clan debt could be solved by another round of clan tax, so give it another chance and only bankrupt if still in debt after
            if (clan.getBank().getDebt() > 0) {
                bankruptLog.add(clan.getTagColored());
                bankruptClan(clan);
            } else {
                double debt = processTaxEventForClan(clan, taxes);
                if (debt > 0) {
                    debtLog.add(
                            Text.join(
                                    clan.getTagColored(),
                                    Text.of(" "),
                                    Text.builder("(" + Utils.round(debt, 2) + ")").build()
                            )
                    );
                }
            }
        }

        // TODO move out into separate method
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
                    Text.builder("There are no clans to tax").build()
            );
        } else {
            taxLogBuilder.append(
                    Text.builder(clanCount + " clan").build(),
                    Text.builder(clanCount == 1 ? " is" : "s are").build(),
                    Text.builder(" billed " + Utils.round(taxTotal, 2) + " ").build(),
                    currency.getPluralDisplayName(),
                    Text.builder(" in total").build()
            );
        }

        if (!debtLog.isEmpty()) {
            taxLogBuilder.append(
                    Text.NEW_LINE,
                    Text.builder("Clans in debt: ").build(),
                    Text.joinWith(Text.of(", "), debtLog)
            ).build();
        }

        if (!bankruptLog.isEmpty()) {
            taxLogBuilder.append(
                    Text.NEW_LINE,
                    Text.builder("Bankrupted clans: ").build(),
                    Text.joinWith(Text.of(", "), bankruptLog)
            ).build();
        }

        taxLogBuilder.append(
                Text.NEW_LINE,
                Text.of("Next tax event in "),
                Utils.formatTime(Config.getInteger(Config.CLAN_TAX_INTERVAL_SECONDS), Messages.BASIC_CHAT_COLOR, Messages.BASIC_HIGHLIGHT)
        ).build();

        Text taxLog = taxLogBuilder.build();

        Sponge.getServer().getBroadcastChannel().send(taxLog);
        MCClans.getPlugin().getLogger().info(taxLog.toPlain(), true);
    }

    private void bankruptClan(ClanImpl clan) {
        clan.sendMessage(Text.of("Your clan has been disbanded for its debts!"));
        ClansImpl.getInstance().disbandClan(clan);
    }

    private double processTaxEventForClan(ClanImpl clan, List<Tax> taxes) {
        double bill = taxTotal(taxes);
        if (bill == 0) {
            return 0;
        }

        double remainingBill = chargeClanMembers(bill, clan);
        return chargeClan(clan, remainingBill, bill);
    }

    private double chargeClanMembers(double bill, ClanImpl clan) {
        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;

        double memberBill = Utils.round(clan.getBank().getMemberFee() == -1 ? bill / clan.getMemberCount() : clan.getBank().getMemberFee(), 2);
        if (memberBill == 0) {
            return bill;
        }

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
        return bill;
    }

    private double chargeClan(ClanImpl clan, double remainingBill, double totalBill) {
        if (remainingBill >= 0.1) {
            if (!clan.getBank().withdraw(remainingBill)) {
                double balance = clan.getBank().getBalance();
                if (balance > 0) {
                    if (clan.getBank().withdraw(balance)) {
                        remainingBill -= balance;
                    }
                }
                clan.getBank().addDebt(remainingBill);
                TaskForwarder.sendUpdateClan(clan);

                clan.sendMessage(Text.of("Your clan failed to pay the tax of $" + totalBill + ", and is now $" + remainingBill + " in debt"));
                return remainingBill;
            }
        } else if (remainingBill < 0.1) {
            // TODO undo debt / deposit to clan bank
        }
        clan.sendMessage(Text.of("Your clan paid the tax of $" + totalBill));
        return 0;
    }

    private static double taxTotal(List<Tax> taxes) {
        double bill = 0;
        for (Tax tax : taxes) {
            bill += tax.getCost();
        }
        return bill;
    }
}
