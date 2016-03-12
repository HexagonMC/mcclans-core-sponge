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
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.Utils;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

/**
 * Created by Kippers on 13/02/2016.
 */
public class FriendlyFireListener {

    @Listener
    public void onDamageEntity(DamageEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!Config.getBoolean(Config.ALLOW_FF_PROTECTION)) {
            return;
        }
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getTargetEntity();
        Player damager = null;
        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
        if (optDamageSource.isPresent()) {
            EntityDamageSource damageSource = optDamageSource.get();
            Entity entityDamager = damageSource.getSource();
            if (entityDamager instanceof Player) {
                damager = (Player) entityDamager;
            } else if (entityDamager instanceof Projectile) {
                Projectile projectile = (Projectile) entityDamager;
                ProjectileSource projectileSource = projectile.getShooter();
                if (projectileSource instanceof Player) {
                    damager = (Player) projectileSource;
                }
            }
        }
        if (damager == null) {
            return;
        }

        handleDamageEntity(event, victim, damager);
    }

    public void handleDamageEntity(DamageEntityEvent event, Player victim, Player damager) {
        ClanPlayerImpl victimClanPlayer = ClansImpl.getInstance().getClanPlayer(victim.getUniqueId());
        ClanPlayerImpl damagerClanPlayer = ClansImpl.getInstance().getClanPlayer(damager.getUniqueId());
        if (victimClanPlayer == null || damagerClanPlayer == null)
            return;
        if (victimClanPlayer.getClan() == null || damagerClanPlayer.getClan() == null)
            return;
        if (Utils.isWorldBlockedFromAllowingFriendlyFireProtection(victim.getWorld().getName())) {
            return;
        }
        if (victim.getUniqueId().equals(damager.getUniqueId())) {
            return;
        }

        ClanImpl victimPlayerClan = victimClanPlayer.getClan();
        ClanImpl damagerPlayerClan = damagerClanPlayer.getClan();
        if (victimPlayerClan.isPlayerFriendlyToThisClan(damagerClanPlayer)) {
            if (victimClanPlayer.isFfProtected() && victimPlayerClan.isFfProtected() && damagerPlayerClan.isFfProtected()) {
                event.setCancelled(true);
                Messages.sendWarningMessage(damager, Messages.FRIENDLY_FIRE_IS_OFF);
            }
        }
    }

}
