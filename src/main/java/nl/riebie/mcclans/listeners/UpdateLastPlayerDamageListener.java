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
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

/**
 * Created by Kippers on 13/02/2016.
 */
public class UpdateLastPlayerDamageListener {

    @Listener(order = Order.LATE)
    public void onDamageEntity(DamageEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!(event.getTargetEntity() instanceof Player)) {
            return;
        }
        Optional<Player> damagerOpt = event.getCause().first(Player.class);
        if (!damagerOpt.isPresent()) {
            return;
        }

        Player victim = (Player) event.getTargetEntity();
        Player damager = damagerOpt.get();

        ClanPlayerImpl victimClanPlayer = ClansImpl.getInstance().getClanPlayer(victim.getUniqueId());

        if (victimClanPlayer == null) {
            victimClanPlayer = ClansImpl.getInstance().createClanPlayer(victim.getUniqueId(), victim.getName());
        }

        victimClanPlayer.setLastPlayerDamage(damager.getUniqueId());
    }

}
