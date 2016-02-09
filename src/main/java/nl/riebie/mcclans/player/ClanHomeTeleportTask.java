package nl.riebie.mcclans.player;

import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.messages.Messages;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.teleport.TeleportCause;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.security.auth.login.Configuration;
import java.util.function.Consumer;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanHomeTeleportTask implements Consumer<Task> {

    private Player player;
    private ClanPlayerImpl clanPlayer;
    private Location<World> teleportLocation;
    private Location<World> playerPreviousLocation;
    private double playerPreviousHealth;
    private int countdownSeconds;
    private boolean forceNoCurrency;

    public ClanHomeTeleportTask(Player player, ClanPlayerImpl clanPlayer, Location<World> teleportLocation, int countdownSeconds, boolean forceNoCurrency) {
        this.player = player;
        this.clanPlayer = clanPlayer;
        this.teleportLocation = teleportLocation;
        this.playerPreviousLocation = player.getLocation();
        this.playerPreviousHealth = player.health().get();
        this.countdownSeconds = countdownSeconds;
        this.forceNoCurrency = forceNoCurrency;
    }

    @Override
    public void accept(Task task) {
        if (player.isOnline()) {
            Location<World> playerCurrentLocation = player.getLocation();
            double playerCurrentHealth = player.health().get();
            if (!playerCurrentLocation.getExtent().getName().equals(playerPreviousLocation.getExtent().getName())
                    || playerCurrentLocation.getPosition().distance(playerPreviousLocation.getPosition()) > 0) {
                task.cancel();
                Messages.sendWarningMessage(player, Messages.TELEPORT_CANCELLED);
            } else if (playerCurrentHealth < playerPreviousHealth) {
                task.cancel();
                Messages.sendWarningMessage(player, Messages.TELEPORT_CANCELLED);
            } else {
                if (countdownSeconds <= 0) {
                    task.cancel();
                    if (Config.getBoolean(Config.USE_ECONOMY) && !forceNoCurrency) {
                        double teleportCost = Config.getDouble(Config.TELEPORT_COST);
                        // TODO SPONGE EconomyHandler missing
//                        String currencyName = EconomyHandler.getInstance().getCurrencyName();
//                        if (EconomyHandler.getInstance().enoughCurrency(clanPlayer.getName(), teleportCost)) {
//                            teleport(player, clanPlayer, teleportLocation);
//                            if (teleportCost != 0) {
//                                EconomyHandler.getInstance().chargePlayer(clanPlayer.getName(), teleportCost);
//                                Messages.sendYouWereChargedCurrency(player, teleportCost, currencyName);
//                            }
//                        } else {
//                            Messages.sendYouDoNotHaveEnoughCurrency(player, teleportCost, currencyName);
//                        }
                    } else {
                        teleport(player, clanPlayer, teleportLocation);
                    }
                } else {
                    Messages.sendTeleportingInXSeconds(player, countdownSeconds);
                }
                playerPreviousLocation = player.getLocation();
                countdownSeconds--;
            }
        } else {
            task.cancel();
        }
    }

    private void teleport(Player player, ClanPlayerImpl clanPlayer, Location<World> teleportLocation) {
        // TODO SPONGE rotation missing, and no teleport cause?
        player.setLocation(teleportLocation);
        clanPlayer.setLastClanHomeTeleport(new LastClanHomeTeleport());
    }
}
