package nl.riebie.mcclans.player;

import nl.riebie.mcclans.messages.Messages;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.teleport.TeleportCause;
import org.spongepowered.api.world.Location;

import javax.security.auth.login.Configuration;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanHomeTeleportTask {

}

// TODO SPONGE: The fockin thing is a BukkitRunnable...
//public class ClanHomeTeleportTask extends BukkitRunnable {
//
//    private Player player;
//    private ClanPlayerImpl clanPlayer;
//    private Location teleportLocation;
//    private Location playerPreviousLocation;
//    private double playerPreviousHealth;
//    private int countdownSeconds;
//    private boolean forceNoCurrency;
//
//    public ClanHomeTeleportTask(Player player, ClanPlayerImpl clanPlayer, Location teleportLocation, int countdownSeconds, boolean forceNoCurrency) {
//        this.player = player;
//        this.clanPlayer = clanPlayer;
//        this.teleportLocation = teleportLocation;
//        this.playerPreviousLocation = player.getLocation();
//        this.playerPreviousHealth = player.getHealth();
//        this.countdownSeconds = countdownSeconds;
//        this.forceNoCurrency = forceNoCurrency;
//    }
//
//    @Override
//    public void run() {
//        if (player.isOnline()) {
//            Location playerCurrentLocation = player.getLocation();
//            double playerCurrentHealth = player.getHealth();
//            if (!playerCurrentLocation.getWorld().getName().equals(playerPreviousLocation.getWorld().getName())
//                    || playerCurrentLocation.distance(playerPreviousLocation) > 0) {
//                super.cancel();
//                Messages.sendWarningMessage(player, Messages.TELEPORT_CANCELLED);
//            } else if (playerCurrentHealth < playerPreviousHealth) {
//                super.cancel();
//                Messages.sendWarningMessage(player, Messages.TELEPORT_CANCELLED);
//            } else {
//                if (countdownSeconds <= 0) {
//                    super.cancel();
//                    if (Configuration.useEconomy && !forceNoCurrency) {
//                        double teleportCost = Configuration.teleportCost;
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
//                    } else {
//                        teleport(player, clanPlayer, teleportLocation);
//                    }
//                } else {
//                    Messages.sendTeleportingInXSeconds(player, countdownSeconds);
//                }
//                playerPreviousLocation = player.getLocation();
//                countdownSeconds--;
//            }
//        } else {
//            super.cancel();
//        }
//    }
//
//    private void teleport(Player player, ClanPlayerImpl clanPlayer, Location teleportLocation) {
//        player.teleport(teleportLocation, TeleportCause.PLUGIN);
//        clanPlayer.setLastClanHomeTeleport(new LastClanHomeTeleport());
//    }
//}
