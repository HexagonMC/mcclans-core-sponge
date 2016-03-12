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
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;
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

    public static boolean withdraw(UUID uuid, double charge) {
        if (charge == 0) {
            return true;
        }

        EconomyService economyService = MCClans.getPlugin().getServiceHelper().economyService;
        Currency currency = MCClans.getPlugin().getServiceHelper().currency;
        Optional<UniqueAccount> accountOpt = economyService.getOrCreateAccount(uuid);
        if (!accountOpt.isPresent()) {
            return false;
        }

        UniqueAccount account = accountOpt.get();
        TransactionResult result = account.withdraw(
                currency,
                BigDecimal.valueOf(charge),
                Cause.of(NamedCause.of("MCClans", MCClans.getPlugin()))
        );
        return (result.getResult().equals(ResultType.SUCCESS));
    }
}
