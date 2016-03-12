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

package nl.riebie.mcclans.player;

import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.utils.EconomyUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Consumer;

/**
 * Created by Kippers on 19-1-2016.
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
                        boolean success = EconomyUtils.withdraw(clanPlayer.getUUID(), teleportCost);
                        String currencyName = MCClans.getPlugin().getServiceHelper().currency.getDisplayName().toPlain();
                        if (success) {
                            teleport(player, clanPlayer, teleportLocation);
                            if (teleportCost != 0) {
                                Messages.sendYouWereChargedCurrency(player, teleportCost, currencyName);
                            }
                        } else {
                            Messages.sendYouDoNotHaveEnoughCurrency(player, teleportCost, currencyName);
                        }
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
