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

package nl.riebie.mcclans.listeners;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.api.enums.KillDeathFactor;
import nl.riebie.mcclans.events.EventDispatcher;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.player.KillDeathFactorHandler;
import nl.riebie.mcclans.player.LastPlayerDamage;
import nl.riebie.mcclans.utils.UUIDUtils;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DestructEntityEvent;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Kippers on 13/02/2016.
 */
public class KillDeathRatioListener {

    @Listener
    public void onDestructEntity(DestructEntityEvent.Death event) {
        if (!Config.getBoolean(Config.LOG_PLAYER_KDR)) {
            return;
        }
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) event.getTargetEntity();
        Player killer = null;
        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
        if (optDamageSource.isPresent()) {
            EntityDamageSource damageSource = optDamageSource.get();
            Entity entityKiller = damageSource.getSource();
            if (entityKiller instanceof Player) {
                killer = (Player) entityKiller;
            }
        }

        if (Utils.isWorldBlockedFromLoggingPlayerKDR(victim.getWorld().getName())) {
            return;
        }

        ClanPlayerImpl victimClanPlayer = ClansImpl.getInstance().getClanPlayer(victim.getUniqueId());
        if (victimClanPlayer == null) {
            victimClanPlayer = ClansImpl.getInstance().createClanPlayer(victim.getUniqueId(), victim.getName());
        }

        UUID killerUUID = null;
        if (killer == null) {
            LastPlayerDamage lastPlayerDamage = victimClanPlayer.getLastPlayerDamage();
            if (lastPlayerDamage != null && !lastPlayerDamage.isDamageExpired() && isValidDeathCauseWhenNoKiller()) {
                killerUUID = lastPlayerDamage.getDamagerUUID();
            }
        } else {
            killerUUID = killer.getUniqueId();
        }

        if (killerUUID == null) {
            return;
        }

        ClanPlayerImpl killerClanPlayer = ClansImpl.getInstance().getClanPlayer(killerUUID);
        if (killerClanPlayer == null) {
            String killerName = UUIDUtils.getName(killerUUID);
            if (killerName == null) {
                return;
            }
            killerClanPlayer = ClansImpl.getInstance().createClanPlayer(killerUUID, killerName);
        }

        if (killerClanPlayer.equals(victimClanPlayer)) {
            return;
        }

        if (killerClanPlayer.getClan() == null || victimClanPlayer.getClan() == null || !killerClanPlayer.getClan().isPlayerFriendlyToThisClan(victimClanPlayer)) {
            KillDeathFactor killFactor = KillDeathFactorHandler.getInstance().getKillFactor(killerClanPlayer, victimClanPlayer);
            KillDeathFactor deathFactor = KillDeathFactorHandler.getInstance().getDeathFactor(killerClanPlayer, victimClanPlayer);

            killerClanPlayer.getKillDeath().addKill(killFactor);
            victimClanPlayer.getKillDeath().addDeath(deathFactor);

            EventDispatcher.getInstance().dispatchClanPlayerKillEvent(killerClanPlayer, victimClanPlayer);
        }
    }

    private boolean isValidDeathCauseWhenNoKiller() {
        // TODO SPONGE no way to check damage cause is there? for now approve all
        return true;
//        if (entityDamageEvent != null) {
//            DamageCause damageCause = entityDamageEvent.getCause();
//            switch (damageCause) {
//                case CONTACT:
//                    return true;
//                case DROWNING:
//                    return true;
//                case FALL:
//                    return true;
//                case FIRE:
//                    return true;
//                case FIRE_TICK:
//                    return true;
//                case LAVA:
//                    return true;
//                case THORNS:
//                    return true;
//                case VOID:
//                    break;
//                default:
//                    return false;
//
//            }
//        }
//        return false;
    }

}
