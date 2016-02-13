package nl.riebie.mcclans.listeners;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

/**
 * Created by Koen on 13/02/2016.
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
